package com.sicpa.standard.sasscl.view.config.misc;

import javax.swing.JPanel;

import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class MiscConfigPanelGetter extends SecuredComponentGetter {


	protected MiscConfigPanelGetter() {
		super(SasSclPermission.EDIT_MISC_PROPERTY, "label.misc.config");
	}

	protected MiscConfigPanel panel;

	protected String defaultLang;

	@Override
	public JPanel getComponent() {
		if (this.panel == null) {
			this.panel = new MiscConfigPanel(defaultLang);
		}
		return this.panel;
	}

	public void setPanel(MiscConfigPanel panel) {
		this.panel = panel;
	}

	public void setDefaultLang(String defaultLang) {
		this.defaultLang = defaultLang;
	}
}
