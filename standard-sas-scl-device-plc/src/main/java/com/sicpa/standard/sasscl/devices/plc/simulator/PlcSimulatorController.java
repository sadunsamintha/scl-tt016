package com.sicpa.standard.sasscl.devices.plc.simulator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.plc.controller.IPlcController;
import com.sicpa.standard.plc.controller.IPlcControllerListener;
import com.sicpa.standard.plc.controller.PlcException;
import com.sicpa.standard.plc.controller.actions.IPlcAction;
import com.sicpa.standard.plc.controller.actions.PlcAction;
import com.sicpa.standard.plc.controller.actions.PlcCallbackAction;
import com.sicpa.standard.plc.controller.actions.PlcRequestAction;
import com.sicpa.standard.plc.controller.internal.PlcNotificationImpl;
import com.sicpa.standard.plc.controller.internal.PlcRequestResultImpl;
import com.sicpa.standard.plc.controller.internal.delegate.NotificationDelegateFactory;
import com.sicpa.standard.plc.controller.notification.IPlcNotification;
import com.sicpa.standard.plc.controller.notification.IPlcNotificationHandler;
import com.sicpa.standard.plc.controller.notification.IPlcNotificationListener;
import com.sicpa.standard.plc.controller.notification.PlcNotificationHandler;
import com.sicpa.standard.plc.controller.request.IPlcRequest;
import com.sicpa.standard.plc.controller.request.IPlcRequestExecutor;
import com.sicpa.standard.plc.controller.request.IPlcRequestResult;
import com.sicpa.standard.plc.controller.request.IPlcRequestResult.RequestStatus;
import com.sicpa.standard.plc.controller.util.AnnotationUtils;
import com.sicpa.standard.plc.driver.IPlcDriver;
import com.sicpa.standard.plc.driver.event.PlcEventArgs;
import com.sicpa.standard.plc.driver.event.PlcEventCode;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.devices.plc.impl.PlcVariables;
import com.sicpa.standard.sasscl.devices.simulator.gui.SimulatorControlView;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class PlcSimulatorController implements IPlcController<PlcSimulatorConfig> {

	protected final static Logger logger = LoggerFactory.getLogger(PlcSimulatorController.class);

	protected PlcSimulatorConfig config;

	protected final List<IPlcControllerListener> listeners = new ArrayList<IPlcControllerListener>();

	protected Thread notificationWorkerThread;

	protected boolean running;

	protected boolean connected;

	/*
	 *
	 */
	protected SimulatorControlView simulatorGui;

	/*
	 * variable to keep notification request in a queue to be processed later
	 * 
	 * createNotification can be called before the the controller is created, this queue is to keep all the notification
	 * request registered before the controller is created
	 */
	protected final BlockingQueue<PlcNotificationImpl<?>> notificationQueue = new LinkedBlockingQueue<PlcNotificationImpl<?>>();

	/*
	 * map to keep all the timers for notification
	 */
	protected Map<String, Timer> notificationTimers = new HashMap<String, Timer>();

	/*
	 * map to keep the notification definition/pattern, to retrieve configuration for each notification based on
	 * variable name
	 */
	protected Map<String, PlcSimulatorNotificationConfig> configNotificationVariableMap = new HashMap<String, PlcSimulatorNotificationConfig>();

	/*
	 * to keep all the value of the variable
	 */
	protected final Map<String, IPlcVariable<?>> plcVariableMap = new LinkedHashMap<String, IPlcVariable<?>>();

	/*
	 * list of variables that have been created
	 */
	protected final Map<String, PlcNotificationImpl<?>> registeredNotifications = new HashMap<String, PlcNotificationImpl<?>>();

	/*
	 * Executor for request that need to be executed later.
	 */
	protected final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

	/*
	 * use to generate random number in notification value calculation
	 */
	protected Random rand = new Random();
	protected String title = "plc";

	public PlcSimulatorController() {
		this(new PlcSimulatorConfig());
	}

	public PlcSimulatorController(final PlcSimulatorConfig config) {
		this.config = config;
		setupConfigNotificationVariableMap();
	}

	protected PlcSimulatorController(PlcSimulatorConfig config, String title) {
		this(config);
		this.title = title;
	}

	/**
	 * 
	 * @param config
	 * @param simulatorGui
	 * @param productsCounterVarName
	 */
	public PlcSimulatorController(PlcSimulatorConfig config, SimulatorControlView simulatorGui) {
		this(config);
		this.simulatorGui = simulatorGui;
	}

	/*
	 * populate PLC variable map to locate variable by variable name
	 */
	public void setupVariableValueMap() {
		ThreadUtils.sleepQuietly(500);

		if (config.getPlcVariables() == null) {
			return;
		}

		for (IPlcVariable<?> var : config.getPlcVariables()) {
			plcVariableMap.put(var.getVariableName(), var);
		}

		// synchronize notification & variable list - to copy all notification variable into PLC variable map
		for (PlcSimulatorNotificationConfig var : config.getNotificationVariables()) {
			if (!plcVariableMap.keySet().contains(var.getVarName())) {
				plcVariableMap.put(var.getVarName(),
						PlcSimulatorNotificationConfig.getPlcVariable(var.getVarName(), var.getInitialValue()));
			}
		}

	}

	/*
	 * populate config notification variable map to locate variable by variable name
	 */
	protected void setupConfigNotificationVariableMap() {
		if (config.getNotificationVariables() == null) {
			return;
		}

		for (PlcSimulatorNotificationConfig var : config.getNotificationVariables()) {
			configNotificationVariableMap.put(var.getVarName(), var);
		}
	}

	protected PlcSimulatorView plcSimulatorView;

	public class GuiCreator implements Runnable {

		@Override
		public void run() {

			simulatorGui.removeSimulator(title);
			plcSimulatorView = new PlcSimulatorView(PlcSimulatorController.this);
			simulatorGui.addSimulator(title, plcSimulatorView);
		}
	}

	protected void showGui() {

		if (simulatorGui == null) {
			return;
		}

		ThreadUtils.invokeLater(new GuiCreator());
	}

	protected void hideGui() {
		if (simulatorGui == null) {
			return;
		}

		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				simulatorGui.removeSimulator(title);
			}
		});
	}

	public void fireStatusChanged(final PlcEventCode eventCode) {

		if (eventCode.equals(PlcEventCode.DISCONNECTED)) {
			// disconnecting device, stop
			this.doStop();
		}

		synchronized (listeners) {
			for (IPlcControllerListener lis : listeners) {
				lis.onPlcEventReceived(this, new PlcEventArgs(eventCode));
			}
		}
	}

	@Override
	public void addListener(final IPlcControllerListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	@Override
	public void create(final PlcSimulatorConfig model) throws PlcException {
		create(model, null);
	}

	@Override
	public void create(final PlcSimulatorConfig model, final IPlcDriver driver) throws PlcException {

		if (createBeforeShowingGui(model))
			return;

		showGui();

		createAfterShowingGui();

	}

	protected boolean createBeforeShowingGui(PlcSimulatorConfig model) {

		EventBusService.post(new MessageEvent(MessageEventKey.Simulator.PLC));
		if (connected) {
			logger.debug("plc controller has been created");
			return true;
		}

		// set model
		if (model != null) {
			config = model;
		}
		setupVariableValueMap();

		// clear notification every time it is created
		notificationTimers.clear();
		return false;
	}

	protected void createAfterShowingGui() {

		connected = true;
		running = true;

		if (notificationWorkerThread == null || !notificationWorkerThread.isAlive()) {
			notificationWorkerThread = new Thread(new NotificationWorker(), "Notification Worker");
			notificationWorkerThread.start();
		}

		fireStatusChanged(PlcEventCode.CONNECTED);
	}

	@Override
	public void create() throws PlcException {
		create(null);
	}

	@Override
	public <T extends IPlcRequestExecutor> T createExecutor(final Class<T> clazz) {
		return null;
	}

	@Override
	public <T> IPlcNotification createNotification(final IPlcVariable<T> variable,
			final IPlcNotificationListener<T> listener) {
		if (variable == null || listener == null) {
			return null;
		}
		return createNotification(variable, PlcAction.callback(variable, listener));
	}

	/*
	 * 
	 * this method can be called before or after create method
	 * 
	 * @see
	 * com.sicpa.standard.plc.controller.IPlcController#createNotification(com.sicpa.standard.plc.value.IPlcVariable,
	 * com.sicpa.standard.plc.controller.actions.IPlcAction[])
	 */
	@Override
	public <T> IPlcNotification createNotification(final IPlcVariable<T> variable, final IPlcAction... actions) {

		// button if this notification has already been registered, if so ignore and return null
		if (notificationTimers.get(variable.getVariableName()) != null) {
			return null;
		}

		PlcNotificationImpl<T> notification = new PlcNotificationImpl<T>(variable, actions);
		notificationQueue.add(notification);
		return notification;
	}

	protected class NotificationWorker implements Runnable {
		@Override
		public void run() {
			while (running) {
				checkAndDoNotification();
			}
		}
	}

	public void checkAndDoNotification() {

		PlcNotificationImpl<?> plcNotification = null;

		try {
			plcNotification = notificationQueue.poll(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}

		if (plcNotification != null) {

			// process notification
			// button if the variable is register in the model as well
			final PlcSimulatorNotificationConfig notificationVar = configNotificationVariableMap.get(plcNotification
					.getVarName());

			// if so, create timer to execute the call back
			if (notificationVar != null) {

				// add into registered notifications list - to be utilized by the GUI
				registeredNotifications.put(plcNotification.getVarName(), plcNotification);

				if (notificationVar.getValuePattern().equals(PlcSimulatorNotificationValuePattern.UI)) {
					// do no create timer task for notification that will be sent by using GUI
					return;
				}

				logger.debug(
						"Creating notification for PLC variable : {}, sending notification every {1} milliseconds",
						notificationVar.getVarName(), String.valueOf(notificationVar.getNotificationInterval()));

				Timer timer = createNotificationTimer(plcNotification, notificationVar);

				// register the notification variable into the PLC variable map
				plcVariableMap.put(
						notificationVar.getVarName(),
						PlcSimulatorNotificationConfig.getPlcVariable(notificationVar.getVarName(),
								notificationVar.getInitialValue()));

				notificationTimers.put(plcNotification.getVarName(), timer);

			}

			else {

				// button from plc variable map
				IPlcVariable<?> plcVariable = plcVariableMap.get(plcNotification.getVarName());

				// for any un-configured notification, register it to be sent via GUI
				if (plcVariable != null) {
					// add into registered notifications list - to be utilized by the GUI
					registeredNotifications.put(plcNotification.getVarName(), plcNotification);
				} else {
					// throw error because it is an un-registered variable
					logger.error("Failed to create notification for variable : {} - Variable not found",
							plcNotification.getVarName());
				}
			}
		}
	}

	/*
	 * form a notification timer and return to caller
	 */
	protected Timer createNotificationTimer(final PlcNotificationImpl<?> plcNotification,
			final PlcSimulatorNotificationConfig notificationVar) {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(getTimerTask(plcNotification, notificationVar),
				notificationVar.getNotificationInterval(), notificationVar.getNotificationInterval());
		return timer;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected TimerTask getTimerTask(final PlcNotificationImpl<?> plcNotification,
			final PlcSimulatorNotificationConfig notificationVar) {

		return new TimerTask() {

			@Override
			public void run() {

				if (!running)
					return;

				for (IPlcAction action : plcNotification.getActions()) {
					if (action instanceof PlcCallbackAction) {
						PlcCallbackAction<?> callbackAction = (PlcCallbackAction<?>) action;
						IPlcNotificationListener notificationListener = callbackAction.getListener();

						// get latest value from plc variable map
						IPlcVariable plcVariable = plcVariableMap.get(plcNotification.getVarName());

						// calculate the next value based on the pattern defined in model
						Object nextValue = calculateNotificationValue(notificationVar, plcVariable.getValue());
						plcVariable.setValue(nextValue);

						notificationListener.onPlcNotify(PlcSimulatorController.this, plcVariable,
								plcVariable.getValue());

						notifyTest();
					}
				}
			}
		};
	}

	protected int counter;

	public int getCounter() {
		synchronized (PlcSimulatorController.this) {
			return counter;
		}
	}

	protected void notifyTest() {
		synchronized (PlcSimulatorController.this) {
			counter++;
			notifyAll();
		}
	}

	protected Object calculateNotificationValue(final PlcSimulatorNotificationConfig notificationVar,
			Object currentValue) {

		switch (notificationVar.getValuePattern()) {
		case STATIC:
			break;

		case INCREMENTAL:
			// this case only works for integer
			if (currentValue instanceof Integer) {
				currentValue = ((Integer) currentValue) + notificationVar.getIncrementalValue();
			}
			break;

		case RANDOM:

			// this case works for boolean and integer
			if (currentValue instanceof Integer) {
				currentValue = rand.nextInt(notificationVar.getMaxValue() - notificationVar.getMinValue() + 1)
						+ notificationVar.getMinValue();
			}

			if (currentValue instanceof Boolean) {
				currentValue = rand.nextBoolean();
			}

			break;

		default:
			break;
		}

		return currentValue;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void createNotification(final IPlcNotificationHandler handler) {

		final Map<Method, PlcNotificationHandler> annotatedMethods = AnnotationUtils.findAllMethod(handler.getClass(),
				PlcNotificationHandler.class);

		for (final Map.Entry<Method, PlcNotificationHandler> entry : annotatedMethods.entrySet()) {

			final String varName = entry.getValue().variableName();
			final Class plcVariableType = entry.getValue().variableType();

			final Method method = entry.getKey();
			final Class<?>[] paramsTypes = method.getParameterTypes();
			final Class<?> javaValueType = paramsTypes[2];

			IPlcVariable variable = new PlcVariable(varName, plcVariableType, javaValueType);
			IPlcAction actions = PlcAction.callback(variable,
					NotificationDelegateFactory.createDelegate(handler, method));

			PlcNotificationImpl notification = new PlcNotificationImpl(variable, actions);
			notificationQueue.add(notification);
		}

	}

	@Override
	public IPlcRequest createRequest(final IPlcAction... actions) {

		// convert actions into instance of PlcRequestAction
		List<PlcRequestAction<?>> requestActions = new ArrayList<PlcRequestAction<?>>();

		for (IPlcAction action : actions) {
			if (action instanceof PlcRequestAction<?>) {
				requestActions.add((PlcRequestAction<?>) action);
			}
		}
		return new PlcRequestImpl(this, requestActions.toArray(new PlcRequestAction<?>[requestActions.size()]));
	}

	@Override
	public IPlcRequest createRequest(final String varName) {
		return createRequest(PlcAction.request(varName));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getPlcValue(final IPlcVariable<T> variable) throws PlcException {
		IPlcVariable<?> value = plcVariableMap.get(variable.getVariableName());
		return value == null ? null : (T) value.getValue();
	}

	@Override
	public void removeListener(final IPlcControllerListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	/**
	 * stop notification
	 */
	protected void doStop() {
		running = false;
	}

	/**
	 * start notification
	 */
	protected void doStart() {
		running = true;
	}

	@Override
	public void shutdown() {
		hideGui();
		running = false;
		connected = false;

		if (notificationWorkerThread != null) {
			notificationWorkerThread.interrupt();
			try {
				joinNotificationWorkerThread();
			} catch (InterruptedException e) {
			}
		}

		// stop all notification timers
		if (notificationTimers != null) {
			for (Timer timer : notificationTimers.values()) {
				timer.cancel();
				timer.purge();
			}
		}

		// clear notification queue
		notificationQueue.clear();

		fireStatusChanged(PlcEventCode.DISCONNECTED);
	}

	protected void joinNotificationWorkerThread() throws InterruptedException {

		notificationWorkerThread.join();
	}

	@Override
	public void update(final PlcSimulatorConfig model) throws PlcException {

	}

	public void setSimulatorGui(final SimulatorControlView simulatorGui) {
		this.simulatorGui = simulatorGui;
	}

	/*
	 * request executor
	 * 
	 * @param action
	 * 
	 * @throws PlcException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void execute(final PlcRequestAction<?> action) throws PlcException {
		
		final IPlcVariable<?> plcVar = action.getVariable();
		if (plcVar == null)
			return;
		
		String varName = plcVar.getVariableName();
		if (varName == null)
			return;
		
		if (varName.equals(PlcVariables.REQUEST_STOP.getVariableName())) {
			// stop timer
			this.doStop();
		}

		else if (varName.equals(PlcVariables.REQUEST_START.getVariableName())) {
			// start timer
			this.doStart();
		}

		logger.debug("Writing value of PLC variable : {}, value : {}", varName, plcVar.getValue());

		IPlcVariable plcVariable = plcVariableMap.get(varName);

		if (plcVariable != null)
			plcVariable.setValue(plcVar.getValue());
	}

	public Future<IPlcRequestResult> executeLater(final IPlcRequest request, final long delay, final TimeUnit unit) {
		return executorService.schedule(new Callable<IPlcRequestResult>() {
			public IPlcRequestResult call() throws Exception {
				try {
					request.execute();
					return new PlcRequestResultImpl(RequestStatus.COMPLETED, null);
				} catch (final PlcException e) {
					return new PlcRequestResultImpl(RequestStatus.ERROR, e);
				}
			}
		}, delay, unit);
	}

	/*
	 * 
	 * Simplified version of PlcRequestImpl
	 */
	protected static class PlcRequestImpl implements IPlcRequest {

		protected final PlcSimulatorController controller;
		protected final List<PlcRequestAction<?>> actions = new ArrayList<PlcRequestAction<?>>();

		public PlcRequestImpl(final PlcSimulatorController controller, final PlcRequestAction<?>... actions) {
			this.controller = controller;
			for (PlcRequestAction<?> action : actions) {
				this.actions.add(action);
			}
		}

		public void execute() throws PlcException {
			for (PlcRequestAction<?> action : actions) {
				controller.execute(action);
			}
		}

		public Future<IPlcRequestResult> executeLater(final long delay, final TimeUnit unit) {
			return controller.executeLater(this, delay, unit);
		}
	}

	/*
	 * define as default access modifier to allow PLCSimulatorView to access it
	 */
	Map<String, IPlcVariable<?>> getPlcVariableMap() {
		return plcVariableMap;
	}

	/*
	 * 
	 * define as default access modifier to allow PLCSimulatorView to access it
	 */
	Map<String, PlcNotificationImpl<?>> getRegisteredNotifications() {
		return registeredNotifications;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void sendNotification(PlcNotificationImpl<?> notificationImpl, IPlcVariable<?> variable) {
		if (notificationImpl == null) {
			return;
		}
		for (IPlcAction action : notificationImpl.getActions()) {
			if (action instanceof PlcCallbackAction) {
				PlcCallbackAction<?> callbackAction = (PlcCallbackAction<?>) action;
				IPlcNotificationListener notificationListener = callbackAction.getListener();
				notificationListener.onPlcNotify(this, variable, variable.getValue());
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void sendNotification(String name, Object value) {
		PlcNotificationImpl notificationImpl = getRegisteredNotifications().get(name);
		IPlcVariable variable = getPlcVariableMap().get(name);
		variable.setValue(value);
		sendNotification(notificationImpl, variable);
	}

	@Override
	public void removeNotification(String varName) {
	}

	public PlcSimulatorConfig getModel() {
		return this.config;
	}

	public void setModel(PlcSimulatorConfig plcModel) {
		this.config = plcModel;
	}
}
