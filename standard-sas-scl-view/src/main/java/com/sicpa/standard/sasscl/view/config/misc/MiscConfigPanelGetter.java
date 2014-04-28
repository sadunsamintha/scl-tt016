package com.sicpa.standard.sasscl.view.config.misc;

import javax.swing.JPanel;

import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class MiscConfigPanelGetter extends SecuredComponentGetter {

	protected MiscConfigPanelGetter() {
		super(SasSclPermission.EDIT_MISC_PROPERTY, "label.misc.config");
	}

	protected MiscConfigPanel panel;

	@Override
	public JPanel getComponent() {
		if (this.panel == null) {
			this.panel = new MiscConfigPanel();
		}
		return this.panel;
	}

}
