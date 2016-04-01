package com.sicpa.standard.sasscl.devices.remote.simulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.exception.InitializationRuntimeException;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.remote.AbstractRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.GlobalMonitoringToolInfo;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.simulator.stdCrypto.bean.DescriptorBean;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.StdCryptoAuthenticatorWrapperSimulator;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.StdCryptoEncoderWrapperSimulator;
import com.sicpa.standard.sasscl.devices.simulator.gui.SimulatorControlView;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.generator.FileSequenceStorageProvider;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.EncoderNoEncryptionSimulator;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.standard.sicpadata.api.business.BSicpadataModuleFactory;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataModule;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataReader;
import com.sicpa.standard.sicpadata.api.business.IKeyManager;
import com.sicpa.standard.sicpadata.api.business.KeyManagerFactory;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;
import com.sicpa.standard.sicpadata.api.exception.UnknownModeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownSystemTypeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownVersionException;
import com.sicpa.standard.sicpadata.api.model.ModeModel;
import com.sicpa.standard.sicpadata.api.model.SicpadataModel;
import com.sicpa.standard.sicpadata.api.model.VersionModel;
import com.sicpa.standard.sicpadata.api.model.field.FieldModel;
import com.sicpa.standard.sicpadata.preset.ICoreModelPreset;
import com.sicpa.standard.sicpadata.preset.std.StdCoreModelPreset;
import com.sicpa.standard.sicpadata.preset.std.StdVersioningPreset;
import com.sicpa.standard.sicpadata.spi.manager.IServiceProviderManager;
import com.sicpa.standard.sicpadata.spi.manager.ServiceProviderException;
import com.sicpa.standard.sicpadata.spi.password.NoSuchPasswordException;

public class RemoteServerSimulator extends AbstractRemoteServer implements ISimulatorGetEncoder {

	private final static Logger logger = LoggerFactory.getLogger(RemoteServerSimulator.class);

	protected final RemoteServerSimulatorModel simulatorModel;
	private SimulatorControlView simulatorGui;
	private String remoteServerSimulatorOutputFolder;
	private ProductionParameters productionParameters;
	private IStorage storage;
	private IServiceProviderManager serviceProviderManager;
	private FileSequenceStorageProvider fileSequenceStorageProvider;

	// specify the starting year, used in date calculation (value to be set in {@link DescriptorBean#setDate(long)}.
	private int cryptoStartYear = 2010;
	private int cryptoVersion = 0;
	private String cryptoMode = "MY_MODE";
	private StdCoreModelPreset cryptoModelPreset = StdCoreModelPreset.CRYPTO_12x26;

	private int currentEncoderIndex = 0;
	private volatile boolean setupBusinessCryptoDone = false;
	private IBSicpadataModule sicpadataModule;

	public IServiceProviderManager getServiceProviderManager() {
		return serviceProviderManager;
	}

	public void setServiceProviderManager(IServiceProviderManager serviceProviderManager) {
		this.serviceProviderManager = serviceProviderManager;
	}

	public void setCryptoModelPreset(StdCoreModelPreset cryptoModelPreset) {
		this.cryptoModelPreset = cryptoModelPreset;
	}

	public void setCryptoMode(String cryptoMode) {
		this.cryptoMode = cryptoMode;
	}

	public void setCryptoVersion(int cryptoVersion) {
		this.cryptoVersion = cryptoVersion;
	}

	public void setCryptoStartYear(int cryptoStartYear) {
		this.cryptoStartYear = cryptoStartYear;
	}

