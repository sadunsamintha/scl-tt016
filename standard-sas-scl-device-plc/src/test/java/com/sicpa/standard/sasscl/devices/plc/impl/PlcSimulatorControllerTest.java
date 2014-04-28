package com.sicpa.standard.sasscl.devices.plc.impl;

import static com.sicpa.standard.plc.controller.actions.PlcAction.request;
import static com.sicpa.standard.plc.driver.event.PlcEventCode.CONNECTED;
import static com.sicpa.standard.plc.driver.event.PlcEventCode.DISCONNECTED;
import static com.sicpa.standard.plc.value.PlcVariable.createBooleanVar;
import static com.sicpa.standard.plc.value.PlcVariable.createInt32Var;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.sicpa.standard.common.util.ThreadUtils;
import com.sicpa.standard.plc.controller.IPlcController;
import com.sicpa.standard.plc.controller.IPlcControllerListener;
import com.sicpa.standard.plc.controller.actions.IPlcAction;
import com.sicpa.standard.plc.controller.notification.IPlcNotificationListener;
import com.sicpa.standard.plc.driver.event.PlcEventArgs;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.devices.plc.DefaultPlcRequestExecutor;
import com.sicpa.standard.sasscl.devices.plc.simulator.PlcSimulatorConfig;
import com.sicpa.standard.sasscl.devices.plc.simulator.PlcSimulatorController;
import com.sicpa.standard.sasscl.devices.plc.simulator.PlcSimulatorNotificationConfig;

public class PlcSimulatorControllerTest {

	private static final int SOME_VALUE = 4000;
	private IPlcController<?> plcController;

	private PlcSimulatorConfig simulatorConfig;

	final static String LIFE_CHECK_VAR = "lifeCheck";

	final static String INT_VAR_1 = "var1";

	final static String INCREMENTAL_VAR_1 = "incremental_var_1";

	final static String RANDOM_VAR_1 = "random_var_1";

	final static int RANDOM_VAR_MIN_VALUE = 80;

	final static int RANDOM_VAR_MAX_VALUE = 200;

	final static String RANDOM_BOOLEAN_VAR = "random_boolean";

	final static String START_REQ = "start";

	final static String INT_VAR_REQ = "intVarReq";

	private IPlcVariable<Boolean> lifeCheckVariable = createBooleanVar(LIFE_CHECK_VAR);

	private IPlcVariable<Integer> intVariable1 = createInt32Var(INT_VAR_1);

	final static String REQUEST_START = ".req.Start";
	final static String REQUEST_RUN = ".req.Run";
	final static String REQUEST_PAUSE = ".req.Pause";
	final static String REQUEST_STOP = ".req.Stop";
	final static String REQUEST_START_MAINTENANCE = ".req.MaintenanceStart";
	final static String REQUEST_STOP_MAINTENANCE = ".req.MaintenanceStop";

	// --- define some request variable
	private IPlcVariable<Boolean> startRequest = createBooleanVar(REQUEST_START);
	private IPlcVariable<Boolean> runRequest = createBooleanVar(REQUEST_RUN);
	private IPlcVariable<Boolean> pauseRequest = createBooleanVar(REQUEST_PAUSE);
	private IPlcVariable<Boolean> stopRequest = createBooleanVar(REQUEST_STOP);
	private IPlcVariable<Boolean> startMaintenanceRequest = createBooleanVar(REQUEST_START_MAINTENANCE);
	private IPlcVariable<Boolean> stopMaintenanceRequest = createBooleanVar(REQUEST_STOP_MAINTENANCE);

	@Before
	public void setup() {

		setupPlcSimulatorConfig();
		plcController = new PlcSimulatorController(simulatorConfig);
	}

