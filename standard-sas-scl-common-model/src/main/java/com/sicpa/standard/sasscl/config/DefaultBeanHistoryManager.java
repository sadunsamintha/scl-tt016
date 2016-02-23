
package com.sicpa.standard.sasscl.config;

import com.sicpa.standard.client.common.config.history.BeanHistoryManager;

public class DefaultBeanHistoryManager extends BeanHistoryManager {

	public DefaultBeanHistoryManager(String folder,String profilePath) {
		super(folder, "beanChangedLog.txt");
		addManagedFile(profilePath+"/config/printer/printer_pr_scl_1.xml");
		addManagedFile(profilePath+"/config/printer/printer_pr_scl_2.xml");

		addManagedFile(profilePath+"/config/camera/cameraJobConfig.xml");
		addManagedFile(profilePath+"/config/camera/camera_qc_sas.xml");
		addManagedFile(profilePath+"/config/camera/camera_qc_scl_1.xml");
		addManagedFile(profilePath+"/config/camera/camera_qc_scl_2.xml");
		

		addManagedFile(profilePath+"/config/plc/cabinetConfig.xml");
		addManagedFile(profilePath+"/config/plc/line1Config.xml");
		addManagedFile(profilePath+"/config/plc/line2Config.xml");
		addManagedFile(profilePath+"/config/plc/line3Config.xml");

		addManagedFile(profilePath+"/config/server/remoteServer.xml");
		addManagedFile(profilePath+"/config/security.xml");
	}

}