	public synchronized void setupBusinessCrypto() {
		try {
			if (setupBusinessCryptoDone) {
				return;
			}

			// PREPARE THE MODEL
			List<FieldModel> fixedFieldModels = new ArrayList<>(Arrays.asList(//
					new FieldModel("batchId", (1L << 10) - 1L),//
					new FieldModel("codeType", (1L << 10) - 1L),//
					new FieldModel("date", (1L << 19) - 1L)));//
			List<FieldModel> varFieldModels = new ArrayList<>(Arrays.asList(//
					new FieldModel("sequence", (1L << 39) - 1L)//
					));//

			VersionModel versionModel = new VersionModel(fixedFieldModels, varFieldModels, cryptoModelPreset);

			ModeModel modeModel = new ModeModel(StdVersioningPreset.ONE_DIGIT);
			modeModel.addVersionModel(cryptoVersion, versionModel);

			SicpadataModel sicpadataModel = new SicpadataModel();
			sicpadataModel.addModeModel(cryptoMode, modeModel);

			// PUT THE MODEL
			sicpadataModule = null;
			try {
				sicpadataModule = BSicpadataModuleFactory.getInstance(serviceProviderManager);
				sicpadataModule.setSicpadataModel(sicpadataModel);

				// GENERATE THE KEYS
				IKeyManager keyManager = null;
				keyManager = KeyManagerFactory.getInstance(serviceProviderManager);
				keyManager.generateKeyset(cryptoMode, cryptoVersion);

			} catch (Exception e) {
				throw new RemoteServerException(e);
			}
			setupBusinessCryptoDone = true;
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public RemoteServerSimulator(RemoteServerSimulatorModel model) {
		this.simulatorModel = model;
	}

	public RemoteServerSimulator(String configFile) throws RemoteServerException {
		try {
			simulatorModel = ConfigUtils.load(RemoteServerSimulatorModel.class, configFile);
		} catch (Exception e) {
			throw new InitializationRuntimeException("Failed to load remote server simulator model", e);
		}
	}

	private IBSicpadataGenerator generateEncoders(long batchId, long codeType) throws UnknownModeException,
			UnknownVersionException, UnknownSystemTypeException, SicpadataException {

		// setup descriptor bean
		DescriptorBean descBean = new DescriptorBean();

		descBean.setBatchId(batchId);
		descBean.setDate(getNumberOfYearSinceProjectStart());
		descBean.setType(codeType);

		IBSicpadataGenerator generator = sicpadataModule.createSicpadataGenerator(cryptoMode, cryptoVersion,
				ICoreModelPreset.DEFAULT_SYSTEM_TYPE, descBean);

		generator.setId(batchId);

		return generator;
	}

	/**
	 * Generate and return an instance of authenticator using standard crypto business.
	 */
	@Override
	public IAuthenticator getAuthenticator() throws RemoteServerException {

		if (simulatorModel == null) {
			throw new RemoteServerException("Remote server model is not set");
		}

		setupBusinessCrypto();

		if (simulatorModel.isUseCrypto()) {
			IBSicpadataReader authenticator = null;
			try {
				authenticator = sicpadataModule.createSicpadataReader(ICoreModelPreset.DEFAULT_SYSTEM_TYPE);
				return new StdCryptoAuthenticatorWrapperSimulator(authenticator, cryptoFieldsConfig);
			} catch (Exception e) {
				throw new RemoteServerException(e);
			}

		} else {
			AcceptAllAuthenticator auth = new AcceptAllAuthenticator();
			auth.setProductionParameters(productionParameters);
			return auth;
		}
	}

	private final long getNumberOfYearSinceProjectStart() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		return year - cryptoStartYear;
	}

	/**
	 * Generates encoders based on the batches quantity. This method uses the standard crypto business to generate a
	 * list of the business encoder - <code>IBEncoder</code>. This list of <code>IBEncoder</code> is wrapped in the
	 * StdCryptoEncoderWrapper to be returned
	 */
	@Override
	public void downloadEncoder(int quantity, CodeType codeType, int year) throws RemoteServerException {

		if (simulatorModel == null) {
			throw new RemoteServerException("Remote server model is not set");
		}
		if (quantity == 0) {
			return;
		}

		setupBusinessCrypto();

		try {
			for (int i = 0; i < quantity; i++) {
				currentEncoderIndex++;
				logger.debug("Generating encoders for batch number : {}", currentEncoderIndex);
				IEncoder encoder = createOneEncoder(year, (int) codeType.getId());
				storeEncoder(encoder, year);
				storeSequence(encoder, 0);
			}

		} catch (Exception e) {
			throw new RemoteServerException("Failed to generate encoders", e);
		}
	}

	protected void storeEncoder(IEncoder encoder, int year) {
		storage.saveEncoders(year, encoder);
		storage.confirmEncoder(encoder.getId());

	}

	private void storeSequence(IEncoder encoder, long sequence) {
		try {
			fileSequenceStorageProvider.storeSequence(encoder.getId(), sequence);
		} catch (ServiceProviderException e) {
			logger.error("", e);
		}
	}

	public IEncoder createOneEncoder(int year, int codeTypeId) throws UnknownModeException, UnknownVersionException,
			UnknownSystemTypeException, SicpadataException, NoSuchPasswordException {
		IEncoder encoder;
		if (simulatorModel.isUseCrypto()) {
			encoder = createCryptoEncoder(year, codeTypeId);
		} else {
			encoder = createNoEncryptionEncoder(year, codeTypeId);
		}
		encoder.setOnClientDate(new Date());
		return encoder;
	}

	private IEncoder createCryptoEncoder(int year, int codeTypeId) throws UnknownModeException,
			UnknownVersionException, UnknownSystemTypeException, SicpadataException {
		IBSicpadataGenerator bEncoder = generateEncoders(currentEncoderIndex, codeTypeId);
		int id = new Random().nextInt(Integer.MAX_VALUE);
		return new StdCryptoEncoderWrapperSimulator(id, id, bEncoder, year, getSubsystemID(),
				simulatorModel.getNumberOfCodesByEncoder(), cryptoFieldsConfig, codeTypeId);
	}

	private IEncoder createNoEncryptionEncoder(int year, int codeTypeId) {
		return new EncoderNoEncryptionSimulator(0, new Random().nextInt(Integer.MAX_VALUE), 0,
				simulatorModel.getNumberOfCodesByEncoder(), year, getSubsystemID(), codeTypeId);
	}

	@Override
	public ProductionParameterRootNode getTreeProductionParameters() throws RemoteServerException {
		if (simulatorModel == null) {
			throw new RemoteServerException("Remote server model is not set");
		}
		return simulatorModel.getProductionParameters();
	}

	@Override
	public void doConnect() {
		EventBusService.post(new MessageEvent(MessageEventKey.Simulator.REMOTE_SERVER));
		setupBusinessCrypto();
		showGui();
		fireDeviceStatusChanged(DeviceStatus.CONNECTED);
	}

	@Override
	public void doDisconnect() {
		fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
	}

	protected static class StatusAndSkuKey {
		ProductStatus status;
		SKU sku;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.sku == null) ? 0 : this.sku.hashCode());
			result = prime * result + ((this.status == null) ? 0 : this.status.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			StatusAndSkuKey other = (StatusAndSkuKey) obj;
			if (this.sku == null) {
				if (other.sku != null) {
					return false;
				}
			} else if (!this.sku.equals(other.sku)) {
				return false;
			}
			if (this.status == null) {
				if (other.status != null) {
					return false;
				}
			} else if (!this.status.equals(other.status)) {
				return false;
			}
			return true;
		}

