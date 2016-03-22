package com.sicpa.standard.sasscl.devices.camera.simulator;

import java.awt.Image;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.camera.controller.CameraException;
import com.sicpa.standard.camera.controller.ICameraControllerListener;
import com.sicpa.standard.camera.controller.ICameraImageSaver;
import com.sicpa.standard.camera.controller.ICognexCameraController;
import com.sicpa.standard.camera.driver.CameraDriverEventCode;
import com.sicpa.standard.camera.driver.EventType;
import com.sicpa.standard.camera.driver.ICameraDriver;
import com.sicpa.standard.camera.driver.event.CameraDriverEventArgs;
import com.sicpa.standard.camera.driver.event.ImageReceivedEventArgs;
import com.sicpa.standard.camera.driver.simulator.ICameraDriverSimulatorListener;
import com.sicpa.standard.camera.parser.ICameraCodeParser;
import com.sicpa.standard.camera.parser.event.CodeEventArgs;
import com.sicpa.standard.camera.parser.event.ErrorCodeEventArgs;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.simulator.gui.SimulatorControlView;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;

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
public class CameraSimulatorController implements ICognexCameraController<CameraSimulatorConfig>,
		ICameraControllerSimulator {

	private static final Logger logger = LoggerFactory.getLogger(CameraSimulatorController.class);

	protected final List<ICameraControllerListener> listeners = new ArrayList<>();

	public static int idCount = 0;

	private CameraSimulatorConfig config;
	private ProductionParameters productionParameters;
	private SimulatorControlView simulatorGui;

	private Thread codeThread;
	private ICodeProvider codeProvider;
	private volatile boolean running;
	private volatile boolean connected;
	private volatile boolean initCodeProviderDone;
	private String currentActiveJob = "unset - use pre-defined camera job in camera";
	private String deviceId;

	private final Map<String, String> cellsValue = new HashMap<>();
	private String aliasROI_StartRow;
	private String aliasROI_x;
	private String aliasROI_y;
	private String aliasROI_w;
	private String aliasROI_h;

	private CameraSimuCodeTransformer custoCodesGeneratedTransformer;
	private CameraSimuCodeTransformer defaultCodesGeneratedTransformer = (code) -> defaultCodeTransformerImpl(code);

	public CameraSimulatorController() {
		this(new CameraSimulatorConfig());
	}

	protected void showGui() {
		ThreadUtils.invokeLater(() -> {
			if (simulatorGui != null) {
				simulatorGui.addSimulator(deviceId, new CameraSimulatorView(CameraSimulatorController.this));
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

	public CameraSimulatorController(CameraSimulatorConfig config) {
		this.config = config;
		deviceId = "camera" + idCount++;
	}

	public CameraSimulatorController(CameraSimulatorConfig config, ProductionParameters productionParameters,
			SimulatorControlView simulatorGui) {
		this(config);
		this.productionParameters = productionParameters;
		this.simulatorGui = simulatorGui;
	}

	public void setCodeProvider(ICodeProvider codeProvider) {
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

		Pair<Boolean, String> pair = transformCode(code);
		code = pair.getRight();
		boolean valid = pair.getLeft();
		sendCode(valid, code);
	}

	private void sendCode(boolean validCode, String code) {
		if (validCode) {
			fireGoodCode(code);
		} else {
			fireBadCode(code);
		}
		fireCameraImageReceived(getCameraImage(), code);
	}

	private Pair<Boolean, String> transformCode(String codeGenerated) {
		if (custoCodesGeneratedTransformer != null) {
			return useCustoCodeTransformer(codeGenerated);
		} else {
			return useDefaultCodeTransformer(codeGenerated);
		}
	}

	private Pair<Boolean, String> defaultCodeTransformerImpl(String codeGenerated) {
		String code;
		boolean valid;
		if (StringUtils.isNotBlank(codeGenerated) && shouldGenerateGoodCode()) {
			code = codeGenerated;
			valid = true;
		} else {
			code = "";
			valid = false;
		}
		return Pair.of(valid, code);
	}

	private Pair<Boolean, String> useCustoCodeTransformer(String codeGenerated) {
		return useCodeTransformer(codeGenerated, custoCodesGeneratedTransformer);
	}

	private Pair<Boolean, String> useDefaultCodeTransformer(String codeGenerated) {
		return useCodeTransformer(codeGenerated, defaultCodesGeneratedTransformer);
	}

	private Pair<Boolean, String> useCodeTransformer(String codeGenerated, CameraSimuCodeTransformer transformer) {
		return transformer.apply(codeGenerated);
	}

	private boolean shouldGenerateGoodCode() {
		return new Random().nextInt(100) >= config.getPercentageBadCode();
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

	private boolean isCounting() {
		return !productionParameters.getProductionMode().isWithSicpaData();
	}

	private boolean isRefeed() {
		return productionParameters.getProductionMode().equals(ProductionMode.REFEED_CORRECTION)
				|| productionParameters.getProductionMode().equals(ProductionMode.REFEED_NORMAL);
	}

	private void initCodeProvider() {
		// do no need to check for requested, requested are set directly by setCodeProvider

		if (config.getCodeGetMethod() != CodeGetMethod.none) {
			if (isCounting()) {
				codeProvider = new CountingCodeProvider();
				initCodeProviderDone = true;
			} else if (initWithRandomCodeProvider()) {
				if (!config.getCodeGetMethod().equals(CodeGetMethod.file)) {
					codeProvider = new RandomCodeProvider();
					initCodeProviderDone = true;
				}
			}
		}
		if (!initCodeProviderDone) {
			switch (config.getCodeGetMethod()) {
			case file:
				EventBusService.post(new MessageEvent(MessageEventKey.Simulator.CAMERA_READ_CODE_FROM_FILE));
				codeProvider = new FileCodeProvider(config.getDataFilePath());
				break;
			case requested:// use the one set
				break;
			case none:
				codeProvider = null;
				break;
			case generated:
			default:
				codeProvider = new RandomCodeProvider();
			}
			initCodeProviderDone = true;
		}
	}

	private boolean initWithRandomCodeProvider() {
		return isRefeed();
	}

	public void fireGoodCode(final String code) {
		synchronized (listeners) {
			for (ICameraControllerListener lis : listeners) {
				lis.onValidCameraCodeReceived(this, new CodeEventArgs(code, "", code));
			}
		}
	}

	public void fireBadCode(final String code) {
		synchronized (listeners) {
			for (ICameraControllerListener lis : listeners) {
				lis.onErrorCameraCodeReceived(this, new ErrorCodeEventArgs(0l, code, "", code));
			}
		}
	}

	@Override
	public Image getCameraImage() {
		return ImageUtils.createCameraImage();
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	@Override
	public void addListener(ICameraControllerListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}

	}

	public void fireStatusChanged(CameraDriverEventCode status) {
		synchronized (listeners) {
			for (ICameraControllerListener lis : listeners) {
				lis.onCameraDriverEventReceived(this,
						new CameraDriverEventArgs(EventType.INFO, status.getCode(), false));
			}
		}
	}

	public void fireCameraImageReceived(Image cameraImage, String code) {
		synchronized (listeners) {
			for (ICameraControllerListener lis : listeners) {
				lis.onCameraImageReceived(this, new ImageReceivedEventArgs(cameraImage, code));
			}
		}
	}

	@Override
	public void create() {
		EventBusService.post(new MessageEvent(MessageEventKey.Simulator.CAMERA));
		showGui();
		connected = true;
		fireStatusChanged(CameraDriverEventCode.CONNECTED);
	}

	@Override
	public void create(final CameraSimulatorConfig arg0) {

	}

	@Override
	public boolean downloadJob(final String jobFileName, final OutputStream outputStream)
			throws com.sicpa.standard.camera.controller.CameraException {
		return false;
	}

	@Override
	public CameraSimulatorConfig getCameraModel() {
		return config;
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public void removeListener(ICameraControllerListener listener) {
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
	public void setCameraDriverSimulatorListener(final ICameraDriverSimulatorListener simulatorListener) {
	}

	@Override
	public void setCameraImageSaver(final ICameraImageSaver cameraImageSaver) {
	}

	@Override
	public void setDriver(final ICameraDriver driver) {
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
		fireStatusChanged(CameraDriverEventCode.DISCONNECTED);
	}

	@Override
	public void startReading() throws com.sicpa.standard.camera.controller.CameraException {
		if (!connected) {
			throw new com.sicpa.standard.camera.controller.CameraException(
					"Cannot start readin, camera is not connected");
		}

		running = true;
		// check if the thread is started
		if (codeThread == null || !codeThread.isAlive()) {
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
	public void setCameraCodeParser(final Class<? extends ICameraCodeParser> parserClass) {
	}

	@Override
	public void setCameraCodeParser(final Class<? extends ICameraCodeParser> parserClass, final Properties arg1) {
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
	public void setActiveJob(final String jobFileName, final boolean forcedLoadActiveJob) throws CameraException {
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
	public String readCellValue(String columnLetter, int rowNo) throws CameraException {
		return null;
	}

	@Override
	public void writeCellValueString(String columnLetter, int rowNo, String cellValue) throws CameraException {

	}

	@Override
	public void writeCellValueInt(String columnLetter, int rowNo, int cellValue) throws CameraException {

	}

	@Override
	public void writeCellValueFloat(String columnLetter, int rowNo, float cellValue) throws CameraException {

	}

	@Override
	public void writeAliasValueInt(String alias, int cellValue) throws CameraException {

	}

	@Override
	public void writeAliasValueString(String alias, String cellValue) throws CameraException {

	}

	@Override
	public void writeAliasValue(String alias, String cellValue) throws CameraException {

	}

	@Override
	public String readAliasValue(String alias) throws CameraException {
		if (cellsValue.isEmpty()) {
			initCellsValueMap();
		}
		return cellsValue.get(alias);
	}

	public void setCustoCodesGeneratedTransformer(CameraSimuCodeTransformer custoCodesGeneratedTransformer) {
		this.custoCodesGeneratedTransformer = custoCodesGeneratedTransformer;
	}
}
