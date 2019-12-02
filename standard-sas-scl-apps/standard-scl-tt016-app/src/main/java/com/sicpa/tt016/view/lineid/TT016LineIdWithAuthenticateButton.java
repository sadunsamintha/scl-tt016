package com.sicpa.tt016.view.lineid;

import java.awt.Color;

import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.view.lineid.LineIdWithAuthenticateButton;

public class TT016LineIdWithAuthenticateButton extends LineIdWithAuthenticateButton {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(TT016LineIdWithAuthenticateButton.class);

	@Override
	public JLabel getLabelUserInfo() {
		if (labelUserInfo == null) {
			labelUserInfo = new JLabel("  ");
			labelUserInfo.setFont(labelUserInfo.getFont().deriveFont(10f));
			labelUserInfo.setForeground(Color.WHITE);
		}
		return labelUserInfo;
	}

	@Override
	public JLabel getLabelLogAs() {
		if (labelLogAs == null) {
			labelLogAs = new JLabel(Messages.get("security.logas"));
			labelLogAs.setName("security.logas");
			labelLogAs.setFont(labelLogAs.getFont().deriveFont(10f));
			labelLogAs.setForeground(Color.WHITE);
		}
		return labelLogAs;
	}
}