		public StatusAndSkuKey(final ProductStatus status, final SKU sku) {
			this.status = status;
			this.sku = sku;
		}

		@Override
		public String toString() {
			return this.status + " " + this.sku;
		}
	}

	@Override
	public void sendEncoderInfos(List<EncoderInfo> infos) throws RemoteServerException {
		for (EncoderInfo info : infos) {
			logger.info("encoder info sent: {}", info);
		}
	}

	@Override
	public void sendProductionData(final PackagedProducts products) throws RemoteServerException {

		MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventType.LAST_SENT_TO_REMOTE_SERVER, products
				.getProducts().size() + ""));

		File dir = new File(getRemoteServerSimulatorOutputFolder());
		dir.mkdirs();

		SimpleDateFormat timeStampFormatFileName = new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss-SSS");
		SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
		File f = new File(getRemoteServerSimulatorOutputFolder() + File.separator
				+ timeStampFormatFileName.format(new Date()));

		Map<StatusAndSkuKey, Integer> countBySkuAndStatus = new HashMap<>();

		String productsOut = "";
		for (Product p : products.getProducts()) {
			productsOut += products.getProductStatus();
			productsOut += " " + timeStampFormat.format(p.getActivationDate());
			productsOut += " " + products.getProductionBatchId();
			productsOut += " " + p.getCode();
			productsOut += " " + p.getSku();
			productsOut += "\n";
			StatusAndSkuKey key = new StatusAndSkuKey(p.getStatus(), p.getSku());
			Integer count = countBySkuAndStatus.get(key);
			if (count == null) {
				count = 0;
			}
			count++;
			countBySkuAndStatus.put(key, count);
		}
		String output = "";
		for (Entry<StatusAndSkuKey, Integer> entry : countBySkuAndStatus.entrySet()) {
			output += entry.getKey() + " count=" + entry.getValue() + "\n";
		}

		output += productsOut;

		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			f.createNewFile();
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			bw.write(output);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void showGui() {
		ThreadUtils.invokeLater(() -> simulatorGui.addSimulator("remote server", new RemoteServerSimulatorView(this)));
	}

	public void setSimulatorGui(final SimulatorControlView simulatorGui) {
		this.simulatorGui = simulatorGui;
	}

	@Override
	public Map<String, ? extends ResourceBundle> getLanguageBundles() throws RemoteServerException {
		return null;
	}

	public String getRemoteServerSimulatorOutputFolder() {
		return this.remoteServerSimulatorOutputFolder;
	}

	public void setRemoteServerSimulatorOutputFolder(final String remoteServerSimulatorOutputFolder) {
		this.remoteServerSimulatorOutputFolder = remoteServerSimulatorOutputFolder;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}

	@Override
	public IEncoder getEncoder(CodeType codeType) {
		try {
			setupBusinessCrypto();
			return createOneEncoder(Calendar.getInstance().get(Calendar.YEAR), (int) codeType.getId());
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	@Override
	public long getSubsystemID() {
		return 123456;
	}

	@Override
	public void sendInfoToGlobalMonitoringTool(GlobalMonitoringToolInfo info) {
		logger.info("Info sent to Global Monitoring Tool Production Started:{}", info.isProductionStarted());
	}

	public void setFileSequenceStorageProvider(FileSequenceStorageProvider fileSequenceStorageProvider) {
		this.fileSequenceStorageProvider = fileSequenceStorageProvider;
	}

	@Override
	public void lifeCheckTick() {
	}

	public RemoteServerSimulatorModel getSimulatorModel() {
		return simulatorModel;
	}

}
