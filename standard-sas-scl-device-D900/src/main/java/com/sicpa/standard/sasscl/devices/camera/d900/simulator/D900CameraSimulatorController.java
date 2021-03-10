package com.sicpa.standard.sasscl.devices.camera.d900.simulator;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.camera.d900.controller.D900CameraException;
import com.sicpa.standard.camera.d900.controller.ID900CameraController;
import com.sicpa.standard.camera.d900.controller.ID900CameraControllerListener;
import com.sicpa.standard.camera.d900.controller.ID900CameraImageSaver;
import com.sicpa.standard.camera.d900.driver.D900CameraDriverEventCode;
import com.sicpa.standard.camera.d900.driver.D900EventType;
import com.sicpa.standard.camera.d900.driver.ID900StandardCameraDriver;
import com.sicpa.standard.camera.d900.driver.event.D900CameraDriverEventArgs;
import com.sicpa.standard.camera.d900.driver.event.D900ImageReceivedEventArgs;
import com.sicpa.standard.camera.d900.driver.simulator.ID900CameraDriverSimulatorListener;
import com.sicpa.standard.camera.d900.parser.ID900CameraCodeParser;
import com.sicpa.standard.camera.d900.parser.event.D900CodeEventArgs;
import com.sicpa.standard.camera.d900.parser.event.D900ErrorCodeEventArgs;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.d900.simulator.ID900CameraControllerSimulator;
import com.sicpa.standard.sasscl.devices.d900.simulator.ID900CodeProvider;
import com.sicpa.standard.sasscl.devices.simulator.gui.SimulatorControlView;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.OutputStream;
import java.util.*;
import java.util.List;

import static com.sicpa.standard.gui.utils.ImageUtils.changeSizeKeepRatio;
import static com.sicpa.standard.gui.utils.ImageUtils.createCameraImage;

/**
 * 
 * Implementation for camera simulator.
 * 
 * <p>
 * This implementation simulates the real camera. It simulates the camera by providing codes from a file, or randomly
 * generated alphanumeric code, or a code from printer/application. It is also possible to return an image when the
 * caller requests.
 * </p>
 * 
 * @author CWong
 * 
 */
