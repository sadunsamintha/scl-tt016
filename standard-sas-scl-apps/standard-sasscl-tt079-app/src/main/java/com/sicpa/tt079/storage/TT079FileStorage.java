package com.sicpa.tt079.storage;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.storage.ISimpleFileStorage;
import com.sicpa.standard.client.common.storage.StorageException;
import com.sicpa.standard.client.common.utils.DateUtils;
import com.sicpa.standard.sasscl.common.exception.ProductionRuntimeException;
import com.sicpa.standard.sasscl.common.storage.FileStorage;
import com.sicpa.standard.sasscl.common.storage.QuarantineReason;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;

/**
 * storage implementation using files
 * 
 * @author DIelsch
 * 
 */
public class TT079FileStorage extends FileStorage {

	private final static Logger logger = LoggerFactory.getLogger(TT079FileStorage.class);

	private final String dataFolder;
	private final String internalFolder;
	private final String quarantineFolder;
	public static final String FILE_DECODER = "decoder.data";
	public static final String FILE_PRODUCTION_PARAMETERS = "productionParameters.data";
	public static final String FILE_SELECTED_PRODUCTION_PARAMS = "selectedProductionParam.data";
	public static final String FOLDER_PRODUCTION_SAVED = "buffer";
	public static final String FOLDER_PRODUCTION_PACKAGED = "packaged";
	public static final String FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER = "sent";
	public static final String FOLDER_PRODUCTION = "products";
	public static final String PROFILES = "profiles";

	private boolean externalActivation;
	private String externalActivationCustoId;
	private String externalActivationProfile;
	private String custoId;

	private String timeStampFormat = "yyyy-MM-dd--HH-mm-ss-SSS";

	private ISimpleFileStorage storageBehavior;

	/**
	 * @param baseFolder
	 *            root of the storage, mainly used for unit test, for normal case use the default constructor
	 */
	public TT079FileStorage(final String baseFolder, String internalFolder, String quarantineFolder,
			ISimpleFileStorage storageBehavior) {
		super(baseFolder, internalFolder, quarantineFolder, storageBehavior);
		
		this.quarantineFolder = quarantineFolder;
		this.dataFolder = baseFolder;
		this.internalFolder = internalFolder;
		this.storageBehavior = storageBehavior;
	}

	public void setStorageBehavior(ISimpleFileStorage storageBehavior) {
		this.storageBehavior = storageBehavior;
	}
	
	@Override
	public void saveAuthenticator(IAuthenticator auth) {
		logger.debug("Saving {} {}", "decoder", FILE_DECODER);
		try {
			String newInternalFolder = useDirFromExternalApp(internalFolder);
			saveInternal(auth, newInternalFolder, FILE_DECODER);
		} catch (Exception e) {
			logger.error("Cannot save decoder", e);
		}
	}

	@Override
	public IAuthenticator getAuthenticator() {
		logger.debug("Loading {} {}", "decoder", FILE_DECODER);
		try {
			String newInternalFolder = useDirFromExternalApp(internalFolder);
			return (IAuthenticator) loadInternal(FILE_DECODER, newInternalFolder);
		} catch (Exception e) {
			logger.error("Cannot load decoder", e);
			return null;
		}
	}

	@Override
	public void saveProductionParameters(ProductionParameterRootNode node) {
		logger.debug("Saving {} {}", "Production parameters", FILE_PRODUCTION_PARAMETERS);
		try {
			String newInternalFolder = useDirFromExternalApp(internalFolder);
			saveInternal(node, newInternalFolder, FILE_PRODUCTION_PARAMETERS);
		} catch (Exception e) {
			logger.error("Cannot save production parameters", e);
		}
	}

