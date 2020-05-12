package com.sicpa.tt016.monitoring.mbean;

import java.io.File;

import com.sicpa.standard.sasscl.monitoring.mbean.scl.SclApp;
import com.sicpa.tt016.scl.TT016MainAppWithProfile;

import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_PRODUCTION;
import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_PRODUCTION_PACKAGED;
import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_PRODUCTION_SAVED;
import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER;
import static com.sicpa.tt016.model.statistics.TT016StatisticsKey.EJECTED_PRODUCER;
import static com.sicpa.tt016.model.statistics.TT016StatisticsKey.INK_DETECTED;

public class TT016SclApp extends SclApp implements TT016SclAppMBean {
	
	public static final String PROFILE_FOLDER = TT016MainAppWithProfile.profilePath + "/";
	
	public static final String PACKAGED_FOLDER = "data/" + FOLDER_PRODUCTION + "/" + FOLDER_PRODUCTION_PACKAGED;

	public static final String BUFFER_FOLDER = "data/" + FOLDER_PRODUCTION + "/" + FOLDER_PRODUCTION_SAVED;

	public static final String SENT_FOLDER = "data/" + FOLDER_PRODUCTION + "/" + FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER;

    @Override
    public int getNbInkDetectedProducts() {
        return getNbProductsByType(INK_DETECTED);
    }

    @Override
    public int getNbProducerEjectedProducts() {
        return getNbProductsByType(EJECTED_PRODUCER);
    }
    
    public String getSizeOfFolderInByte(String folderPath) {
    	
		File errors = new File(folderPath);
		if (!errors.exists() || !errors.isDirectory()) {
			return "";
		}
		File[] files = errors.listFiles();
		if (files == null || files.length == 0) {
			return "";
		}
		long total = 0;
		for (File file2 : files) {
			total += file2.length();
		}
		return "" + total;
	}
    
}
