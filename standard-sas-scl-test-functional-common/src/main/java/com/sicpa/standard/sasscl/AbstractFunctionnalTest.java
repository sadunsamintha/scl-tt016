package com.sicpa.standard.sasscl;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.camera.driver.CameraDriverEventCode;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.ioc.PropertyPlaceholderResources;
import com.sicpa.standard.client.common.launcher.CommonMainApp.Loader;
import com.sicpa.standard.client.common.launcher.LoaderConfig;
import com.sicpa.standard.client.common.launcher.display.IProgressDisplay;
import com.sicpa.standard.client.common.utils.AppUtils;
import com.sicpa.standard.client.common.utils.LogUtils;
import com.sicpa.standard.client.common.utils.SingleAppInstanceUtils;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.gui.screen.loader.AbstractApplicationLoader;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.business.statistics.impl.Statistics;
import com.sicpa.standard.sasscl.common.exception.ExceptionHandler;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.flow.FlowControl;
import com.sicpa.standard.sasscl.controller.flow.statemachine.executor.ExecutorExit;
import com.sicpa.standard.sasscl.controller.flow.statemachine.executor.ExecutorStarting;
import com.sicpa.standard.sasscl.controller.hardware.ProductionDevicesCreatedEvent;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraAdaptorSimulator;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorConfig;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController;
import com.sicpa.standard.sasscl.devices.camera.simulator.CodeGetMethod;
import com.sicpa.standard.sasscl.devices.plc.impl.PlcAdaptor;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.simulator.ISimulatorGetEncoder;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKeyBad;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKeyGood;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.repository.errors.AppMessage;
import com.sicpa.standard.sasscl.repository.errors.IErrorsRepository;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.test.utils.TestHelper;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.*;

public abstract class AbstractFunctionnalTest extends TestCase {

	protected List<String> dataGenerated = new ArrayList<String>();

	protected Statistics statistiscs;

	protected CameraSimulatorController camera;
	protected CameraSimulatorConfig cameraModel;

	protected MainApp mainApp;
	protected FlowControl flowControl;
	protected ApplicationFlowState currentState;
	protected ExceptionHandler exceptionHandler;
	protected IRemoteServer remoteServer;
	protected CodeType selecCodeType = new CodeType(1);
	protected ProductionParameters productionParameters;

	public PlcAdaptor plc;

	protected IErrorsRepository incidentContext;

	public AbstractFunctionnalTest() {

		System.out.println("================");
		System.out.println(getClass());
		System.out.println("================");

		PropertyPlaceholderResources.addProperties("executorExit", ExecutorExitNoJVMExit.class.getName());

		// needed because we need to be able to control the simulator with a service executor
		PropertyPlaceholderResources.addProperties(BeansName.PRINTER_SIMULATOR,
				"com.sicpa.standard.sasscl.utils.printer.PrinterSimulatorThatProvidesCodes");

		PropertyPlaceholderResources.addProperties("remoteServerSimulator",
				"com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator");

		mainApp = new MainApp() {
			{
				this.progressDisplay = new IProgressDisplay() {
					@Override
					public void setText(String text) {

					}

					@Override
					public void setProgress(int progress) {

					}
				};
			}

			@Override
			public AbstractApplicationLoader createLoader(LoaderConfig config, String ... profiles) {
				return super.createLoader(new LoaderConfig(getSpringConfig(), "functional test:\n"
						+ getClass().getSimpleName(), null), profiles);
			}
		};
	}

	public void exit() {
		flowControl.notifyExitApplication();
	}

	public void startProduction() {
		flowControl.notifyStartProduction();
	}

	public void stopProduction() {
		flowControl.notifyStopProduction();
	}

	public void checkApplicationStatusCONNECTED() {
		checkApplicationStatus(ApplicationFlowState.STT_CONNECTED);
	}

	public void checkApplicationStatusRUNNING() {
		checkApplicationStatus(ApplicationFlowState.STT_STARTED);
	}

	public void checkApplicationStatusRECOVERING() {
		checkApplicationStatus(ApplicationFlowState.STT_RECOVERING);
	}

	protected void checkApplicationStatus(ApplicationFlowState status) {
		TestHelper.runAllTasks();
		System.out.println("current state:" + currentState);
		if (!currentState.equals(status)) {
			Assert.fail("wrong status, application status:" + currentState + " expected:" + status);
		}

	}

	public void setProductionParameter(int skuId, int codeTypeId, ProductionMode mode) {
		flowControl.notifyEnterSelectionScreen();
		SKU sku = new SKU(skuId, "SKU#" + skuId);
		sku.setCodeType(selecCodeType = new CodeType(codeTypeId));
		productionParameters.setSku(sku);
		productionParameters.setProductionMode(mode);
		EventBusService.post(new ProductionParametersEvent(productionParameters));
		TestHelper.runAllTasks();
	}

	public void disconnectCamera() {
		camera.stopReading();
		camera.fireStatusChanged(CameraDriverEventCode.DISCONNECTED);
	}

	public void disconnectPlc() {
		plc.disconnect();
	}

	public void connectPlc() throws DeviceException {
		plc.connect();
	}

	public void connectCamera() {
		camera.fireStatusChanged(CameraDriverEventCode.CONNECTED);
	}

