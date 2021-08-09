package com.sicpa.tt016.storage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.storage.ISimpleFileStorage;
import com.sicpa.standard.client.common.storage.StorageException;
import com.sicpa.standard.client.common.utils.DateUtils;
import com.sicpa.standard.client.common.utils.ZipUtils;
import com.sicpa.standard.sasscl.common.storage.FileStorage;
import com.sicpa.standard.sasscl.common.storage.QuarantineReason;
import com.sicpa.standard.sasscl.common.storage.productPackager.IProductsPackager;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sicpadata.spi.sequencestorage.FileSequenceStorageProvider;
import com.sicpa.tt016.common.dto.SkuGrossNetProductCounterDTO;

/**
 * storage implementation using files
 * 
 * @author GGubatan
 * 
 */
public class TT016FileStorage extends FileStorage implements ITT016Storage {

	private final static Logger logger = LoggerFactory.getLogger(TT016FileStorage.class);

	public static final String GLOBAL_PROPERTIES_PATH = "profiles/TT016/config/global.properties";

	private final String dataFolder;
	private final String internalFolder;
	private final String quarantineFolder;
	public static final String FOLDER_PRODUCTION_SAVED = "buffer";
	public static final String FOLDER_PRODUCTION_PACKAGED = "packaged";
	public static final String FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER = "sent";
	public static final String FOLDER_PRODUCTION = "products";
	public static final String FOLDER_SKU_GROSS_NET = "sku_gross_net";
	public static final String PROFILES = "profiles";
	
	private File skuGrossNetBatchBeingSent;
	private int cleanUpSendDataThreshold_day;

	private String timeStampFormat = "yyyy-MM-dd--HH-mm-ss-SSS";

	private IProductsPackager productsPackager;
	private ISimpleFileStorage storageBehavior;
	private FileSequenceStorageProvider fileSequenceProvider;
	
	/**
	 * @param baseFolder
	 *            root of the storage, mainly used for unit test, for normal case use the default constructor
	 */
	public TT016FileStorage(final String baseFolder, String internalFolder, String quarantineFolder,
			ISimpleFileStorage storageBehavior) {
		super(baseFolder, internalFolder, quarantineFolder, storageBehavior);
		
		this.quarantineFolder = quarantineFolder;
		this.dataFolder = baseFolder;
		this.internalFolder = internalFolder;
		this.storageBehavior = storageBehavior;
	}
	
	@Override
	public void saveSkuGrossNet(SkuGrossNetProductCounterDTO[] skuGrossNetProductList) throws StorageException {
		if (skuGrossNetProductList == null || skuGrossNetProductList.length == 0) {
			return;
		}
		
		String sFile = getPathSkuGrossNetSaved() + "/" + DateUtils.format(timeStampFormat, new Date()) + ".data";
		File file = new File(sFile);
		logger.debug("Saving {} {}", "SKU Gross Net", sFile);
		save(skuGrossNetProductList, file);
		
		File renamedFile = new File(file + ".ok");
		boolean renameOk = file.renameTo(renamedFile);
		
		if (!renameOk) {
			logger.error("Fail to rename sku gross net file: " + renamedFile);
		} else {
			if (renamedFile.length() == 0) {
				logger.debug("Renamed empty file {} {}", "SKU Gross Net", renamedFile);
			}
		}
	}
	
	@Override
	public SkuGrossNetProductCounterDTO[] getABatchOfSkuGrossNet() {
		File folder = new File(getPathSkuGrossNetSaved());

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
					this.skuGrossNetBatchBeingSent = files[cpt];
					try {
						SkuGrossNetProductCounterDTO[] skuGrossNetBatch = (SkuGrossNetProductCounterDTO[]) load(skuGrossNetBatchBeingSent);
						
						if (skuGrossNetBatch != null) {
							return skuGrossNetBatch;
						} else {
							logger.warn("Empty sku gross net file {}", skuGrossNetBatchBeingSent);
							moveToQuarantine(skuGrossNetBatchBeingSent, QuarantineReason.LOAD_ERROR);
						}
					} catch (StorageException e) {
						logger.warn("Cannot load {}", e);
						moveToQuarantine(skuGrossNetBatchBeingSent, QuarantineReason.LOAD_ERROR);
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
	public int getSkuGrossNetBatchCount() {
		File folder = new File(getPathSkuGrossNetSaved());

		File[] files = folder.listFiles();
		if (files == null) {
			return 0;
		}
		return files.length;
	}
	
	@Override
	public void notifySkuGrossNetDataSentToRemoteServer() {
		// move working files to a sent folder and then zip them
		moveNotifyFile(FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER, "Notify send success => move sku gross net file to sent {}", true);
	}

	// move sku gross net data files to a new folder
	private void moveNotifyFile(String destination, String logMessage, boolean zip) {
		logger.debug(logMessage, skuGrossNetBatchBeingSent.getName());
		String s = skuGrossNetBatchBeingSent.getAbsolutePath().replace(FOLDER_PRODUCTION_SAVED, destination);
		File moveTo = new File(s);
		moveTo.getParentFile().mkdirs();
		this.skuGrossNetBatchBeingSent.renameTo(moveTo);

		if (zip) {
			// zip file that will contains all the file that has been moved
			try {
				ZipUtils.zip(moveTo);
				moveTo.delete();
			} catch (IOException e) {
				logger.warn("Cannot zip file " + moveTo.getAbsolutePath(), e);
			}
		}
		skuGrossNetBatchBeingSent = null;
	}
	
	@Override
	public void notifySkuGrossNetDataErrorSendingToRemoteServer() {
		moveToQuarantine(skuGrossNetBatchBeingSent, QuarantineReason.REMOTE_SERVER_BUSINESS_ERROR);
	}
	
	@Override
	public void cleanUpOldSentProduction() {
		try {
			File folderProdData = new File(getPathSentToRemoteServer());
			File[] prodDataFiles = folderProdData.listFiles();
			
			File folderSkuGrossNetData = new File(getPathSkuGrossNetSentToRemoteServer());
			File[] skuGrossNetDataFiles = folderSkuGrossNetData.listFiles();
			
			File[] files = ArrayUtils.addAll(prodDataFiles, skuGrossNetDataFiles);
			
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
	
	@Override
	protected long getSequenceOfEncoder(IEncoder encoder) {
		super.setFileSequenceProvider(fileSequenceProvider);
		return super.getSequenceOfEncoder(encoder);
	}
	
	@Override
	protected void savePackages(List<Product> products, List<File> packagedFiles) throws StorageException {
		super.setProductsPackager(productsPackager);
		super.savePackages(products, packagedFiles);
	}
	
	public String getPathSkuGrossNetSaved() {
		return this.dataFolder + "/" + FOLDER_SKU_GROSS_NET + "/" + FOLDER_PRODUCTION_SAVED;
	}
	
	public String getPathSkuGrossNetSentToRemoteServer() {
		return this.dataFolder + "/" + FOLDER_SKU_GROSS_NET + "/" + FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER;
	}
	
	public void setProductsPackager(final IProductsPackager productsPackager) {
		this.productsPackager = productsPackager;
	}
	
	public void setStorageBehavior(ISimpleFileStorage storageBehavior) {
		this.storageBehavior = storageBehavior;
	}
	
	public void setFileSequenceProvider(FileSequenceStorageProvider fileSequenceProvider) {
		this.fileSequenceProvider = fileSequenceProvider;
	}
	
	public void setCleanUpSendDataThreshold_day(int cleanUpSendDataThreshold_day) {
		this.cleanUpSendDataThreshold_day = cleanUpSendDataThreshold_day;
	}
}
