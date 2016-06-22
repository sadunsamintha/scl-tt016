package com.sicpa.standard.sasscl.devices.plc.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

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
import com.sicpa.standard.sasscl.controller.productionconfig.ConfigurationFailedException;
import com.sicpa.standard.sasscl.controller.productionconfig.IConfigurable;
import com.sicpa.standard.sasscl.controller.productionconfig.IConfigurator;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PlcConfig;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.plc.AbstractPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.IPlcParamSender;
import com.sicpa.standard.sasscl.devices.plc.IPlcRequestExecutor;
import com.sicpa.standard.sasscl.devices.plc.IPlcValuesLoader;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.devices.plc.PlcRequest;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

@SuppressWarnings("rawtypes")
public class PlcAdaptor extends AbstractPlcAdaptor implements IPlcControllerListener, IConfigurable<PlcConfig, PlcAdaptor> {

	private static final Logger logger = LoggerFactory.getLogger(PlcAdaptor.class);

	private IPlcController<? extends IPlcModel> controller;
	private final Map<PlcRequest, IPlcRequestExecutor> plcRequestActionMap = new HashMap<>();
	private final List<IPlcVariable<?>> notificationVariables = new ArrayList<>();
	private IPlcValuesLoader loader;
	private IPlcParamSender paramSender;
	private List<IPlcVariable> notificationLine;
	private String lineActiveVarName;
	private IPlcVariable<Integer> plcVersionHVar;
	private IPlcVariable<Integer> plcVersionMVar;
	private IPlcVariable<Integer> plcVersionLVar;

	private final AtomicBoolean notificationCreated = new AtomicBoolean(false);
	private PlcConfig currentProdConfig;
	private final Collection<Integer> activeLines = new ArrayList<>();

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
			public void onPlcNotify(IPlcController sender, IPlcVariable variable, Object value) {
				firePlcEvent(new PlcEvent(variable.getVariableName(), value));
			}
		});
	}

	@Override
	protected void doDisconnect() {
		controller.shutdown();
		fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
		removeAllListener();
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

	@Override
	public void doStop() throws PlcAdaptorException {
		if (!isConnected()) {
			throw new PlcAdaptorException("PLC is not connected");
		}
		executeRequest(PlcRequest.STOP);
		fireDeviceStatusChanged(DeviceStatus.STOPPED);
	}

	@Override
	public void onPlcEventReceived(IPlcController<?> controller, PlcEventArgs eventArgs) {
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
		sendReloadPlcParametersRequest();
		createNotifications();
		fireDeviceStatusChanged(DeviceStatus.CONNECTED);
	}

	private void sendAllParameters() {
		loader.sendValues();
		sendProductionVariableConfig();
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
		addNotifForActiveLines(config);
	}

	private void sendProductionVariableConfig() {
		sendCabinetProductionConfig();
		sendLinesProductionConfig();
	}

	private void sendCabinetProductionConfig() {
		int notaLine = 0;
		sentViaParamSender(currentProdConfig.getCabinetProperties(), notaLine);
	}

	private void sendLinesProductionConfig() {
		for (Entry<Integer, StringMap> entry : currentProdConfig.getLinesProperties().entrySet()) {
			sentViaParamSender(entry.getValue(), entry.getKey());
		}
	}

	private void sentViaParamSender(StringMap properties, int lineIndex) {
		for (Entry<String, String> entry : properties.entrySet()) {
			sentViaParamSender(entry.getKey(), entry.getValue(), lineIndex);
		}
	}

	private void sentViaParamSender(String var, String value, int lineIndex) {
		try {
			paramSender.sendToPlc(var, value, lineIndex);
		} catch (PlcAdaptorException e) {
			logger.error("failed to send param " + var + ":" + value, e);
			EventBusService.post(new MessageEvent(this, MessageEventKey.PLC.ERROR_SENDING_PARAM, var, value + ""));
		}
	}

	private void addNotifForActiveLines(PlcConfig config) throws Exception {
		getActiveLines(config).forEach(i -> loadLineNotification(i));
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
		try {
			return read(plcVersionHVar) + "." + read(plcVersionMVar) + "." + read(plcVersionLVar);
		} catch (PlcAdaptorException e) {
			logger.error("", e);
			return "error reading plc version";
		}
	}

	@Override
	public boolean isLineActive(int lineIndex) {
		return getActiveLines().contains(lineIndex);
	}

	private Collection<Integer> getActiveLines() {
		return getActiveLines(null);
	}

	private Collection<Integer> getActiveLines(PlcConfig config) {
		if (activeLines.isEmpty() && config != null) {
			for (Entry<Integer, StringMap> entry : config.getLinesProperties().entrySet()) {
				int lineIndex = entry.getKey();
				for (Entry<String, String> e : entry.getValue().entrySet()) {
					if (e.getKey().equals(lineActiveVarName)) {
						if (Boolean.parseBoolean(e.getValue())) {
							activeLines.add(lineIndex);
						}
					}
				}
			}
		}

		return activeLines;
	}

	public void setLineActiveVarName(String lineActiveVarName) {
		this.lineActiveVarName = lineActiveVarName;
	}

	public void setPlcVersionHVar(IPlcVariable<Integer> plcVersionHVar) {
		this.plcVersionHVar = plcVersionHVar;
	}

	public void setPlcVersionLVar(IPlcVariable<Integer> plcVersionLVar) {
		this.plcVersionLVar = plcVersionLVar;
	}

	public void setPlcVersionMVar(IPlcVariable<Integer> plcVersionMVar) {
		this.plcVersionMVar = plcVersionMVar;
	}

	public void setParamSender(IPlcParamSender paramSender) {
		this.paramSender = paramSender;
	}
}