	@Override
	public ProductionParameterRootNode getProductionParameters() {
		logger.debug("Loading {} {}", "Production parameters", FILE_PRODUCTION_PARAMETERS);
		try {
			String newInternalFolder = useDirFromExternalApp(internalFolder);
			return (ProductionParameterRootNode) loadInternal(FILE_PRODUCTION_PARAMETERS, newInternalFolder);
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
		if (!externalActivation) {
			super.saveSelectedProductionParamters(param);
		}
	}

	@Override
	public ProductionParameters getSelectedProductionParameters() {
		if (!externalActivation) {
			return super.getSelectedProductionParameters();
		}
		return null;
	}

	@Override
	public void saveProduction(Product[] products) throws StorageException {
		if (!externalActivation) {
			super.saveProduction(products);
		}
	}

	@Override
	public String getPathProductionSaved() {
		String newDataFolder = useDirFromExternalApp(dataFolder);
		return newDataFolder + "/" + FOLDER_PRODUCTION + "/" + FOLDER_PRODUCTION_SAVED;
	}

	@Override
	public String getPathSentToRemoteServer() {
		String newDataFolder = useDirFromExternalApp(dataFolder);
		return newDataFolder + "/" + FOLDER_PRODUCTION + File.separator + FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER;
	}

	public final Object loadInternal(String file, String folder) throws StorageException {
		File f = new File(folder + "/" + file);
		return load(f);
	}

	public final Object loadData(String file, String folder) throws StorageException {
		File f = new File(folder + "/" + file);
		return load(f);
	}

	public final void saveInternal(Serializable o, String folder, String file) throws StorageException {
		File f = new File(folder + "/" + file);
		save(o, f);
	}

	public final void saveData(Serializable o, String folder, String file) throws StorageException {
		File f = new File(folder + "/" + file);
		save(o, f);
	}

	@Override
	public String getPathProductionPackaged() {
		String newDataFolder = useDirFromExternalApp(dataFolder);
		return newDataFolder + "/" + FOLDER_PRODUCTION + "/" + FOLDER_PRODUCTION_PACKAGED;
	}

	@Override
	public String getFileForQuarantine(String fileName, QuarantineReason reason) {
		String newQuarantineFolder = useDirFromExternalApp(quarantineFolder);
		return newQuarantineFolder + "/" + reason.getSubFolder() + "/" + DateUtils.format(timeStampFormat, new Date())
				+ "--" + reason.getFilePrefix() + "--" + fileName;
	}

	@Override
	public void remove(String file) throws StorageException {
		String newDataFolder = useDirFromExternalApp(dataFolder);
		File f = new File(newDataFolder + "/" + file);
		storageBehavior.remove(f);
	}

	public String getDataFolder() {
		return useDirFromExternalApp(dataFolder);
	}

	public String getInternalFolder() {
		return useDirFromExternalApp(internalFolder);
	}

	private String useDirFromExternalApp(String localDir) {
		File externalDir;
		
		String[] dirList = localDir.split("/");
		String appDir = dirList[dirList.length - 4];
		String localSubDir = dirList[dirList.length - 1];
		int fileSeparatorIdx = localDir.lastIndexOf(appDir);
		String externalAppDir = localDir.substring(0, fileSeparatorIdx - 1);
		
		// filter needed to get path of external country application instance defined in global.properties (external.activation.custo.ide)
		File[] appList = filterFiles(externalAppDir, externalActivationCustoId, false);
		
		if (appList != null && appList.length > 0) {
			File externalApp = appList[0];
			
			// filter needed to get if having profiles folder (based from Core Solution)
			File[] appContentList = filterFiles(externalApp.getAbsolutePath(), PROFILES, true);
			
			if (appContentList != null && appContentList.length > 0) {
				String extProfilesDir = externalApp.getAbsolutePath() + "/" + PROFILES;
				
				// filter needed to get if having specific profile folder (based from Core Solution) defined in global.properties (external.activation.profile)
				File[] appProfileContentList = filterFiles(extProfilesDir, externalActivationProfile, true);
				
				if (appProfileContentList != null && appProfileContentList.length > 0) {
					externalDir = new File(extProfilesDir + "/" + externalActivationProfile + "/" + localSubDir + "/" + custoId);
					externalDir.mkdirs();
					
					return externalDir.getAbsolutePath();
				} else {
					throw new ProductionRuntimeException(MessageFormat.format("Fail to get profile: {0} from application with Custo ID: {1}", externalActivationProfile, externalActivationCustoId));
				}
			} else {
				externalDir = new File(externalApp.getAbsolutePath() + "/" + localSubDir + "/" + custoId);
				externalDir.mkdirs();
				
				return externalDir.getAbsolutePath();
			}
		} else {
			throw new ProductionRuntimeException(MessageFormat.format("Fail to get application with Custo ID: {0}", externalActivationCustoId));
		}
	}
	
	private File[] filterFiles(String dirPath, String filterName, boolean isEqual) {
		File[] contentList = new File(dirPath).listFiles(new FileFilter() {
	      @Override
	      public boolean accept(File pathname) {
	    	if (isEqual) {
	    		return pathname.getName().equals(filterName);
	    	} else {
	    		return pathname.getName().toUpperCase().contains(filterName.toUpperCase());
	    	}
	      }
	    });
		
		return contentList;
	}

	public void setExternalActivation(boolean externalActivation) {
		this.externalActivation = externalActivation;
	}

	public void setExternalActivationCustoId(String externalActivationCustoId) {
		this.externalActivationCustoId = externalActivationCustoId;
	}

	public void setExternalActivationProfile(String externalActivationProfile) {
		this.externalActivationProfile = externalActivationProfile;
	}

	public void setCustoId(String custoId) {
		this.custoId = custoId;
	}
}