	protected void emptyStorageFolders() {
		try {
			File folder = new File("data");
			if (folder.exists()) {
				FileUtils.deleteDirectory(folder);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		try {
			File folder = new File("internal");
			if (folder.exists()) {
				FileUtils.deleteDirectory(folder);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	protected void emptyRemoteServerReceivedData() {
		try {
			File folder = new File("SimulProductSend");
			if (folder.exists()) {
				FileUtils.deleteDirectory(folder);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	public void init() {

		if (!AppUtils.isHeadless()) {
			SicpaLookAndFeel.install();
		}

		TestHelper.initExecutor();

		emptyStorageFolders();
		emptyRemoteServerReceivedData();
		LogUtils.initLogger();
		loadSpring();
	}

	@SuppressWarnings("rawtypes")
	protected void loadSpring() {
		SingleAppInstanceUtils.releaseLock();

		String[] profiles = new String[1];
		try {
			String fileName = "config/global.properties";
			URL url = ClassLoader.getSystemResource(fileName);
			File f = (url == null) ?  new File(fileName) : new File(url.toURI());
			Properties properties = new Properties();
			properties.load(new BufferedReader(new FileReader(f)));

			String key = "plcSecure.behavior";
			profiles[0] = key + "." + (String) properties.get(key);

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		final Loader loader = (Loader) mainApp.createLoader(new LoaderConfig(getSpringConfig(), "functional test:\n"
				+ getClass().getSimpleName(), null), profiles);
		loader.loadApplication();
		ThreadUtils.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				loader.done();
			}
		});

		statistiscs = BeanProvider.getBean(BeansName.STATISTICS);

		flowControl = BeanProvider.getBean(BeansName.FLOW_CONTROL);

		ExecutorStarting executorStarting = BeanProvider.getBean("executorStarting");
		executorStarting.setTimeoutDelay(Integer.MAX_VALUE);

		remoteServer = BeanProvider.getBean(BeansName.REMOTE_SERVER);
		exceptionHandler = BeanProvider.getBean(BeansName.EXCEPTION_HANDLER);

		productionParameters = BeanProvider.getBean(BeansName.PRODUCTION_PARAMETERS);

		incidentContext = BeanProvider.getBean(BeansName.ERRORS_REPOSITORY);

		EventBusService.register(this);

		final PlcProvider plcProvider = BeanProvider.getBean(BeansName.PLC_PROVIDER);
		plcProvider.addChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				plc = (PlcAdaptor) plcProvider.get();
			}
		});
	}

	@Subscribe
	public void handleApplicationFlowStateChanged(ApplicationFlowStateChangedEvent evt) {
		currentState = evt.getCurrentState();
	}

	public void checkWarningMessage(final String key) {
		boolean contains = false;
		for (AppMessage msg : incidentContext.getApplicationWarnings()) {
			if (msg.getMessage().equals(key)) {
				contains = true;
				break;
			}
		}
		for (AppMessage msg : incidentContext.getApplicationErrors()) {
			if (msg.getMessage().equals(key)) {
				contains = true;
				break;
			}
		}
		assertTrue("warning not present:" + key, contains);
	}

	public List<String> getDataFromRemoteServer() {
		try {
			File folder = new File("SimulProductSend");
			List<String> codes = new ArrayList<String>();
			for (File f : folder.listFiles()) {
				String content = FileUtils.readFileToString(f);
				for (String line : content.split("\n")) {
					String s = getDataFromRemoteServerSimulatorFile(line);
					if (s != null) {
						codes.add(s);
					}
				}
			}
			return codes;
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		return null;
	}

	// AUTHENTICATED 2010/12/22-10:31:08 1293010267642 G000 SKU#1
	public String getDataFromRemoteServerSimulatorFile(String line) {
		String[] data = line.split(" ");
		if (data.length > 4) {
			return data[0] + data[3] + data[4];
		} else {
			return null;
		}
	}

	public void checkDataSentToRemoteServer() {
		TestHelper.runAllTasks();
		List<String> codes = getDataFromRemoteServer();
		for (String expected : dataGenerated) {
			Assert.assertTrue(codes.contains(expected));
		}
	}

	public abstract SpringConfig getSpringConfig();

	protected IEncoder currentEncoder;

	public String generateACodeFromEncoder() throws Exception {
		if (currentEncoder == null) {
			currentEncoder = ((ISimulatorGetEncoder) remoteServer).getEncoder(selecCodeType);
		}
		return currentEncoder.getEncryptedCodes(1).get(0);
	}

	protected Map<String, IStartableDevice> devicesMap = new HashMap<String, IStartableDevice>();

	@Subscribe
	public void handleDevicesCreated(ProductionDevicesCreatedEvent evt) {
		for (IStartableDevice dev : evt.getDevices()) {
			devicesMap.put(dev.getName(), dev);
		}
		configureDevices();
	}

	protected void configureDevices() {
		CameraAdaptorSimulator cameraDevice = (CameraAdaptorSimulator) devicesMap.get("qc_1");
		camera = (CameraSimulatorController) cameraDevice.getSimulatorController();
		cameraModel = camera.getCameraModel();
		cameraModel.setCodeGetMethod(CodeGetMethod.none);
	}

	public static class ExecutorExitNoJVMExit extends ExecutorExit {
		@Override
		protected void exitJVM() {
		}
	}

	public void runAllTasks() {
		TestHelper.runAllTasks();
	}

	public void checkStatistics(int expectedGood, int exceptedBad) {

		StatisticsKeyGood keyGood = new StatisticsKeyGood();
		keyGood.setLine("qc_1");

		StatisticsKeyBad keybad = new StatisticsKeyBad();
		keybad.setLine("qc_1");

		int good = statistiscs.getValues().get(keyGood);
		int bad = statistiscs.getValues().get(keybad);
		Assert.assertEquals(expectedGood, good);
		Assert.assertEquals(exceptedBad, bad);
	}
}
