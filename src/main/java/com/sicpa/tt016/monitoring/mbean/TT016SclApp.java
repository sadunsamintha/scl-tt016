package com.sicpa.tt016.monitoring.mbean;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;

import com.sicpa.standard.client.common.utils.DateUtils;
import com.sicpa.standard.sasscl.monitoring.mbean.scl.SclApp;
import com.sicpa.tt016.scl.TT016MainAppWithProfile;

import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_PRODUCTION;
import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_PRODUCTION_PACKAGED;
import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_PRODUCTION_SAVED;
import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER;
import static com.sicpa.standard.sasscl.monitoring.mbean.StandardMonitoringMBeanConstants.ERROR_BUSINESS_FOLDER;
import static com.sicpa.standard.sasscl.monitoring.mbean.StandardMonitoringMBeanConstants.ERROR_LOAD_FOLDER;
import static com.sicpa.tt016.model.statistics.TT016StatisticsKey.EJECTED_PRODUCER;
import static com.sicpa.tt016.model.statistics.TT016StatisticsKey.INK_DETECTED;

public class TT016SclApp extends SclApp implements TT016SclAppMBean {
	
	public static final String PROFILE_FOLDER = TT016MainAppWithProfile.profilePath + "/";
	
	public static final String PACKAGED_FOLDER = "data/" + FOLDER_PRODUCTION + "/" + FOLDER_PRODUCTION_PACKAGED;

	public static final String BUFFER_FOLDER = "data/" + FOLDER_PRODUCTION + "/" + FOLDER_PRODUCTION_SAVED;

	public static final String SENT_FOLDER = "data/" + FOLDER_PRODUCTION + "/" + FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER;
	
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static final String KB = " KB";
	
    @Override
    public int getNbInkDetectedProducts() {
        return getNbProductsByType(INK_DETECTED);
    }

    @Override
    public int getNbProducerEjectedProducts() {
        return getNbProductsByType(EJECTED_PRODUCER);
    }
    
    public String getSizeOfFolderInByte(String folderPath) {
    	File file = new File(folderPath);
    	
		return file.exists() ? Double.toString((double)FileUtils.sizeOfDirectory(file)/1000) + KB : "";
	}
    
    @Override
    public int getNumberOfQuarantineProductionFile() {
		List<File> folderList = new ArrayList<>();
		List<String> totalFileList = new ArrayList<>();
		folderList.add(new File(PROFILE_FOLDER + ERROR_LOAD_FOLDER));
		folderList.add(new File(PROFILE_FOLDER + ERROR_BUSINESS_FOLDER));
		for (File errors : folderList) {
			if (!errors.exists() || !errors.isDirectory()) {
				continue;
			}
			if (errors.list() != null) {
				totalFileList.addAll(Arrays.asList(errors.list()));
			}
		}
		return totalFileList.size();
	}
	
    public String getOldestFileDateOfAFolder(String folderPath) {
    	File file = new File(folderPath);
		if (!file.exists() || !file.isDirectory()) {
			return "";
		}
		
		File[] files = file.listFiles();
		if (ArrayUtils.isEmpty(files)) {
			return "";
		}
		
		long lastMod = files[0].lastModified();
		for (File test : files) {
			if (test.lastModified() < lastMod) {
				lastMod = test.lastModified();
			}
		}
		return DateUtils.format(DATE_FORMAT, new Date(lastMod));
	}
    
}