	/*
	 * to setup the simulator config to be passed into the plc simulator controller
	 */
	private void setupPlcSimulatorConfig() {

		simulatorConfig = new PlcSimulatorConfig();

		// setup notification variable
		List<PlcSimulatorNotificationConfig> notificationVariables = new ArrayList<PlcSimulatorNotificationConfig>();

		notificationVariables.add(new PlcSimulatorNotificationConfig(LIFE_CHECK_VAR, false, 500));

		notificationVariables.add(new PlcSimulatorNotificationConfig(INT_VAR_1, 5, 1000));

		// create notification with incremental pattern
		notificationVariables.add(PlcSimulatorNotificationConfig.createIncrementalPatternVar(INCREMENTAL_VAR_1, 0, 2,
				500));

		// create notification with random pattern (integer)
		notificationVariables.add(PlcSimulatorNotificationConfig.createRandomPatternIntVar(RANDOM_VAR_1,
				RANDOM_VAR_MIN_VALUE, RANDOM_VAR_MAX_VALUE, 300));

		// create notification with random pattern (boolean)
		notificationVariables
				.add(PlcSimulatorNotificationConfig.createRandomPatternBooleanVar(RANDOM_BOOLEAN_VAR, 100));

		// setup plc variables
		List<IPlcVariable<?>> plcVariables = new ArrayList<IPlcVariable<?>>();

		IPlcVariable<Boolean> startVar = createBooleanVar(START_REQ);
		startVar.setValue(false);

		IPlcVariable<Integer> intVarRequest = createInt32Var(INT_VAR_REQ);
		intVarRequest.setValue(1);

		plcVariables.add(startVar);
		plcVariables.add(intVarRequest);

		// add request var to test request executor
		plcVariables.add(startRequest);
		plcVariables.add(stopRequest);
		plcVariables.add(runRequest);
		plcVariables.add(pauseRequest);
		plcVariables.add(startMaintenanceRequest);
		plcVariables.add(stopMaintenanceRequest);

		simulatorConfig.setNotificationVariables(notificationVariables);
		simulatorConfig.setPlcVariables(plcVariables);
	}

	/*
	 * perform establishing connection test
	 * 
	 * - test if the status changed to connected if create() is called
	 * 
	 * - test if the status changed to disconnected if shutdown() is called
	 */
	@Test
	public void plcSimulatorControllerConnectionTest() throws Exception {

		IPlcControllerListener plcControllerListener = mock(IPlcControllerListener.class);

		plcController.addListener(plcControllerListener);

		ArgumentCaptor<PlcEventArgs> plcEventArgument = forClass(PlcEventArgs.class);

		plcController.create();

		verify(plcControllerListener, atLeastOnce()).onPlcEventReceived(Mockito.<IPlcController<?>> anyObject(),
				plcEventArgument.capture());

		assertThat(plcEventArgument.getValue().getEventCode(), equalTo(CONNECTED));

		reset(plcControllerListener);

		plcController.shutdown();

		verify(plcControllerListener, atLeastOnce()).onPlcEventReceived(Mockito.<IPlcController<?>> anyObject(),
				plcEventArgument.capture());

		assertThat(plcEventArgument.getValue().getEventCode(), equalTo(DISCONNECTED));
	}

