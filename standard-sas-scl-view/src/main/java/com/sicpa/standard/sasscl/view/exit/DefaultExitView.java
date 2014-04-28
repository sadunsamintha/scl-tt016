package com.sicpa.standard.sasscl.view.exit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

@SuppressWarnings("serial")
public class DefaultExitView extends AbstractExittView {

	protected JButton buttonExit;

	public DefaultExitView() {
		setOpaque(false);
		initGUI();
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill, inset 0 0 0 0"));
		add(new PaddedButton(getButtonExit()), "h 80! , w 120");
	}

	public JButton getButtonExit() {
		if (buttonExit == null) {
			buttonExit = new JButton(Messages.get("mainframe.button.exit"));
			buttonExit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonExitActionPerformed();
				}
			});
		}
		return buttonExit;
	}

	protected void buttonExitActionPerformed() {
		fireExit();
	}

	@Override
	public void modelChanged() {
		getButtonExit().setEnabled(model.isExitButtonEnabled());
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		getButtonExit().setText(Messages.get("mainframe.button.exit"));
	}
}
