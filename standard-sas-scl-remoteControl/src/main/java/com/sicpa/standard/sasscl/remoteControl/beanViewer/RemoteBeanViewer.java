package com.sicpa.standard.sasscl.remoteControl.beanViewer;

import com.sicpa.standard.printer.driver.DominoParameter;
import com.sicpa.standard.sasscl.view.monitoring.mbean.BeanViewer;

public class RemoteBeanViewer extends BeanViewer {

	private static final long serialVersionUID = 1L;

	public RemoteBeanViewer() {
		super();
		addPropertyToIgnore("notificationInfo");
		addPropertyToIgnore("loggersList");
		addClassToDetailList(DominoParameter.class.getName());
	}
}
