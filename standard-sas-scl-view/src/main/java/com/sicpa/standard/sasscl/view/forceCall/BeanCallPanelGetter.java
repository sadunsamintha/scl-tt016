package com.sicpa.standard.sasscl.view.forceCall;

import java.awt.Component;
import java.util.List;

import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class BeanCallPanelGetter extends SecuredComponentGetter {

	public BeanCallPanelGetter() {
		super(SasSclPermission.BEAN_CALL, "label.beanCall.title");
	}

	protected BeanCallPanel panel;
	protected List<IBeanCall> beancalls;

	@Override
	public Component getComponent() {
		if (panel == null) {
			panel = new BeanCallPanel(beancalls);
		}
		return panel;
	}

	public void setBeancalls(List<IBeanCall> beancalls) {
		this.beancalls = beancalls;
	}

	public List<IBeanCall> getBeancalls() {
		return beancalls;
	}
}