	/*
	 * perform notification test
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void plcSimulatorControllerNotificationTest() throws Exception {

		plcController.create();

		// mock various listener for different variables
		IPlcNotificationListener lifeCheckNotificationListener = mock(IPlcNotificationListener.class);
		IPlcNotificationListener intVariable1NotificationListener = mock(IPlcNotificationListener.class);
		IPlcNotificationListener otherNotificationListener = mock(IPlcNotificationListener.class);

		plcController.createNotification(intVariable1, intVariable1NotificationListener);

		plcController.createNotification(lifeCheckVariable, lifeCheckNotificationListener);

		// create other variable which is not defined in the model
		IPlcVariable<Boolean> otherBooleanVariable = createBooleanVar("otherBoolean");

		plcController.createNotification(otherBooleanVariable, otherNotificationListener);

		// wait for timer in the plc simulator controller to send out notification
		synchronized (plcController) {

			while (((PlcSimulatorController) plcController).getCounter() < 2) {
				plcController.wait();
			}
		}
		// TODO fix it, failing from time to time so adding a sleep...
		ThreadUtils.sleepQuietly(2000);

		ArgumentCaptor<IPlcVariable> plcVariableArgument = forClass(IPlcVariable.class);
		ArgumentCaptor<Object> valueArgument = forClass(Object.class);

		// verify life button variable
		verify(lifeCheckNotificationListener, atLeastOnce()).onPlcNotify(Mockito.<IPlcController<?>> anyObject(),
				plcVariableArgument.capture(), valueArgument.capture());

		assertThat(plcVariableArgument.getValue().getVariableName(), equalTo(LIFE_CHECK_VAR));
		assertThat((Boolean) valueArgument.getValue(), equalTo(FALSE));

		// verify int variable 1
		verify(intVariable1NotificationListener, atLeastOnce()).onPlcNotify(Mockito.<IPlcController<?>> anyObject(),
				plcVariableArgument.capture(), valueArgument.capture());

		assertThat(plcVariableArgument.getValue().getVariableName(), equalTo(INT_VAR_1));
		assertThat((Integer) valueArgument.getValue(), equalTo(5));

		// button other variable - variable that is not defined in the model
		verify(otherNotificationListener, never()).onPlcNotify(Mockito.<IPlcController<?>> anyObject(),
				plcVariableArgument.capture(), valueArgument.capture());

		plcController.shutdown();
	}

	/*
	 * perform notification test with PLC notification handler
	 * 
	 * @See IPlcNotificationHandler
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void plcSimulatorControllerNotificationHandlerTest() throws Exception {

		PlcNotificationHandlerForTesting notificationHandlerInstance = mock(PlcNotificationHandlerForTesting.class);

		plcController.createNotification(notificationHandlerInstance);

		plcController.create();

		// wait for timer to send notification
		synchronized (plcController) {

			while (((PlcSimulatorController) plcController).getCounter() < 3) {

				plcController.wait();
			}
		}

		plcController.shutdown();

		ArgumentCaptor<IPlcVariable> variableArgument = forClass(IPlcVariable.class);
		ArgumentCaptor<Integer> intValueArgument = forClass(Integer.class);
		ArgumentCaptor<Boolean> booleanValueArgument = forClass(Boolean.class);

		verify(notificationHandlerInstance, atLeastOnce()).intVarNotification(Mockito.<IPlcController<?>> anyObject(),
				variableArgument.capture(), intValueArgument.capture());

		assertThat(variableArgument.getValue().getVariableName(), equalTo(INT_VAR_1));
		assertThat(intValueArgument.getValue(), equalTo(5));

		verify(notificationHandlerInstance, atLeastOnce()).lifeCheckNotification(
				Mockito.<IPlcController<?>> anyObject(), variableArgument.capture(), booleanValueArgument.capture());

		assertThat(variableArgument.getValue().getVariableName(), equalTo(LIFE_CHECK_VAR));
		assertThat(booleanValueArgument.getValue(), equalTo(FALSE));
	}

	/*
	 * 
	 * perform test to read/write a PLC variable value
	 * 
	 * @throws Exception
	 */
	@Test
	public void plcSimulatorControllerRequestTest() throws Exception {

		plcController.create();

		// ----- Boolean test -------------

		// create start request
		IPlcVariable<Boolean> startRequestVariable = createBooleanVar(START_REQ, true);

		// button initial values
		assertThat(plcController.getPlcValue(startRequestVariable), equalTo(FALSE));

		plcController.createRequest(request(startRequestVariable)).execute();

		assertThat(plcController.getPlcValue(startRequestVariable), equalTo(TRUE));

		// ----- Integer test ----------

		// create int request
		IPlcVariable<Integer> intRequestVariable = createInt32Var(INT_VAR_REQ);
		intRequestVariable.setValue(100);

		// button initial values
		assertThat(plcController.getPlcValue(intRequestVariable), equalTo(1));

		plcController.createRequest(request(intRequestVariable)).execute();

		assertThat(plcController.getPlcValue(intRequestVariable), equalTo(100));

		plcController.shutdown();
	}

