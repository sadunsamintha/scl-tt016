package com.sicpa.standard.sasscl.remoteControl.beanViewer;

import java.awt.Component;

import com.sicpa.standard.client.common.view.IGUIComponentGetter;

public class RemoteBeanViewerGetter implements IGUIComponentGetter {

	protected RemoteBeanViewer panel;
	protected Object bean;

	@Override
	public Component getComponent() {
		if (this.panel == null) {
			this.panel = new RemoteBeanViewer();
			this.panel.setBean(this.bean);
		}
		return this.panel.getMainPanel();
	}

	public void setBean(final Object bean) {
		this.bean = bean;
	}

}
