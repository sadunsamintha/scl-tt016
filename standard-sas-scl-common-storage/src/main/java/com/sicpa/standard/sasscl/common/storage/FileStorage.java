package com.sicpa.standard.sasscl.common.storage;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.storage.ISimpleFileStorage;
import com.sicpa.standard.client.common.storage.StorageException;
import com.sicpa.standard.client.common.utils.DateUtils;
import com.sicpa.standard.client.common.utils.ZipUtils;
import com.sicpa.standard.sasscl.common.exception.ProductionRuntimeException;
import com.sicpa.standard.sasscl.common.storage.productPackager.IProductsPackager;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.*;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.generator.FinishedEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.IFinishedEncoder;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.standard.sicpadata.spi.sequencestorage.FileSequenceStorageProvider;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.MessageFormat;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * storage implementation using files
 * 
 * @author DIelsch
 * 
 */
public class FileStorage implements IStorage {

	private final static Logger logger = LoggerFactory.getLogger(IStorage.class);

	private final String dataFolder;
	private final String internalFolder;
	private final String quarantineFolder;
	public static final String FILE_ENCODER = "encoder-{0}-{1}-{2}-{3}.data";
	public static final String FILE_FINISHED_ENCODER = "encoder-finished-{0}-{1}-{2}.data";
	public static final String FILE_FINISHED_ENCODER_REGEX = "encoder-finished-{0}-[0-9]*-[0-9]*-[0-9]*--[0-9]*-[0-9]*-[0-9]*-[0-9]*-[0-9]*.data";
	public static final String FILE_ENCODER_REGEX = "encoder-{0}-{1}-[0-9]*-[0-9]*-[0-9]*--[0-9]*-[0-9]*-[0-9]*-[0-9]*-[0-9]*.data";
	public static final String FILE_CURRENT_ENCODER_REGEX = "currentEncoder-CodeType[0-9]*.data";
	public static final String FILE_CURRENT_ENCODER = "currentEncoder-{0}.data";
	public static final String FILE_DECODER = "decoder.data";
	public static final String FILE_PRODUCTION_PARAMETERS = "productionParameters.data";
	public static final String FILE_STATISTICS = "statistics.data";
	public static final String FILE_SELECTED_PRODUCTION_PARAMS = "selectedProductionParam.data";
	public static final String FOLDER_PRODUCTION_SAVED = "buffer";
	public static final String FOLDER_PRODUCTION_PACKAGED = "packaged";
	public static final String FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER = "sent";
	public static final String FOLDER_PRODUCTION = "products";
	public static final String FOLDER_ENCODER_BUFFER = "encoders/buffer";
	public static final String FOLDER_ENCODER_CURRENT = "encoders/current";
	public static final String FOLDER_ENCODER_PENDING = "encoders/pending";
	public static final String FOLDER_ENCODER_FINISHED_PENDING = "encoders/finished/pending";
	public static final String FOLDER_ENCODER_FINISHED_CONFIRMED = "encoders/finished/confirmed";

	private File productionBatchBeingSend;
	private int cleanUpSendDataThreshold_day;

	private String timeStampFormat = "yyyy-MM-dd--HH-mm-ss-SSS";

	private IProductsPackager productsPackager;
	private ISimpleFileStorage storageBehavior;
	private FileSequenceStorageProvider fileSequenceProvider;

	// this lock is used to avoid having 2 operations at the same time on the same encoders (from buffer or current
	// others are not critical)
	private final Object encoderLock = new Object();

	/**
	 * @param baseFolder
	 *            root of the storage, mainly used for unit test, for normal case use the default constructor
	 */
	public FileStorage(final String baseFolder, String internalFolder, String quarantineFolder,
			ISimpleFileStorage storageBehavior) {
		this.quarantineFolder = quarantineFolder;
		this.dataFolder = baseFolder;
		this.internalFolder = internalFolder;
		this.storageBehavior = storageBehavior;
	}

	public void setStorageBehavior(ISimpleFileStorage storageBehavior) {
		this.storageBehavior = storageBehavior;
	}