	/*
	 * 
	 * To test to change the variable value and get the updated value by notification
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void plcSimulatorControllerChangeNotificationVariableTest() throws Exception {

		plcController.create();

		IPlcNotificationListener intVariable1NotificationListener = mock(IPlcNotificationListener.class);

		plcController.createNotification(intVariable1, intVariable1NotificationListener);

		synchronized (plcController) {

			while (((PlcSimulatorController) plcController).getCounter() < 3) {

				plcController.wait();
			}
		}

		ArgumentCaptor<Integer> valueArgument = forClass(Integer.class);

		verify(intVariable1NotificationListener, atLeastOnce()).onPlcNotify(Mockito.<IPlcController<?>> anyObject(),
				Mockito.<IPlcVariable> anyObject(), valueArgument.capture());

		assertThat(valueArgument.getValue(), equalTo(5));

		// change the integer variable value to some other value
		intVariable1.getPlcValue().set(SOME_VALUE);
		plcController.createRequest(request(intVariable1)).execute();

		// reset
		reset(intVariable1NotificationListener);

		synchronized (plcController) {

			while (((PlcSimulatorController) plcController).getCounter() < 4) {

				plcController.wait();
			}
		}

		// button the value again
		verify(intVariable1NotificationListener, atLeastOnce()).onPlcNotify(Mockito.<IPlcController<?>> anyObject(),
				Mockito.<IPlcVariable> anyObject(), valueArgument.capture());

		assertThat(valueArgument.getValue(), equalTo(SOME_VALUE));

		plcController.shutdown();
	}

	/*
	 * 
	 * to test a notification value with incremental pattern
	 * 
	 * @throws Exception
	 */
	@Test
	public void plcSimulatorControllerIncrementalPatternTest() throws Exception {

		plcController.create();

		final AtomicInteger notificationValue = new AtomicInteger();
		final AtomicInteger counter = new AtomicInteger();

		IPlcNotificationListener<Integer> notificationListener = new IPlcNotificationListener<Integer>() {

			@Override
			public void onPlcNotify(IPlcController<?> sender, IPlcVariable<Integer> variable, Integer value) {

				notificationValue.set(value);
				counter.getAndIncrement();
			}
		};

		/*
		 * notification is going to be sent every 500 milliseconds starting with 2 then 4,6,8 ....
		 */
		plcController.createNotification(createInt32Var(INCREMENTAL_VAR_1), notificationListener);

		synchronized (plcController) {

			while (((PlcSimulatorController) plcController).getCounter() < 10 && counter.get() < 5) {

				plcController.wait();
			}
		}

		assertThat(notificationValue.get(), equalTo(counter.get() * 2));

		plcController.shutdown();
	}

	/*
	 * to test a notification with RANDOM pattern
	 * 
	 * @throws Exception
	 */
	@Test
	public void plcSimulatorControllerRandomPatternTest() throws Exception {

		plcController.create();

		final List<Integer> randomIntList = Collections.synchronizedList(new ArrayList<Integer>());

		IPlcNotificationListener<Integer> notificationListener = new IPlcNotificationListener<Integer>() {

			@Override
			public void onPlcNotify(IPlcController<?> sender, IPlcVariable<Integer> variable, Integer value) {

				assertThat(variable.getVariableName(), equalTo(RANDOM_VAR_1));
				randomIntList.add(value);
			}
		};

		plcController.createNotification(createInt32Var(RANDOM_VAR_1), notificationListener);

		final List<Boolean> randomBooleanList = Collections.synchronizedList(new ArrayList<Boolean>());

		IPlcNotificationListener<Boolean> booleanNotificationListener = new IPlcNotificationListener<Boolean>() {

			@Override
			public void onPlcNotify(IPlcController<?> sender, IPlcVariable<Boolean> variable, Boolean value) {

				assertThat(variable.getVariableName(), equalTo(RANDOM_BOOLEAN_VAR));
				randomBooleanList.add(value);
			}
		};

		plcController.createNotification(createBooleanVar(RANDOM_BOOLEAN_VAR), booleanNotificationListener);

		synchronized (plcController) {

			while (((PlcSimulatorController) plcController).getCounter() < 33) {

				plcController.wait();
			}
		}

		for (Integer randomInt : randomIntList) {
			assertThat(randomInt >= RANDOM_VAR_MIN_VALUE && randomInt <= RANDOM_VAR_MAX_VALUE, equalTo(TRUE));
		}

		int countTrue = 0;
		int countFalse = 0;

		for (Boolean random : randomBooleanList)
			if (random)
				countTrue++;
			else
				countFalse++;

		assertThat(countTrue > 0, equalTo(TRUE));
		assertThat(countFalse > 0, equalTo(TRUE));

		plcController.shutdown();
	}

	@Test
	public void requestExecutorTest() throws Exception {

		DefaultPlcRequestExecutor requestExecutor = new DefaultPlcRequestExecutor();

		plcController.create();

		assertThat(plcController.getPlcValue(startRequest), equalTo(FALSE));
		assertThat(plcController.getPlcValue(runRequest), equalTo(FALSE));

		requestExecutor.setPlcActions(new IPlcAction[] { request(REQUEST_START, true), request(REQUEST_RUN, true) });

		requestExecutor.execute(plcController);

		assertThat(plcController.getPlcValue(startRequest), equalTo(TRUE));
		assertThat(plcController.getPlcValue(runRequest), equalTo(TRUE));

		plcController.shutdown();

	}
}
