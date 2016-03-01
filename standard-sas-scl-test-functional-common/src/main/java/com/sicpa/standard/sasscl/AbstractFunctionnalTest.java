package com.sicpa.standard.sasscl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.camera.driver.CameraDriverEventCode;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.LangUtils;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.utils.AppUtils;
import com.sicpa.standard.client.common.utils.SingleAppInstanceUtils;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.sasscl.business.coding.ICoding;
import com.sicpa.standard.sasscl.business.statistics.impl.Statistics;
import com.sicpa.standard.sasscl.common.exception.ExceptionHandler;
import com.sicpa.standard.sasscl.common.storage.FileStorage;
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
import com.sicpa.standard.sasscl.devices.camera.simulator.ICodeProvider;
import com.sicpa.standard.sasscl.devices.plc.impl.PlcAdaptor;
import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterAdaptorSimulator;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.simulator.ISimulatorGetEncoder;
import com.sicpa.standard.sasscl.ioc.BeansName;
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

public abstract class AbstractFunctionnalTest extends TestCase {

	public static final String PROFILE_FOLDER = "profiles/test";

	public static final ProductionMode SAS_MODE = new ProductionMode(50, "productionmode.sas", true);
	public static final ProductionMode SCL_MODE = new ProductionMode(51, "productionmode.scl", true);

	protected List<String> dataGenerated = new ArrayList<>();

	protected Statistics statistiscs;

	protected CameraSimulatorController camera;
	protected CameraSimulatorConfig cameraModel;

	protected PrinterAdaptorSimulator printer;

	protected MainAppForFunctionnalTest mainApp;
	protected FlowControl flowControl;
	protected ICoding coding;
	protected ApplicationFlowState currentState;
	protected ExceptionHandler exceptionHandler;
	protected IRemoteServer remoteServer;
	protected CodeType selecCodeType = new CodeType(1);
	protected ProductionParameters productionParameters;

	protected FileStorage storage;

	public PlcAdaptor plc;

	protected IErrorsRepository incidentContext;

	protected IEncoder currentEncoder;

	public AbstractFunctionnalTest() {

		System.out.println("================");
		System.out.println(getClass());
		System.out.println("================");

		mainApp = new MainAppForFunctionnalTest();
	}

	protected abstract ProductionMode getProductionMode();

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

	public void setProductionParameter() {
		setProductionParameter(1, 1, getProductionMode());
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

	private void emptyStorageFolders() {
		try {
			FileUtils.deleteDirectory(new File(PROFILE_FOLDER + "/internalSimulator"));
			FileUtils.deleteDirectory(new File(PROFILE_FOLDER + "/dataSimulator"));
			FileUtils.deleteDirectory(new File(PROFILE_FOLDER + "/internal"));
			FileUtils.deleteDirectory(new File(PROFILE_FOLDER + "/monitoring"));
			FileUtils.deleteDirectory(new File(PROFILE_FOLDER + "/simulProductSend"));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	private void emptyRemoteServerReceivedData() {
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

		LangUtils.initLanguageFiles("en", "sasscl");

		TestHelper.initExecutor();

		emptyStorageFolders();
		emptyRemoteServerReceivedData();
		// GroovyLoggerConfigurator lc = new GroovyLoggerConfigurator(new StringMap());
		// lc.initLogger();
		loadSpring();
	}

	protected void loadSpring() {
		SingleAppInstanceUtils.releaseLock();

		mainApp.load(getClass().getSimpleName());

		statistiscs = BeanProvider.getBean(BeansName.STATISTICS);
		flowControl = BeanProvider.getBean(BeansName.FLOW_CONTROL);
		coding = BeanProvider.getBean(BeansName.CODING);
		storage = BeanProvider.getBean(BeansName.STORAGE);
		ExecutorStarting executorStarting = BeanProvider.getBean("executorStarting");
		executorStarting.setTimeoutDelay(Integer.MAX_VALUE);
		remoteServer = BeanProvider.getBean(BeansName.REMOTE_SERVER);
		exceptionHandler = BeanProvider.getBean(BeansName.EXCEPTION_HANDLER);
		productionParameters = BeanProvider.getBean(BeansName.PRODUCTION_PARAMETERS);
		incidentContext = BeanProvider.getBean(BeansName.ERRORS_REPOSITORY);
		storage = BeanProvider.getBean(BeansName.STORAGE);
		EventBusService.register(this);

		PlcProvider plcProvider = BeanProvider.getBean(BeansName.PLC_PROVIDER);
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
			File folder = new File("profiles/test/SimulProductSend");
			List<String> codes = new ArrayList<>();
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
			try {
				Assert.assertTrue(codes + " should contain " + expected, codes.contains(expected));
			} catch (Throwable e) {
				e.printStackTrace();
				System.out.println();
			}

		}
	}

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
		cameraModel.setCodeGetMethod(CodeGetMethod.requested);
		cameraModel.setReadCodeInterval(-1);

		if (devicesMap.get("pr_scl_1") != null) {
			printer = (PrinterAdaptorSimulator) devicesMap.get("pr_scl_1");
			printer.setAskCodeDelayMs(-1);
			camera.setCodeProvider(printer);
		} else {
			camera.setCodeProvider(new ICodeProvider() {
				@Override
				public String requestCode() {
					try {
						return generateACodeFromEncoder();
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			});
		}

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

	protected void trigProduct(int count) {
		for (int i = 0; i < count; i++) {
			trigProduct();
		}
	}

	protected void trigProduct() {
		trigPrinter();
		camera.readCode();
	}

	protected String trigGood() {
		trigPrinter();
		String code = camera.getGoodCode();
		camera.fireGoodCode(code);
		return code;
	}

	protected String trigBad() {
		trigPrinter();
		String code = camera.getGoodCode();
		camera.fireBadCode(code);
		return code;
	}

	private void trigPrinter() {
		if (printer != null) {
			printer.onPrinterCodesNeeded(null, 1);
		}
	}
}
