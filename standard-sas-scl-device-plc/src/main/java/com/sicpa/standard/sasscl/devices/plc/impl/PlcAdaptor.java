package com.sicpa.standard.sasscl.devices.plc.impl;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.plc.controller.IPlcController;
import com.sicpa.standard.plc.controller.IPlcControllerListener;
import com.sicpa.standard.plc.controller.PlcException;
import com.sicpa.standard.plc.controller.actions.PlcAction;
import com.sicpa.standard.plc.controller.model.IPlcModel;
import com.sicpa.standard.plc.controller.notification.IPlcNotificationListener;
import com.sicpa.standard.plc.driver.event.PlcEventArgs;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.controller.productionconfig.ConfigurationFailedException;
import com.sicpa.standard.sasscl.controller.productionconfig.IConfigurable;
import com.sicpa.standard.sasscl.controller.productionconfig.IConfigurator;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PlcConfig;
import com.sicpa.standard.sasscl.controller.view.event.LineSpeedEvent;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.plc.*;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.devices.plc.variable.EditablePlcVariables;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroup;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroupEvent;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcPulseVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.IPlcValuesLoader;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValuesForAllVar;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("rawtypes")
public class PlcAdaptor extends AbstractPlcAdaptor implements IPlcControllerListener,
		IConfigurable<PlcConfig, PlcAdaptor> {

	private static final Logger logger = LoggerFactory.getLogger(PlcAdaptor.class);

	protected IPlcController<? extends IPlcModel> controller;

	protected String plcConfigFolder = "config/plc";

	/**
	 * to get actions for specific PLCRequest in a particular production mode
	 */
	protected final Map<PlcRequest, IPlcRequestExecutor> plcRequestActionMap = new HashMap<PlcRequest, IPlcRequestExecutor>();

	/**
	 * PLC notifications (for instance line speed, good trigger & bad trigger etc) - excludes errors & warnings
	 */
	protected List<IPlcVariable<?>> notificationVariables = new ArrayList<IPlcVariable<?>>();

	protected final Collection<IPlcVariable> parameters = new ArrayList<IPlcVariable>();
	
	private Map<String, Short> systemTypes;
	
	private Map<String, Boolean> activeLines;
	
	private static final short SYSTEM_TYPE_TOBACCO = 3;

    protected PlcBrsStateListener brsStateListener;


    /**
	 * 
	 * @param plcVariablesMap
	 */
	public void setPlcVariablesNameMap(Map<String, String> plcVariablesMap) {
		PlcVariableMap.addPlcVariables(plcVariablesMap);
	}

	public PlcAdaptor() {
	}

    public void setBrsStateListener(PlcBrsStateListener brsStateListener) {
        this.brsStateListener = brsStateListener;
    }

	public PlcAdaptor(final IPlcController<?> controller) {
		this.controller = controller;
		controller.addListener(this);
		setName("PLC");
	}

	protected final AtomicBoolean notificationCreated = new AtomicBoolean(false);
	
	
	
	@Override
	protected void doConnect() throws PlcAdaptorException {

		try {
			controller.create();
			notificationCreated.compareAndSet(true, false);
			sendReloadPlcParametersRequest();

		} catch (PlcException e) {
			throw new PlcAdaptorException(e);
		}
	}

	protected void createNotifications() {

		if (notificationCreated.get()) {
			return;
		}
		notificationCreated.compareAndSet(false, true);
		if (notificationVariables == null) {
			return;
		}

		for (IPlcVariable<?> var : notificationVariables) {
			registerNotificationInternal(var);
		}
	}

	public void registerNotification(IPlcVariable<?> var) {
		if (!isConnected()) {
			// do it when connected
			notificationVariables.add(var);
			return;
		}
		registerNotificationInternal(var);
	}

	@SuppressWarnings({ "unchecked" })
	public void registerNotificationInternal(IPlcVariable<?> var) {
		controller.createNotification(var, new IPlcNotificationListener() {
			@Override
			public void onPlcNotify(final IPlcController sender, final IPlcVariable variable, final Object value) {
				firePlcEvent(new PlcEvent(variable.getVariableName(), value));
			}
		});
	}

	@Override
	protected void doDisconnect() {
		controller.shutdown();
		fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
	}

	/**
	 * Send start request <br>
	 * if some error are still on, stop, if no error create notification
	 */
	@Override
	public void doStart() throws PlcAdaptorException {

		if (!isConnected()) {
			throw new PlcAdaptorException("PLC is not connected");
		}

		executeRequest(PlcRequest.START);

		fireDeviceStatusChanged(DeviceStatus.STARTED);
	}
	
	@Override
	public void doRun() throws PlcAdaptorException {

		if (!isConnected()) {
			throw new PlcAdaptorException("PLC is not connected");
		}

		executeRequest(PlcRequest.RUN);

		fireDeviceStatusChanged(DeviceStatus.STARTED);
	}

	/**
	 * send stop request
	 */
	@Override
	public void doStop() throws PlcAdaptorException {

		if (!isConnected()) {
			throw new PlcAdaptorException("PLC is not connected");
		}
		executeRequest(PlcRequest.STOP);
		fireDeviceStatusChanged(DeviceStatus.STOPPED);

	}

	@Override
	public void onPlcEventReceived(final IPlcController<?> controller, final PlcEventArgs eventArgs) {

		logger.debug("{} - event status changed: {}", getName(), eventArgs.getEventCode().name());

		switch (eventArgs.getEventCode()) {
		case CONNECTED:
			onPlcConnected();
			break;
		case DISCONNECTED:
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
			break;
		default:
			break;
		}
	}

	
	
	protected void onPlcConnected() {
		sendAllParameters();
		addPlcListener(new IPlcListener() {			
			@Override
			public void onPlcEvent(PlcEvent event) {				
				handleEvent(event);
			}

			@Override
			public List<String> getListeningVariables() {
				List<String> result = new ArrayList<String>();
				result.addAll(PlcVariables.NTF_LINE_SPEED.getLineVariableNames());
				result.addAll(PlcVariables.NTF_PRODUCTS_FREQ.getLineVariableNames());
				logger.debug("getListeningVariables");
				return result;
			}
		});

		createNotifications();

		fireDeviceStatusChanged(DeviceStatus.CONNECTED);
	}

	protected void sendAllParameters() {
		for (IPlcVariable<?> var : parameters) {
			try {
				write(var);
			} catch (Exception e) {
				logger.error("failed to write plc param:" + var.getVariableName() + " value:" + var.getValue(), e);
			}
		}

        // Send Expected SKU to BRS
        if(this.brsStateListener != null) {
            try {
                brsStateListener.sendSkuConfig();
            } catch (Exception e) {
                logger.error("Failed to send sku config to BRS", e);
            }
        }

		try {
			sendReloadPlcParametersRequest();
		} catch (PlcAdaptorException e) {
			logger.error("", e);
		}
	}
	
	protected void handleEvent(final PlcEvent event) {
		
//		logger.debug("Event {} {}", event.getVarName(), event.getValue());

		String lineIndex = PlcVariableMap.getLineIndex(event.getVarName());
		
		if (!isLineActive(lineIndex)) {
			logger.debug("line not active");
			return;
		}
		
		int length = lineIndex.length()+ 1;

		String eventVarName = event.getVarName().substring(length);
		
		String freqVar = PlcVariables.NTF_PRODUCTS_FREQ.getVariableName().substring(length + 1);
		String speedVar = PlcVariables.NTF_LINE_SPEED.getVariableName().substring(length + 1);
		
		if (eventVarName.equals(freqVar) && isTobacco(event)) {
			handleProductFreqEvent(lineIndex, event);
		}
		else if(eventVarName.equals(speedVar) && !isTobacco(event)) {
			handleLineSpeedEvent(lineIndex, event);
		}
		
	}
	
	
	
	private void handleProductFreqEvent(String lineIndex, final PlcEvent event) {
		
		String value = String.valueOf(event.getValue()) + " PACKS/MIN";
		
//		logger.debug("Prod freq received line {} : {}", lineIndex, value);

		EventBusService.post(new LineSpeedEvent(lineIndex, value));
	}	
	
	protected void handleLineSpeedEvent(String lineIndex, final PlcEvent event) {
		
		String value = String.valueOf(event.getValue()) +" M/MIN";
		
//		logger.debug("Speed received line {} : {}", lineIndex, value);

		EventBusService.post(new LineSpeedEvent(lineIndex, value));

	}
	
	/**
	 * @return system types of all lines
	 */
	private Map<String, Short> getSystemTypes(){
		
		if (parameters == null || parameters.size()<=0){
			return null;
		}
		
		if (systemTypes == null){
			systemTypes = new HashMap<String, Short>();
			
			List<String> systemTypeVarList = PlcVariables.PARAM_SYSTEM_TYPE.getLineVariableNames();
			
			for (String systemTypeVar : systemTypeVarList) {
				for (IPlcVariable<?> var : parameters) {
					if (var.getVariableName().equals(systemTypeVar)){
						systemTypes.put(PlcVariableMap.getLineIndex(systemTypeVar), (Short) var.getValue());
					}
				}
			}
		}
		
		return systemTypes;
	}
	
	/**
	 * @return system types of all lines
	 */
	private Map<String, Boolean> getActiveLines(){
		
		if (parameters == null || parameters.size()<=0){
			return null;
		}
		
		if (activeLines == null){
			activeLines = new HashMap<String, Boolean>();
			
			List<String> systemTypeVarList = PlcVariables.PARAM_LINE_IS_ACTIVE.getLineVariableNames();
			
			for (String systemTypeVar : systemTypeVarList) {
				for (IPlcVariable<?> var : parameters) {
					if (var.getVariableName().equals(systemTypeVar)){
						activeLines.put(PlcVariableMap.getLineIndex(systemTypeVar), (Boolean) var.getValue());
					}
				}
			}
		}
		
		return activeLines;
	}
	
	private boolean isLineActive(String lineIndex) {
		return getActiveLines().get(lineIndex);
	}
	
	/**
	 * 
	 * @param event - notified variable
	 * @return true if the system type of the related line is tobacco
	 */
	private boolean isTobacco(PlcEvent event){
		
		Short systemType = getSystemTypes().get(PlcVariableMap.getLineIndex(event.getVarName()));
		return (systemType != null) && systemType.equals(SYSTEM_TYPE_TOBACCO);
	}
	
	

	/**
	 * @see com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor#executeRequest(com.sicpa.standard.sasscl.devices.plc.PlcRequest)
	 */
	@Override
	public void executeRequest(final PlcRequest request) throws PlcAdaptorException {

		logger.debug("PLC - execute request: {}", request.getDescription());

		IPlcRequestExecutor executor = getRequestExecutor(request);
		if (executor == null) {
			throw new PlcAdaptorException(MessageFormat.format("No request action(s) defined for {0}",
					request.getDescription()));
		}
		executor.execute(controller);
	}

	@Override
	public void reloadPlcParameter(final IPlcVariable<?> var) throws PlcAdaptorException {

		if (var == null) {
			return;
		}

		logger.debug("Reloading PLC parameter : variable name - {} , value - {}", var.getVariableName(), var.getValue());

		try {
			write(var);
			sendReloadPlcParametersRequest();
		} catch (Exception e) {
			throw new PlcAdaptorException(e);
		}
	}

	/**
	 * retrieve request executor based on passed in PLCRequest
	 * 
	 * @param request
	 *            to be executed
	 * @return executor
	 * @throws com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException
	 *             wrapping exception
	 */
	protected IPlcRequestExecutor getRequestExecutor(final PlcRequest request) throws PlcAdaptorException {
		if (plcRequestActionMap == null) {
			throw new PlcAdaptorException("PLC request actions are not setup");
		}
		IPlcRequestExecutor requestExecutor = plcRequestActionMap.get(request);
		return requestExecutor;
	}

	protected IPlcController<? extends IPlcModel> getController() {
		return controller;
	}

	public List<IPlcVariable<?>> getNotificationVariables() {
		return notificationVariables;
	}

	// invoked from spring configuration
	public void setNotificationVariables(final List<IPlcVariable<?>> notificationVariables) {
		this.notificationVariables.addAll(notificationVariables);
	}

	/**
	 * add list of notification variables into the existing notification list
	 * 
	 * @param notificationVariables
	 *            notification variables
	 */
	public void addNotificationVariables(final List<IPlcVariable<?>> notificationVariables) {
		for (IPlcVariable<?> var : notificationVariables) {
			addNotificationVariable(var);
		}
	}

	/**
	 * add a notification variable into the existing notification list
	 * 
	 * @param notificationVariable
	 *            notification variable
	 */
	public void addNotificationVariable(final IPlcVariable<?> notificationVariable) {
		if (notificationVariables != null) {
			notificationVariables.add(notificationVariable);
			registerNotification(notificationVariable);
		}
	}

	public Map<PlcRequest, IPlcRequestExecutor> getPlcRequestActionMap() {
		return plcRequestActionMap;
	}

	public void setPlcRequestActionMap(final Map<PlcRequest, IPlcRequestExecutor> plcRequestActionMap) {
		this.plcRequestActionMap.putAll(plcRequestActionMap);
	}

	protected void sendReloadPlcParametersRequest() throws PlcAdaptorException {
		executeRequest(PlcRequest.RELOAD_PLC_PARAM);
	}

	@Override
	public <T> T read(IPlcVariable<T> var) throws PlcAdaptorException {
		try {
			return controller.getPlcValue(var);
		} catch (PlcException e) {
			throw new PlcAdaptorException(e);
		}
	}

	@Override
	public void write(IPlcVariable<?> var) throws PlcAdaptorException {
		try {
			controller.createRequest(PlcAction.request(var)).execute();
		} catch (PlcException e) {
			throw new PlcAdaptorException(e);
		}
	}

	public void setParameters(Collection<IPlcVariable<?>> parameters) {
		this.parameters.addAll(parameters);
	}

	@Override
	public IConfigurator<PlcConfig, PlcAdaptor> getConfigurator() {
		return new IConfigurator<PlcConfig, PlcAdaptor>() {
			@Override
			public void execute(PlcConfig config, PlcAdaptor configurable) throws ConfigurationFailedException {
				try {
					configure(config);
				} catch (Exception e) {
					throw new ConfigurationFailedException(e);
				}
			}
		};
	}

	protected IPlcValuesLoader loader;
	protected List<IPlcVariable> parameterLine;
	protected List<IPlcVariable> notificationLine;

	protected void configure(PlcConfig config) throws Exception {
		unregisterAllActivePlcVarGroup();
		loadLine1Params(config);
		loadLine2Params(config);
		loadLine3Params(config);

		for (Entry<String, String> entry : config.getProperties().entrySet()) {
			IPlcVariable<?> var = createVar(entry.getKey(), entry.getValue());
			parameters.add(var);
		}
	}

	protected void loadLine1Params(PlcConfig config) throws Exception {
		if (StringUtils.isNotEmpty(config.getLine1Index())) {
			int index1 = Integer.parseInt(config.getLine1Index());
			loadVariableFile(config.getLine1ConfigFile(), index1);
			loadLineNotification(index1);
		}
	}

	protected void loadLine2Params(PlcConfig config) throws Exception {
		if (StringUtils.isNotEmpty(config.getLine2Index())) {
			int index2 = Integer.parseInt(config.getLine2Index());
			loadVariableFile(config.getLine2ConfigFile(), index2);
			loadLineNotification(index2);
		}
	}

	protected void loadLine3Params(PlcConfig config) throws Exception {
		if (StringUtils.isNotEmpty(config.getLine3Index())) {
			int index3 = Integer.parseInt(config.getLine3Index());
			loadVariableFile(config.getLine3ConfigFile(), index3);
			loadLineNotification(index3);
		}
	}

	protected IPlcVariable<?> createVar(String name, String value) {

		String physicalName = name;

		try {
			short sval = Short.parseShort(value);
			IPlcVariable<Short> var = PlcVariable.createShortVar(physicalName, sval);
			return var;
		} catch (Exception e) {
			try {
				int ival = Integer.parseInt(value);
				IPlcVariable<Integer> var = PlcVariable.createInt32Var(physicalName);
				var.setValue(ival);
				return var;
			} catch (NumberFormatException e2) {
				boolean bval = Boolean.parseBoolean(value);
				return PlcVariable.createBooleanVar(physicalName, bval);
			}
		}
	}

	protected List<PlcVariableGroup> lineVarGroups;

	// keep a ref on it to be able to unregister from the event bus as plcpulsedescriptor are register to listen to
	// pulse conversion changed
	protected final Collection<PlcVariableGroup[]> allActivePlcVarGroup = new ArrayList<PlcVariableGroup[]>();

	protected void unregisterAllActivePlcVarGroup() {
		for (PlcVariableGroup[] groups : allActivePlcVarGroup) {
			for (PlcVariableGroup group : groups) {
				for (PlcVariableDescriptor<?> desc : group.getPlcVars()) {
					EventBusService.unregister(desc);
				}
			}
		}
		allActivePlcVarGroup.clear();
	}

	protected void loadVariableFile(String fileName, int index) throws Exception {

		if (fileName != null && !fileName.isEmpty()) {

			List<IPlcVariable> params = new ArrayList<IPlcVariable>();

			for (IPlcVariable var : parameterLine) {
				params.add(PlcUtils.clone(var, index));
			}

			PlcValuesForAllVar values = ConfigUtils.load(plcConfigFolder + "/" + fileName);
			loader.load(params, values);
			parameters.addAll(params);
			generateLineEditableVariable(fileName, index, values);

		}
	}

	protected void generateLineEditableVariable(String fileName, int index, PlcValuesForAllVar values) {
		// sent event to make param available on the gui
		try {
			PlcVariableGroup[] groups = transform(lineVarGroups, index);
			allActivePlcVarGroup.add(groups);

			EditablePlcVariables editablePlcVariables = new EditablePlcVariables(values, groups);
			editablePlcVariables.setFile("config/plc/" + fileName);
			PlcVariableGroupEvent evt = new PlcVariableGroupEvent(editablePlcVariables, "" + index);
			EventBusService.post(evt);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 
	 * transform a group of template var to actual var (#x replace by the line index)
	 */
	@SuppressWarnings("unchecked")
	protected PlcVariableGroup[] transform(List<PlcVariableGroup> group, int index) {

		PlcVariableGroup[] newGroup = new PlcVariableGroup[group.size()];
		for (int i = 0; i < newGroup.length; i++) {
			newGroup[i] = new PlcVariableGroup();
			newGroup[i].setDescription(group.get(i).getDescription());
			for (PlcVariableDescriptor<?> desc : group.get(i).getPlcVars()) {
				PlcVariableDescriptor<?> newDesc = desc.clone();
				EventBusService.register(newDesc);
				newDesc.setVariable((IPlcVariable<?>) PlcUtils.clone(desc.getVariable(), index));
				if (desc instanceof PlcPulseVariableDescriptor) {
					((PlcPulseVariableDescriptor) newDesc).setUnitPlcVar((IPlcVariable<Boolean>) PlcUtils.clone(
							((PlcPulseVariableDescriptor) newDesc).getUnitPlcVar(), index));
				}
				newGroup[i].addDescriptor(newDesc);
			}
		}
		return newGroup;
	}

	public void setLineVarGroups(List<PlcVariableGroup> lineVarGroups) {
		this.lineVarGroups = lineVarGroups;
	}

	protected void loadLineNotification(int index) {
		for (IPlcVariable<?> var : notificationLine) {
			notificationVariables.add(PlcUtils.clone(var, index));
		}
	}

	public void setLoader(IPlcValuesLoader loader) {
		this.loader = loader;
	}

	public void setParameterLine(List<IPlcVariable> parameterLine) {
		this.parameterLine = parameterLine;
	}

	public void setNotificationLine(List<IPlcVariable> notificationLine) {
		this.notificationLine = notificationLine;
	}

	@Override
	public boolean isBlockProductionStart() {
		return true;
	}

	@Override
	public String getPlcVersion() {

		if (!isConnected()) {
			return "";
		}

		IPlcVariable<Integer> h = PlcVariable.createInt32Var(PlcVariables.NTF_VERSION_HIGH.getVariableName());
		IPlcVariable<Integer> m = PlcVariable.createInt32Var(PlcVariables.NTF_VERSION_MEDIUM.getVariableName());
		IPlcVariable<Integer> l = PlcVariable.createInt32Var(PlcVariables.NTF_VERSION_LOW.getVariableName());
		try {
			return read(h) + "." + read(m) + "." + read(l);
		} catch (PlcAdaptorException e) {
			logger.error("", e);
			return "error reading plc version";
		}
	}
}
