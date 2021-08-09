package com.sicpa.tt016.view.resetBeam;

import com.google.common.eventbus.Subscribe;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

@SuppressWarnings("serial")
public class ResetBeamView extends
		AbstractView<IResetBeamViewListener, ResetBeamViewModel> {

	private JButton resetButton;

	public ResetBeamView() {
		setOpaque(false);
		initGUI();
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill, inset 0 0 0 0"));
		add(new PaddedButton(getResetButton()), "h 80! , w 60");
	}

	public JButton getResetButton() {
		if (resetButton == null) {
			resetButton = new JButton(Messages.get("mainframe.button.reset.beam"));
			resetButton.addActionListener(e -> resetBeamToSkuHeight());
		}
		return resetButton;
	}

	private void resetBeamToSkuHeight() {
		for (IResetBeamViewListener l : listeners) {
			l.reset();
		}
	}

	@Override
	public void modelChanged() {
		getResetButton().setEnabled(model.isEnable());
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		getResetButton().setText(Messages.get("mainframe.button.reset.beam"));
	}
}
