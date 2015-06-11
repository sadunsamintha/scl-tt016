package com.sicpa.standard.sasscl.config;

import com.sicpa.standard.client.common.config.history.BeanHistoryManager;

public class DefaultBeanHistoryManager extends BeanHistoryManager {

	public DefaultBeanHistoryManager() {
		super("configHistory", "beanChangedLog.txt");
		addManagedFile("config/printer/printer_pr_scl_1.xml");
		addManagedFile("config/printer/printer_pr_scl_2.xml");

		addManagedFile("config/camera/cameraJobConfig.xml");
		addManagedFile("config/camera/camera_qc_sas.xml");
		addManagedFile("config/camera/camera_qc_scl_1.xml");
		addManagedFile("config/camera/camera_qc_scl_2.xml");
		

		addManagedFile("config/plc/cabinetConfig.xml");
		addManagedFile("config/plc/line1Config.xml");
		addManagedFile("config/plc/line2Config.xml");
		addManagedFile("config/plc/line3Config.xml");

		addManagedFile("config/server/remoteServer.xml");
		addManagedFile("config/scheduler.xml");
		addManagedFile("config/security.xml");
		addManagedFile("config/barcode.xml");
	}

}
