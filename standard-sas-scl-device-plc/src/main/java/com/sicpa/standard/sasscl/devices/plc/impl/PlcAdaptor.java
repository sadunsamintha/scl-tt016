package com.sicpa.standard.sasscl.devices.plc.impl;

import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.getLineIndex;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.StringMap;
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
import com.sicpa.standard.sasscl.devices.plc.AbstractPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.IPlcListener;
import com.sicpa.standard.sasscl.devices.plc.IPlcParamSender;
import com.sicpa.standard.sasscl.devices.plc.IPlcRequestExecutor;
import com.sicpa.standard.sasscl.devices.plc.IPlcValuesLoader;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.devices.plc.PlcRequest;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

@SuppressWarnings("rawtypes")
public class PlcAdaptor extends AbstractPlcAdaptor implements IPlcControllerListener,
		IConfigurable<PlcConfig, PlcAdaptor> {

	private static final Logger logger = LoggerFactory.getLogger(PlcAdaptor.class);
	private static final short SYSTEM_TYPE_TOBACCO = 3;

	private IPlcController<? extends IPlcModel> controller;
	private final Map<PlcRequest, IPlcRequestExecutor> plcRequestActionMap = new HashMap<>();
	private final List<IPlcVariable<?>> notificationVariables = new ArrayList<>();
	private IPlcValuesLoader loader;
	private IPlcParamSender paramSender;// TODO to be used later
	private List<IPlcVariable> notificationLine;
	private String lineSpeedVarName;
	private String productFreqVarName;
	private String systemTypeVarName;
	private String lineActiveVarName;
	private String plcVersionHVarName;
	private String plcVersionMVarName;
	private String plcVersionLVarName;

	private final AtomicBoolean notificationCreated = new AtomicBoolean(false);
	private final Map<Integer, Short> systemTypes = new HashMap<>();
	private final Map<Integer, Boolean> activeLines = new HashMap<>();
	private PlcConfig currentProdConfig;

	public PlcAdaptor() {
	}

	public PlcAdaptor(final IPlcController<?> controller) {
		this.controller = controller;
		controller.addListener(this);
		setName("PLC");
	}

	@Override
	protected void doConnect() throws PlcAdaptorException {
		try {
			notificationCreated.compareAndSet(true, false);
			controller.create();
		} catch (PlcException e) {
			throw new PlcAdaptorException(e);
		}
	}

	private void createNotifications() {

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

	private void onPlcConnected() {
		sendAllParameters();
		sendProductionVariableConfig();
		addLineSpeedAndFreqListener();

		createNotifications();

		fireDeviceStatusChanged(DeviceStatus.CONNECTED);
	}

	private void addLineSpeedAndFreqListener() {

		addPlcListener(new IPlcListener() {
			@Override
			public void onPlcEvent(PlcEvent event) {
				handleSpeedEvent(event);
			}

			@Override
			public List<String> getListeningVariables() {
				List<String> result = new ArrayList<String>();
				result.addAll(PlcLineHelper.getLinesVariableName(lineSpeedVarName));
				result.addAll(PlcLineHelper.getLinesVariableName(productFreqVarName));
				return result;
			}
		});
	}

	protected void sendAllParameters() {
		loader.sendValues();
		sendProductionVariableConfig();
		sendReloadPlcParametersRequest();
	}

	protected void handleSpeedEvent(final PlcEvent event) {

		// logger.debug("Event {} {}", event.getVarName(), event.getValue());

		int lineIndex = getLineIndex(event.getVarName());

		if (!isLineActive(lineIndex)) {
			logger.debug("line not active");
			return;
		}

		int length = (lineIndex + "").length() + 1;

		String eventVarName = event.getVarName().substring(length);

		String freqVar = productFreqVarName.substring(length + 1);
		String speedVar = lineSpeedVarName.substring(length + 1);

		if (eventVarName.equals(freqVar) && isTobacco(event)) {
			handleProductFreqEvent(lineIndex, event);
		} else if (eventVarName.equals(speedVar) && !isTobacco(event)) {
			handleLineSpeedEvent(lineIndex, event);
		}

	}

	private void handleProductFreqEvent(int lineIndex, PlcEvent event) {
		String value = String.valueOf(event.getValue()) + " PACKS/MIN";
		EventBusService.post(new LineSpeedEvent(lineIndex, value));
	}

	private void handleLineSpeedEvent(int lineIndex, PlcEvent event) {
		String value = String.valueOf(event.getValue()) + " M/MIN";
		EventBusService.post(new LineSpeedEvent(lineIndex, value));
	}

	private Map<Integer, Short> getSystemTypesByLine() {
		if (systemTypes.isEmpty()) {
			for (Entry<Integer, StringMap> entry : loader.getValues().entrySet()) {
				int lineIndex = entry.getKey();
				for (Entry<String, String> e : entry.getValue().entrySet()) {
					if (e.getKey().equals(systemTypeVarName)) {
						systemTypes.put(lineIndex, Short.parseShort(e.getValue()));
					}
				}
			}
		}
		return systemTypes;
	}

	private Map<Integer, Boolean> getActiveLines() {
		if (activeLines.isEmpty()) {

			for (Entry<Integer, StringMap> entry : loader.getValues().entrySet()) {
				int lineIndex = entry.getKey();
				for (Entry<String, String> e : entry.getValue().entrySet()) {
					if (e.getKey().equals(lineActiveVarName)) {
						activeLines.put(lineIndex, Boolean.parseBoolean(e.getValue()));
					}
				}
			}
		}

		return activeLines;
	}

	private boolean isLineActive(int lineIndex) {
		return getActiveLines().get(lineIndex);
	}

	private boolean isTobacco(PlcEvent event) {

		Short systemType = getSystemTypesByLine().get(getLineIndex(event.getVarName()));
		return (systemType != null) && systemType.equals(SYSTEM_TYPE_TOBACCO);
	}

	@Override
	public void executeRequest(final PlcRequest request) throws PlcAdaptorException {

		logger.info("PLC - execute request: {}", request.getDescription());

		IPlcRequestExecutor executor = getRequestExecutor(request);
		if (executor == null) {
			throw new PlcAdaptorException(MessageFormat.format("No request action(s) defined for {0}",
					request.getDescription()));
		}
		executor.execute(controller);
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
	private IPlcRequestExecutor getRequestExecutor(final PlcRequest request) throws PlcAdaptorException {
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

	public void addNotificationVariables(List<IPlcVariable<?>> notificationVariables) {
		for (IPlcVariable<?> var : notificationVariables) {
			addNotificationVariable(var);
		}
	}

	public void addNotificationVariable(IPlcVariable<?> notificationVariable) {
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

	private void sendReloadPlcParametersRequest() {
		try {
			executeRequest(PlcRequest.RELOAD_PLC_PARAM);
		} catch (Exception e) {
			logger.error("", e);
		}
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
			logger.info("writing plc var {} - {}", var.getVariableName(), var.getValue());
			controller.createRequest(PlcAction.request(var)).execute();
		} catch (PlcException e) {
			throw new PlcAdaptorException(e);
		}
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

	private void configure(PlcConfig config) throws Exception {
		this.currentProdConfig = config;
		addNotifIfNeededLine1(config);
		addNotifIfNeededLine2(config);
		addNotifIfNeededLine3(config);
	}

	private void sendProductionVariableConfig() {
		for (Entry<String, String> entry : currentProdConfig.getProperties().entrySet()) {
			try {
				// TODO paramSender.sendToPlc(entry.getKey(), entry.getValue());
			} catch (Exception e) {
				logger.error("failed to send param " + entry.getKey() + ":" + entry.getValue(), e);
				EventBusService.post(new MessageEvent(this, MessageEventKey.PLC.ERROR_SENDING_PARAM, entry.getKey(),
						entry.getValue() + ""));
			}
		}
	}

	private void addNotifIfNeededLine1(PlcConfig config) throws Exception {
		if (StringUtils.isNotEmpty(config.getLine1Index())) {
			int index1 = Integer.parseInt(config.getLine1Index());
			loadLineNotification(index1);
		}
	}

	private void addNotifIfNeededLine2(PlcConfig config) throws Exception {
		if (StringUtils.isNotEmpty(config.getLine2Index())) {
			int index2 = Integer.parseInt(config.getLine2Index());
			loadLineNotification(index2);
		}
	}

	private void addNotifIfNeededLine3(PlcConfig config) throws Exception {
		if (StringUtils.isNotEmpty(config.getLine3Index())) {
			int index3 = Integer.parseInt(config.getLine3Index());
			loadLineNotification(index3);
		}
	}

	private void loadLineNotification(int index) {
		for (IPlcVariable<?> var : notificationLine) {
			notificationVariables.add(PlcLineHelper.clone(var, index));
		}
	}

	public void setLoader(IPlcValuesLoader loader) {
		this.loader = loader;
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

		IPlcVariable<Integer> h = PlcVariable.createInt32Var(plcVersionHVarName);
		IPlcVariable<Integer> m = PlcVariable.createInt32Var(plcVersionMVarName);
		IPlcVariable<Integer> l = PlcVariable.createInt32Var(plcVersionLVarName);
		try {
			return read(h) + "." + read(m) + "." + read(l);
		} catch (PlcAdaptorException e) {
			logger.error("", e);
			return "error reading plc version";
		}
	}

	public void setLineSpeedVarName(String lineSpeedVarName) {
		this.lineSpeedVarName = lineSpeedVarName;
	}

	public void setProductFreqVarName(String productFreqVarName) {
		this.productFreqVarName = productFreqVarName;
	}

	public void setSystemTypeVarName(String systemTypeVarName) {
		this.systemTypeVarName = systemTypeVarName;
	}

	public void setLineActiveVarName(String lineActiveVarName) {
		this.lineActiveVarName = lineActiveVarName;
	}

	public void setPlcVersionHVarName(String plcVersionHVarName) {
		this.plcVersionHVarName = plcVersionHVarName;
	}

	public void setPlcVersionLVarName(String plcVersionLVarName) {
		this.plcVersionLVarName = plcVersionLVarName;
	}

	public void setPlcVersionMVarName(String plcVersionMVarName) {
		this.plcVersionMVarName = plcVersionMVarName;
	}

	public void setParamSender(IPlcParamSender paramSender) {
		this.paramSender = paramSender;
	}
}
