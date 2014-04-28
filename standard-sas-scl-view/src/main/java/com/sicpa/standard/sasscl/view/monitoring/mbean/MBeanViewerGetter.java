package com.sicpa.standard.sasscl.view.monitoring.mbean;

import javax.swing.JPanel;

import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class MBeanViewerGetter extends SecuredComponentGetter {

	protected BeanViewer viewer;
	protected Object bean;

	protected MBeanViewerGetter() {
		super(SasSclPermission.MONITORING_MBEAN, "label.mbean");
	}

	@Override
	public JPanel getComponent() {
		if (this.viewer == null) {
			this.viewer = new BeanViewer(this.bean);
		}
		return this.viewer;
	}

	public void setBean(final Object bean) {
		this.bean = bean;
	}

}