public class D900CameraSimulatorController implements ID900CameraController<D900CameraSimulatorConfig>,
		ID900CameraControllerSimulator {

	private static final int CAMERA_IMAGE_SIZE_PX = 600;

	private static final Logger logger = LoggerFactory.getLogger(D900CameraSimulatorController.class);

	protected final List<ID900CameraControllerListener> listeners = new ArrayList<>();

	public static int idCount = 0;

	private D900CameraSimulatorConfig config;
	protected ProductionParameters productionParameters;
	private SimulatorControlView simulatorGui;

	private Thread codeThread;
	private ID900CodeProvider codeProvider;
	private volatile boolean running;
	private volatile boolean connected;
	private volatile boolean initCodeProviderDone;
	private volatile boolean appTimedOut;
	private String currentActiveJob = "unset - use pre-defined camera job in camera";
	private String deviceId;

	private final Map<String, String> cellsValue = new HashMap<>();
	private String aliasROI_StartRow;
	private String aliasROI_x;
	private String aliasROI_y;
	private String aliasROI_w;
	private String aliasROI_h;

	public D900CameraSimulatorController() {
		this(new D900CameraSimulatorConfig());
	}

	protected void showGui() {
		ThreadUtils.invokeLater(() -> {
			if (simulatorGui != null) {
				simulatorGui.addSimulator(deviceId, new D900CameraSimulatorView(D900CameraSimulatorController.this));
			}
		});
	}

	protected void hideGui() {
		ThreadUtils.invokeLater(() -> {
			if (simulatorGui != null) {
				simulatorGui.removeSimulator(deviceId);
			}
		});
	}

	public D900CameraSimulatorController(D900CameraSimulatorConfig config) {
		this.config = config;
		deviceId = "D900_" + idCount++;
	}



	public void setCodeProvider(ID900CodeProvider codeProvider) {
		this.codeProvider = codeProvider;
	}

	// A thread to periodically provides camera codes based on the configuration.
	private class CameraSimulatorTask implements Runnable {
		@Override
		public void run() {
			if (config.getReadCodeInterval() >= 0) {
				while (running) {
					readCode();
					ThreadUtils.sleepQuietly(config.getReadCodeInterval());
				}
			}
		}
	}

	public void readCode() {
		// do the getGoodCode outside the if, to keep the same rate of querying all the time, meaning we ask the
		// printer simulator for code even if we want to trigger a bad code to have a closer behavior to
		// reality
		String code = getGoodCode();

		// skip null code to be able to simulate 100% good code when setting percentageBadCode = 0
		if (code == null) {
			return;
		}
		//todo
		//check if sku is found to fire good or bad result
		//initially pass true
		Random random = new Random();
		sendCode(random.nextBoolean(), code);
	}

	private void sendCode(boolean validCode, String code) {
		if (validCode) {
			fireGoodCode(code);
		} else {
			fireBadCode(code);
		}
		fireCameraImageReceived(getCameraImage(), code);
	}

	private boolean shouldGenerateGoodCode(int randomVal) {
		return randomVal  >= config.getPercentageBadCode() && !shouldGenerateBlobDetectedCode(randomVal);
	}

	private boolean shouldGenerateBlobDetectedCode(int randomVal) {
		return randomVal >= (100 - config.getPercentageBlobCode());
	}



	public String getGoodCode() {
		String code;
		if (!initCodeProviderDone) {
			initCodeProvider();
		}
		if (codeProvider != null) {
			code = codeProvider.requestCode();
			return code;
		} else {
			return null;
		}
	}

	protected boolean isCounting() {
		return !productionParameters.getProductionMode().isWithSicpaData();
	}

	private boolean isRefeed() {
		return productionParameters.getProductionMode().equals(ProductionMode.REFEED_CORRECTION)
				|| productionParameters.getProductionMode().equals(ProductionMode.REFEED_NORMAL);
	}

	private void initCodeProvider() {
		// do no need to check for requested, requested are set directly by setCodeProvider

		if (config.getCodeGetMethod() != D900CodeGetMethod.none) {
			if (isCounting()) {
				codeProvider = new D900CountingCodeProvider();
				initCodeProviderDone = true;
			} else if (initWithRandomCodeProvider()) {
				if (!config.getCodeGetMethod().equals(D900CodeGetMethod.file)) {
					codeProvider = new D900RandomCodeProvider();
					initCodeProviderDone = true;
				}
			}
		}
		if (!initCodeProviderDone) {
			switch (config.getCodeGetMethod()) {
			case file:
				EventBusService.post(new MessageEvent(MessageEventKey.Simulator.CAMERA_READ_CODE_FROM_FILE));
				codeProvider = new D900FileCodeProvider(config.getDataFilePath());
				break;
			case requested:// use the one set
				break;
			case none:
				codeProvider = null;
				break;
			case generated:
			default:
				codeProvider = new D900RandomCodeProvider();
			}
			initCodeProviderDone = true;
		}
	}

	private boolean initWithRandomCodeProvider() {
		return isRefeed();
	}

	public void fireGoodCode(final String code) {
		synchronized (listeners) {
			for (ID900CameraControllerListener lis : listeners) {
				lis.onValidCameraCodeReceived(this, new D900CodeEventArgs(code));
			}
		}
	}

	public void fireBadCode(final String code) {
		synchronized (listeners) {
			for (ID900CameraControllerListener lis : listeners) {
				lis.onErrorCameraCodeReceived(this, new D900ErrorCodeEventArgs(code));
			}
		}
	}

	@Override
	public Image getCameraImage() {
		return changeSizeKeepRatio(createCameraImage(), CAMERA_IMAGE_SIZE_PX, CAMERA_IMAGE_SIZE_PX);
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	@Override
	public void addListener(ID900CameraControllerListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}

	}

	public void fireStatusChanged(D900CameraDriverEventCode status) {
		synchronized (listeners) {
			for (ID900CameraControllerListener lis : listeners) {
				lis.onCameraDriverEventReceived(this,
						new D900CameraDriverEventArgs(D900EventType.INFO, status.getCode(), false));
			}
		}
	}

	public void fireCameraImageReceived(Image cameraImage, String code) {
		synchronized (listeners) {
			for (ID900CameraControllerListener lis : listeners) {
				lis.onCameraImageReceived(this, new D900ImageReceivedEventArgs(cameraImage, code));
			}
		}
	}

	@Override
	public void create() {
		EventBusService.post(new MessageEvent(MessageEventKey.Simulator.D900));
		showGui();
		connected = true;
		fireStatusChanged(D900CameraDriverEventCode.CONNECTED);
	}

	@Override
	public void create(final D900CameraSimulatorConfig arg0) {

	}

	@Override
	public boolean downloadJob(final String jobFileName, final OutputStream outputStream)
			throws D900CameraException {
		return false;
	}

	@Override
	public D900CameraSimulatorConfig getCameraModel() {
		return config;
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public void removeListener(ID900CameraControllerListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	@Override
	public void reset() {
	}

	@Override
	public void setActiveJob(String jobFileName) {
		logger.debug("Current camera job name : {}", currentActiveJob);
		currentActiveJob = jobFileName;
		logger.debug("New camera job name : {}", currentActiveJob);
	}

	@Override
	public void setCameraDriverSimulatorListener(final ID900CameraDriverSimulatorListener simulatorListener) {
	}

	@Override
	public void setCameraImageSaver(final ID900CameraImageSaver cameraImageSaver) {
	}

	@Override
	public void setDriver(final ID900StandardCameraDriver driver) {
	}

	@Override
	public void shutdown() {
		hideGui();
		connected = false;
		if (codeThread != null && codeThread.isAlive()) {
			codeThread.interrupt();
			try {
				codeThread.join(10000);
			} catch (InterruptedException e) {
				// Ignore this.
			}
		}
		fireStatusChanged(D900CameraDriverEventCode.DISCONNECTED);
	}

	@Override
	public void startReading() throws D900CameraException {
		if (!connected) {
			throw new D900CameraException(
					"Cannot start readin, camera is not connected");
		}

		running = true;
		// check if the thread is started
		if ((codeThread == null || !codeThread.isAlive()) && !appTimedOut) {
			codeThread = createCameraThread();
			codeThread.start();
		}
	}

	private Thread createCameraThread() {
		return new Thread(new CameraSimulatorTask(), "Camera Simulator");
	}

	@Override
	public void stopReading() {
		running = false;
	}

	@Override
	public void uploadActiveJob(final File jobFile) {
	}

	@Override
	public void uploadJobs(final File... files) {
	}

	@Override
	public void setCameraCodeParser(final Class<? extends ID900CameraCodeParser> parserClass) {
	}

	@Override
	public void setCameraCodeParser(final Class<? extends ID900CameraCodeParser> parserClass, final Properties arg1) {
	}

	public void setSimulatorGui(final SimulatorControlView simulatorGui) {
		this.simulatorGui = simulatorGui;
	}

	@Override
	public String getActiveJobName() {
		logger.debug("Returning current camera job name : {}", currentActiveJob);
		return currentActiveJob;
	}

	@Override
	public void setActiveJob(final String jobFileName, final boolean forcedLoadActiveJob) throws D900CameraException {
		setActiveJob(jobFileName);
	}

	private void initCellsValueMap() {
		cellsValue.put(aliasROI_x, "40");
		cellsValue.put(aliasROI_y, "40");
		cellsValue.put(aliasROI_w, "120");
		cellsValue.put(aliasROI_h, "130");
		cellsValue.put(aliasROI_StartRow, "0");// yoffset
	}

	public void setAliasROI_x(String aliasROI_x) {
		this.aliasROI_x = aliasROI_x;
	}

	public void setAliasROI_y(String aliasROI_y) {
		this.aliasROI_y = aliasROI_y;
	}

	public void setAliasROI_w(String aliasROI_w) {
		this.aliasROI_w = aliasROI_w;
	}

	public void setAliasROI_h(String aliasROI_h) {
		this.aliasROI_h = aliasROI_h;
	}

	public void setAliasROI_StartRow(String aliasROI_StartRow) {
		this.aliasROI_StartRow = aliasROI_StartRow;
	}

	@Override
	public String readCellValue(String columnLetter, int rowNo) throws D900CameraException {
		return null;
	}

	@Override
	public void writeCellValueString(String columnLetter, int rowNo, String cellValue) throws D900CameraException {

	}

	@Override
	public void writeCellValueInt(String columnLetter, int rowNo, int cellValue) throws D900CameraException {

	}

	@Override
	public void writeCellValueFloat(String columnLetter, int rowNo, float cellValue) throws D900CameraException {

	}

	@Override
	public void writeAliasValueInt(String alias, int cellValue) throws D900CameraException {
		logger.info("writeAliasValueInt {}={}", alias, "" + cellValue);
	}

	@Override
	public void writeAliasValueString(String alias, String cellValue) throws D900CameraException {
		logger.info("writeAliasValueString {}={}", alias, cellValue);
	}

	@Override
	public void writeAliasValue(String alias, String cellValue) throws D900CameraException {
		logger.info("writeAliasValue {}={}", alias, cellValue);
	}

	@Override
	public String readAliasValue(String alias) throws D900CameraException {
		if (cellsValue.isEmpty()) {
			initCellsValueMap();
		}
		return cellsValue.get(alias);
	}

	@Subscribe
	public void appTimeOutError(MessageEvent messageEvent) {
		if(messageEvent.getKey().equals(MessageEventKey.FlowControl.START_TIMEOUT)) {
			logger.debug("App Timeout error occured");
			appTimedOut = true;
		}
			
	}

}
