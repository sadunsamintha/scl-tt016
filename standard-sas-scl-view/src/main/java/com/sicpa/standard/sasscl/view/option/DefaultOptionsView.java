package com.sicpa.standard.sasscl.view.option;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

@SuppressWarnings("serial")
public class DefaultOptionsView extends AbstractOptionsView {

	protected JButton buttonOptions;

	public DefaultOptionsView() {
		initGUI();
	}

	protected void initGUI() {
		setOpaque(false);
		setLayout(new BorderLayout());
		add(new PaddedButton(getButtonOptions()));
	}

	@Override
	public void modelChanged() {
		getButtonOptions().setEnabled(model.isDisplayOptionEnabled());
	}

	public JButton getButtonOptions() {
		if (buttonOptions == null) {
			buttonOptions = new JButton(Messages.get("mainframe.buttons.options"));
			buttonOptions.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonOptionsActionPerformed();
				}
			});
		}
		return buttonOptions;
	}

	protected void buttonOptionsActionPerformed() {
		fireDisplayOptions();
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		getButtonOptions().setText(Messages.get("mainframe.buttons.options"));
	}
}