	@Override
	public void saveCurrentEncoder(final IEncoder encoder) {
		synchronized (encoderLock) {
			String file = getCurrentEncoderFile(encoder.getCodeTypeId());
			logger.info("Saving current encoder id:{} seq{}", encoder.getId(), getSequenceOfEncoder(encoder));
			try {
				saveInternal(encoder, file);
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	private String getCurrentEncoderFile(long l) {
		return FOLDER_ENCODER_CURRENT + "/" + MessageFormat.format(FILE_CURRENT_ENCODER, "CodeType" + l);
	}

	@Override
	public void saveEncoders(int year, IEncoder... encoders) {

		String stringyear = "xxxx";

		synchronized (encoderLock) {
			// each encoder are saved to a different file
			try {
				String stime = DateUtils.format(this.timeStampFormat, new Date());
				for (int i = 0; i < encoders.length; i++) {
					String f = FOLDER_ENCODER_PENDING
							+ "/"
							+ MessageFormat.format(FILE_ENCODER, "CodeType" + encoders[i].getCodeTypeId(), stringyear,
									stime, i);
					logger.info("Saving encoder id:{} seqence:{}", encoders[i].getId(),
							getSequenceOfEncoder(encoders[i]));
					saveInternal(encoders[i], f);
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	protected long getSequenceOfEncoder(IEncoder encoder) {
		Long res = null;
		try {
			res = fileSequenceProvider.loadSequence(Long.valueOf(encoder.getId()));
			if (res == null) {
				res = 0l;
			}
		} catch (Exception e) {
			logger.error("error getting the encoder sequence, id:" + encoder.getId(), e);
			res = -1l;
		}
		return res;
	}

	private FilenameFilter getFileFilterForEncoder(CodeType codeType, int year) {
		return new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				// year to string to avoid number formating
				String stringyear = "xxxx";
				String s = MessageFormat.format(FILE_ENCODER_REGEX, "CodeType" + codeType.getId(), stringyear);
				return Pattern.matches(s, name);
			}
		};
	}

	protected void saveFinishedEncoder(IEncoder encoder) {
		if (encoder == null) {
			// possible if current encoder is null
			return;
		}

		IFinishedEncoder finished = new FinishedEncoder(encoder, false);
		String stime = DateUtils.format(timeStampFormat, new Date());
		String encoderFile = MessageFormat.format(FILE_FINISHED_ENCODER, String.valueOf(encoder.getId()), "CodeType"
				+ encoder.getCodeTypeId(), stime);
		try {
			saveInternal(finished, FOLDER_ENCODER_FINISHED_PENDING + "/" + encoderFile);
		} catch (StorageException e) {
			logger.error("fail to save finished encoder " + encoderFile, e);
		}
	}

	/**
	 * 
	 * sort the passed in files to use the oldest file, deserialize the file to encoder and set the encoder as current
	 * encoder
	 */
	private IEncoder findAndSetCurrentEncoder(File[] encoderFiles, CodeType codeType) {

		if (encoderFiles == null || encoderFiles.length == 0) {
			return null;
		}

		File encoderFile = getOldestEncoderFile(encoderFiles);
		try {

			// load this encoder and save it as the current one
			IEncoder encoder = (IEncoder) load(encoderFile);
			
			if (encoder != null) {
				saveCurrentEncoder(encoder);
				// the encoder is now the current encoder, so remove the old
				// file
				if (!encoderFile.delete()) {
					throw new ProductionRuntimeException("error removing encoder file {0}", "encoder:"
							+ encoderFile.getAbsolutePath());
				}
				return encoder;
			}
		} catch (StorageException e) {
			logger.error("", e);
		}
		return null;
	}

	private File getOldestEncoderFile(File[] encoderFiles) {
		TreeSet<File> sortedFile = new TreeSet<>();
		for (File file : encoderFiles) {
			sortedFile.add(file);
		}
		return sortedFile.iterator().next();
	}

	/**
	 * 
	 * useNextEncoder first looks for recycle code encoders. It returns the oldest recycle code encoders if it finds
	 * any. If not, it looks for the oldest encoder from the servers.
	 * 
	 * @param codeType
	 *            Current code type
	 * @return instance of encoder that can be used as next encoder. Return null if no encoder is found
	 */
	@Override
	public IEncoder useNextEncoder(CodeType codeType) {
		synchronized (encoderLock) {
			saveFinishedEncoder(getCurrentEncoder(codeType));
			deleteCurrentEndoder(codeType);
			return getNextEncoder(codeType);
		}
	}

	private IEncoder getNextEncoder(CodeType codeType) {
		int year = Calendar.getInstance().get(Calendar.YEAR);

		File[] encoderFiles = new File(internalFolder + "/" + FOLDER_ENCODER_BUFFER).listFiles(this
				.getFileFilterForEncoder(codeType, year));
		if (encoderFiles != null && encoderFiles.length > 0) {
			IEncoder enc = findAndSetCurrentEncoder(encoderFiles, codeType);
			logger.info("using next encoder codeType:{}  id:{}", codeType.getId(), enc.getId());
			return enc;
		}

		return null;
	}

	private void deleteCurrentEndoder(CodeType codeType) {
		new File(internalFolder + "/" + getCurrentEncoderFile(codeType.getId())).delete();
	}

	@Override
	public IEncoder getCurrentEncoder(CodeType codeType) {
		synchronized (encoderLock) {
			String file = getCurrentEncoderFile(codeType.getId());
			logger.debug("Loading {} {}", "encoder", file);
			try {
				return (IEncoder) loadInternal(file);
			} catch (Exception e) {
				logger.error("fail to get current encoder", e);
				// if there is a problem getting the current encoder, the call
				// should then call useNextEncoder
				File f = new File(internalFolder + "/" + file);
				if (f.exists()) {
					moveToQuarantine(f, QuarantineReason.LOAD_ERROR);
				}
			}
			return null;
		}
	}

	@Override
	public void saveAuthenticator(IAuthenticator auth) {
		logger.debug("Saving {} {}", "decoder", FILE_DECODER);
		try {
			saveInternal(auth, FILE_DECODER);
		} catch (Exception e) {
			logger.error("Cannot save decoder", e);
		}
	}

	@Override
	public IAuthenticator getAuthenticator() {
		logger.debug("Loading {} {}", "decoder", FILE_DECODER);
		try {
			return (IAuthenticator) loadInternal(FILE_DECODER);
		} catch (Exception e) {
			logger.error("Cannot load decoder", e);
			return null;
		}
	}

	@Override
	public void saveProductionParameters(ProductionParameterRootNode node) {
		logger.debug("Saving {} {}", "Production parameters", FILE_PRODUCTION_PARAMETERS);
		try {
			saveInternal(node, FILE_PRODUCTION_PARAMETERS);
		} catch (Exception e) {
			logger.error("Cannot save production parameters", e);
		}
	}

	@Override
	public ProductionParameterRootNode getProductionParameters() {
		logger.debug("Loading {} {}", "Production parameters", FILE_PRODUCTION_PARAMETERS);
		try {
			return (ProductionParameterRootNode) loadInternal(FILE_PRODUCTION_PARAMETERS);
		} catch (Exception e) {
			if (e.getCause() instanceof FileNotFoundException) {
				logger.warn("Cannot load production parameters (" + e.getCause().getMessage() + ")");
			} else {
				logger.error("Cannot load production parameters", e);
			}
			return null;
		}
	}

	@Override
	public void saveSelectedProductionParamters(final ProductionParameters param) {
		logger.debug("Saving {} {}", "SelectedProductionParamters", FILE_SELECTED_PRODUCTION_PARAMS);
		try {
			saveInternal(param, FILE_SELECTED_PRODUCTION_PARAMS);
		} catch (Exception e) {
			logger.error("Cannot save production parameters", e);
		}
	}

	@Override
	public ProductionParameters getSelectedProductionParameters() {
		logger.debug("Loading {} {}", "SelectedProductionParamters", FILE_SELECTED_PRODUCTION_PARAMS);
		try {
			return (ProductionParameters) loadInternal(FILE_SELECTED_PRODUCTION_PARAMS);
		} catch (Exception e) {
			if (e.getCause() instanceof FileNotFoundException) {
				logger.warn("Cannot load production parameters (" + e.getCause().getMessage() + ")");
			} else {
				logger.error("Cannot load production parameters", e);
			}
			return null;
		}
	}

	@Override
	public void saveStatistics(final StatisticsValues stats) {
		logger.debug("Saving {} {}", "Statistics", FILE_STATISTICS);
		try {
			saveData(stats, FILE_STATISTICS);
		} catch (Exception e) {
			logger.error("Cannot save statistics", e);
		}
	}

	@Override
	public StatisticsValues getStatistics() {
		logger.debug("Loading {} {}", "Statistics", FILE_STATISTICS);

		try {
			return (StatisticsValues) loadData(FILE_STATISTICS);
		} catch (Exception e) {
			/*
			 * A file not found exception is always thrown in the application's first execution (previous statistics
			 * don't exists), this case is considered a warning, but others are errors and the stack trace is printed
			 * for debugging.
			 */
			if (e.getCause() instanceof FileNotFoundException) {
				logger.warn("Cannot load statistics (" + e.getCause().getMessage() + ")");
			} else {
				logger.error("Cannot load statistics", e);
			}
			return null;
		}
	}

	@Override
	public void saveProduction(Product[] products) throws StorageException {
		if (products == null || products.length == 0) {
			return;
		}
		
		String sFile = getPathProductionSaved() + "/" + DateUtils.format(timeStampFormat, new Date()) + ".data";
		File file = new File(sFile);
		logger.debug("Saving {} {}", "Production", sFile);
		save(products, file);
		
		File renamedFile = new File(file + ".ok");
		boolean renameOk = file.renameTo(renamedFile);
		
		if (!renameOk) {
			logger.error("Fail to rename production data file: " + renamedFile);
		} else {
			if (renamedFile.length() == 0) {
				logger.debug("Renamed empty file {} {}", "Production", renamedFile);
			}
		}
	}

	public String getPathProductionSaved() {
		return this.dataFolder + "/" + FOLDER_PRODUCTION + "/" + FOLDER_PRODUCTION_SAVED;
	}

	@Override
	public void notifyDataSentToRemoteServer() {
		// move working files to a sent folder and then zip them
		moveNotifyFile(FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER, "Notify send success => move file to sent {}", true);
	}

	// move production data files to a new folder
	private void moveNotifyFile(String destination, String logMessage, boolean zip) {

		logger.debug(logMessage, productionBatchBeingSend.getName());
		String s = productionBatchBeingSend.getAbsolutePath().replace(FOLDER_PRODUCTION_PACKAGED, destination);
		File moveTo = new File(s);
		moveTo.getParentFile().mkdirs();
		this.productionBatchBeingSend.renameTo(moveTo);

		if (zip) {
			// zip file that will contains all the file that has been moved
			try {
				ZipUtils.zip(moveTo);
				moveTo.delete();
			} catch (IOException e) {
				logger.warn("Cannot zip file " + moveTo.getAbsolutePath(), e);
			}
		}
		productionBatchBeingSend = null;
	}

	@Override
	public void notifyDataErrorSendingToRemoteServer() {
		moveToQuarantine(productionBatchBeingSend, QuarantineReason.REMOTE_SERVER_BUSINESS_ERROR);
	}

	public String getPathSentToRemoteServer() {
		return this.dataFolder + "/" + FOLDER_PRODUCTION + File.separator + FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER;
	}

	@Override
	public void cleanUpOldSentProduction() {
		try {
			File folder = new File(getPathSentToRemoteServer());
			File[] files = folder.listFiles();
			if (files != null && files.length > 0) {
				for (File f : files) {
					com.sicpa.standard.client.common.utils.FileUtils.deleteFileIfOlder(f, cleanUpSendDataThreshold_day);
				}
			}
		} catch (Exception e) {
			logger.error("Cannot do clean up", e);
			EventBusService.post(new MessageEvent(this, MessageEventKey.Storage.ERROR_CLEANUP_FAIL, e));
		}
	}

	public final Object loadInternal(String file) throws StorageException {
		File f = new File(internalFolder + "/" + file);
		return load(f);
	}

	public final Object loadData(String file) throws StorageException {
		File f = new File(dataFolder + "/" + file);
		return load(f);
	}

	@Override
	public final Object load(String file) throws StorageException {
		return load(new File(file));
	}

	public final Object load(File file) throws StorageException {
		try {
			if (file != null && file.length() > 0) {
				return storageBehavior.load(file);
			} else {
				logger.info("Loading empty data file: " + file);
				return null;
			}
		} catch (StorageException e) {
			if (!(e.getCause() instanceof FileNotFoundException)) {
				EventBusService.post(new MessageEvent(this, MessageEventKey.Storage.ERROR_CANNOT_LOAD, file));
			}
			throw e;
		}
	}

	public final void saveInternal(Serializable o, String file) throws StorageException {
		File f = new File(internalFolder + "/" + file);
		save(o, f);
	}

	public final void saveData(Serializable o, String file) throws StorageException {
		File f = new File(dataFolder + "/" + file);
		save(o, f);
	}

	@Override
	public final void save(Serializable o, String file) throws StorageException {
		save(o, new File(file));
	}

	public final void save(Serializable o, File file) throws StorageException {
		try {
			storageBehavior.save(o, file);
		} catch (StorageException e) {
			EventBusService.post(new MessageEvent(this, MessageEventKey.Storage.ERROR_CANNOT_SAVE, file));
			throw e;
		}
	}

	@Override
	public int getAvailableNumberOfEncoders(CodeType codeType, int year) {
		logger.debug("Get available number of encoders: code type = {}, year = {}", codeType, year);

		int res = 0;
		res += getAvailableNumberOfEncodersFromFolder(FOLDER_ENCODER_PENDING, codeType, year);
		res += getAvailableNumberOfEncodersFromFolder(FOLDER_ENCODER_BUFFER, codeType, year);

		String currentEncoderPath = internalFolder + "/" + getCurrentEncoderFile(codeType.getId());
		File currentEncoderFile = new File(currentEncoderPath);
		if (currentEncoderFile.exists()) {
			res++;
		}
		return res;
	}

	private int getAvailableNumberOfEncodersFromFolder(String folder, CodeType codeType, int year) {
		File f = new File(internalFolder + "/" + folder);
		FilenameFilter filter = getFileFilterForEncoder(codeType, year);
		File[] fEncoders = f.listFiles(filter);
		int res = 0;
		if (fEncoders != null) {
			res += fEncoders.length;
		}
		return res;
	}

	@Override
	public void packageProduction(int batchSize) {
		try {
			logger.debug("Package production file, batch size = {}", batchSize);
			Set<File> files = getProductionFilesSortedByDate();
			saveAllProductionFileToABatch(files, batchSize);
		} catch (Exception e) {
			logger.error("Failed to package a production", e);
			EventBusService.post(new MessageEvent(this, MessageEventKey.Storage.ERROR_PACKAGE_FAIL, e));
		}
	}

	public Set<File> getProductionFilesSortedByDate() {
		File folder = new File(getPathProductionSaved());
		File[] files = folder.listFiles();
		if (files != null && files.length > 0) {
			// sorted set to get the files sorted by the timestamp
			Comparator<File> fileComparatorByDate = (File o1, File o2) -> Long.valueOf(o1.lastModified()).compareTo(
					o2.lastModified());
			SortedSet<File> sortedFiles = new TreeSet<>(fileComparatorByDate);
			for (File f : files) {
				if (f.getName().endsWith(".ok")) {
					sortedFiles.add(f);
				}
			}
			return sortedFiles;
		}
		return Collections.emptySet();
	}

	private void saveAllProductionFileToABatch(Set<File> sortedFiles, int batchSize) throws StorageException {
		ArrayList<File> packagedFiles = new ArrayList<>();
		ArrayList<Product> products = new ArrayList<>();
		for (File aFile : sortedFiles) {
			packagedFiles.add(aFile);
			products.addAll(loadProduct(aFile));
			if (products.size() >= batchSize) {
				// serialized a batch and delete production files
				savePackages(products, packagedFiles);
				products.clear();
			}
		}
		if (!products.isEmpty()) {
			savePackages(products, packagedFiles);
			products.clear();
		}
	}

	private Collection<Product> loadProduct(File aFile) {
		ArrayList<Product> products = new ArrayList<>();
		try {
			Product[] productArray = (Product[]) load(aFile);
			
			if (productArray != null) {
				for (Product p : productArray) {
					if (p != null) {
						products.add(p);
					}
				}
			}
		} catch (StorageException e) {
			logger.error("", e);
			moveToQuarantine(aFile, QuarantineReason.LOAD_ERROR);
		}
		return products;
	}

	public void setProductsPackager(final IProductsPackager productsPackager) {
		this.productsPackager = productsPackager;
	}

	public String getPathProductionPackaged() {
		return this.dataFolder + "/" + FOLDER_PRODUCTION + "/" + FOLDER_PRODUCTION_PACKAGED;
	}

	// serialized a batch and delete production files
	protected void savePackages(List<Product> products, List<File> packagedFiles) throws StorageException {
		List<PackagedProducts> packagedProducts = productsPackager.getPackagedProducts(products);
		List<File> done = new ArrayList<>();

		for (PackagedProducts pack : packagedProducts) {
			done.add(savePackage(pack));
		}
		acknowledgeAllPackageSaved(done);
		packageFiles(packagedFiles);
	}

	private void packageFiles(List<File> packagedFiles) {
		// remove all the production files that have been used during this packaging
		for (File f : packagedFiles) {
			f.delete();
		}
	}

	private void acknowledgeAllPackageSaved(List<File> files) {
		for (File packageFile : files) {
			packageFile.renameTo(new File(packageFile + ".ok"));
		}
	}

	private File savePackage(PackagedProducts pack) throws StorageException {
		File packageFile = new File(getPathProductionPackaged() + "/" + pack.getFileName() + ".data");
		pack.getProducts().forEach(p -> prepareProductForSerialisation(p));
		save(pack, packageFile);
		return packageFile;
	}

	private void prepareProductForSerialisation(Product p) {
		// the status/batchid/subsystem is already is in packagedProducts
		// so in the product they can be set to null
		// for better readability in the serialized file
		p.setStatus(null);
		p.setProductionBatchId(null);
		p.setSubsystem(null);
		p.setPrinted(null);
	}

	@Override
	public int getBatchOfProductsCount() {

		File folder = new File(getPathProductionPackaged());

		File[] files = folder.listFiles();
		if (files == null) {
			return 0;
		}
		return files.length;
	}

	@Override
	public PackagedProducts getABatchOfProducts() {

		File folder = new File(getPathProductionPackaged());

		File[] files = folder.listFiles();
		if (files == null) {
			return null;
		}

		// listFiles doesn't have to return the file in any specific order
		// sort them so we get the oldest first
		Arrays.sort(files);

		if (files.length > 0) {
			int cpt = 0;
			boolean tryToPackage = true;
			while (tryToPackage) {
				if (files[cpt].getName().endsWith(".ok")) {
					this.productionBatchBeingSend = files[cpt];
					try {
						PackagedProducts packagedProducts = (PackagedProducts) load(productionBatchBeingSend);
						
						if (packagedProducts != null) {
							return packagedProducts;
						} else {
							logger.warn("Empty package file {}", productionBatchBeingSend);
							moveToQuarantine(productionBatchBeingSend, QuarantineReason.LOAD_ERROR);
						}
					} catch (StorageException e) {
						logger.warn("Cannot load {}", e);
						moveToQuarantine(productionBatchBeingSend, QuarantineReason.LOAD_ERROR);
					}
				}
				cpt++;
				if (cpt >= files.length) {
					tryToPackage = false;
				}
			}

		}
		return null;
	}

	@Override
	public String getStorageInfo() {

		String res;
		res = getFolderInfo(new File(getPathProductionSaved())) + " - ";
		res += getFolderInfo(new File(getPathProductionPackaged())) + " - ";
		res += getFolderInfo(new File(getPathSentToRemoteServer()));

		return res;
	}

	private String getFolderInfo(final File folder) {
		if (folder.exists()) {
			long totalSize = FileUtils.sizeOfDirectory(folder);
			String displaySize = FileUtils.byteCountToDisplaySize(totalSize);

			long min = Long.MAX_VALUE;
			long max = -1;

			File[] files = folder.listFiles();
			for (File f : files) {
				min = Math.min(min, f.lastModified());
				max = Math.max(max, f.lastModified());
			}
			String res = folder.getName() + ":" + files.length + " files " + displaySize;

			return res;
		} else {
			return "";
		}
	}

	public void moveToQuarantine(File source, QuarantineReason reason) {
		File destination = new File(getFileForQuarantine(source.getName(), reason));
		logger.info("move {} to error read folder", source);

		destination.getParentFile().mkdirs();
		if (!source.renameTo(destination)) {
			logger.error("error moving {} to quarantine folder", source);
		}
	}

	public String getFileForQuarantine(String fileName, QuarantineReason reason) {
		return quarantineFolder + "/" + reason.getSubFolder() + "/" + DateUtils.format(timeStampFormat, new Date())
				+ "--" + reason.getFilePrefix() + "--" + fileName;
	}

	@Override
	public void saveToQuarantine(Serializable object, String id, QuarantineReason reason) {
		String file = getFileForQuarantine(id, reason);
		try {
			save(object, file);
		} catch (StorageException e) {
			logger.error("failed to saved to quarantine " + file, e);
		}
	}

	@Override
	public void remove(String file) throws StorageException {
		File f = new File(dataFolder + "/" + file);
		storageBehavior.remove(f);
	}

	public String getDataFolder() {
		return dataFolder;
	}

	public String getInternalFolder() {
		return internalFolder;
	}

	@Override
	public List<EncoderInfo> getAllEndodersInfo() {
		List<EncoderInfo> infos = new ArrayList<EncoderInfo>();
		infos.addAll(generatePendingEncoderInfo());
		synchronized (encoderLock) {
			infos.addAll(generateBufferEncoderInfo());
			infos.addAll(generateCurrentEncoderInfo());
		}
		infos.addAll(generateFinishedEncoderInfo());

		return infos;
	}

	private List<EncoderInfo> generateCurrentEncoderInfo() {
		return generateEncoderInfo(FOLDER_ENCODER_CURRENT);
	}

	private List<EncoderInfo> generatePendingEncoderInfo() {
		return generateEncoderInfo(FOLDER_ENCODER_PENDING);
	}

	private List<EncoderInfo> generateBufferEncoderInfo() {
		return generateEncoderInfo(FOLDER_ENCODER_BUFFER);
	}

	private List<EncoderInfo> generateFinishedEncoderInfo() {
		return generateEncoderInfo(FOLDER_ENCODER_FINISHED_PENDING);
	}

	private List<EncoderInfo> generateEncoderInfo(String folder) {
		List<EncoderInfo> infos = new ArrayList<EncoderInfo>();
		File[] fEncoders = new File(internalFolder + "/" + folder).listFiles();
		try {
			if (fEncoders != null) {
				for (File f : fEncoders) {
					Object o = loadInternal(folder + "/" + f.getName());
					
					if (o != null) {
						boolean finished = o instanceof FinishedEncoder;
						if (finished) {
							o = ((FinishedEncoder) o).getEncoder();
						}
						IEncoder encoder = (IEncoder) o;
						EncoderInfo encoderInfo = new EncoderInfo(encoder, finished);
						encoderInfo.setSequence(getSequenceOfEncoder(encoder));
						encoderInfo.setFile(f);
						infos.add(encoderInfo);
					}
				}
			}
		} catch (Exception e) {
			logger.error("error loading encoder for info", e);
		}
		return infos;
	}

	public void setFileSequenceProvider(FileSequenceStorageProvider fileSequenceProvider) {
		this.fileSequenceProvider = fileSequenceProvider;
	}

	@Override
	public List<IEncoder> getPendingEncoders() {

		List<IEncoder> res = new ArrayList<IEncoder>();
		for (Entry<IEncoder, File> entry : getPendingEncodersWithFile()) {
			res.add(entry.getKey());
		}
		return res;
	}

	public List<Entry<IEncoder, File>> getPendingEncodersWithFile() {
		return getEncodersWithFile(FOLDER_ENCODER_PENDING);
	}

	@Override
	public void quarantineEncoder(long id) {
		synchronized (encoderLock) {
			List<Entry<IEncoder, File>> list = new ArrayList<Entry<IEncoder, File>>();
			list.addAll(getEncodersWithFile(FOLDER_ENCODER_BUFFER));
			list.addAll(getEncodersWithFile(FOLDER_ENCODER_CURRENT));
			for (Entry<IEncoder, File> entry : list) {
				if (entry.getKey().getId() == id) {
					moveToQuarantine(entry.getValue(), QuarantineReason.REMOTE_SERVER_BUSINESS_ERROR);
				}
			}
		}
	}

	public List<Entry<IEncoder, File>> getEncodersWithFile(String folder) {
		List<Entry<IEncoder, File>> res = new ArrayList<Entry<IEncoder, File>>();
		File internalfolder = new File(internalFolder + "/" + folder);

		File[] pendingEncodersFile = internalfolder.listFiles();
		if (pendingEncodersFile != null) {
			for (File encoderFile : pendingEncodersFile) {
				try {
					IEncoder encoder = (IEncoder) load(encoderFile);
					
					if (encoder != null) {
						res.add(new SimpleImmutableEntry<IEncoder, File>(encoder, encoderFile));
					}
				} catch (Exception e) {
					logger.error("error loading encoder:" + encoderFile, e);
					moveToQuarantine(encoderFile, QuarantineReason.LOAD_ERROR);
				}
			}
		}
		return res;
	}

	@Override
	public void notifyEncodersInfoSent(List<EncoderInfo> encoderInfos) {
		for (EncoderInfo info : encoderInfos) {
			if (info.isFinished()) {
				moveFinishedPendingToConfirmed(info);
			}
		}
	}

	private void moveFinishedPendingToConfirmed(EncoderInfo encoderInfo) {
		logger.error("confirming finished encoder" + encoderInfo.getFile().getName());
		File destination = new File(internalFolder + "/" + FOLDER_ENCODER_FINISHED_CONFIRMED + "/"
				+ encoderInfo.getFile().getName());
		destination.getParentFile().mkdirs();
		if (!encoderInfo.getFile().renameTo(destination)) {
			// not critical, so only log it
			logger.error("error renaming {} to {}", encoderInfo.getFile(), destination);
		}
	}

	@Override
	public void confirmEncoder(long id) {
		for (Entry<IEncoder, File> entry : getPendingEncodersWithFile()) {
			if (id == entry.getKey().getId()) {
				File file = entry.getValue();
				File newFile = new File(internalFolder + "/" + FOLDER_ENCODER_BUFFER + "/" + file.getName());
				newFile.getParentFile().mkdirs();
				logger.info("confirming encoder - renaming {} to {}", file, newFile);
				if (!file.renameTo(newFile)) {
					logger.error("fail to move pending encoder to buffer folder:" + newFile);
				}

			}
		}
	}

	@Override
	public void removePendingEncoder(long id) {
		for (Entry<IEncoder, File> entry : getPendingEncodersWithFile()) {
			if (id == entry.getKey().getId()) {
				File file = entry.getValue();
				logger.info("removing pending encoder id={} , file={}", id, file);
				if (!file.delete()) {
					logger.error("fail to delete pending encoder" + file);
				}
			}
		}
	}

	public void setCleanUpSendDataThreshold_day(int cleanUpSendDataThreshold_day) {
		this.cleanUpSendDataThreshold_day = cleanUpSendDataThreshold_day;
	}
}
