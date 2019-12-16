package com.sicpa.tt016.view.startstop;

import javax.swing.AbstractButton;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.gui.components.buttons.StartStopButton;
import com.sicpa.standard.gui.components.buttons.StartStopButton.eStartStop;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.startstop.StartStopView;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class TT016StartStopView extends StartStopView {

	@Override
	public void initGUI() {
		ThreadUtils.invokeLater(() -> {
			setLayout(new MigLayout("fill, inset 0 0 0 0"));
			add(getButtonStart(), "sg 1, w 110!,h 70!");
			add(getButtonStop(), "sg 1");
			add(productionStatusView, "w 50,h 50");
		});
	}

	@Override
	public AbstractButton getButtonStart() {
		if (buttonStart == null) {
			buttonStart = new StartStopButton(eStartStop.START);
			buttonStart.setEnabled(false);
			buttonStart.addActionListener(e -> buttonStartActionPerformed());
		}
		return buttonStart;
	}

	@Override
	public AbstractButton getButtonStop() {
		if (buttonStop == null) {
			buttonStop = new StartStopButton(eStartStop.STOP);
			buttonStop.setEnabled(false);
			buttonStop.addActionListener(e -> buttonStopActionPerformed());
		}
		return buttonStop;
	}
	
	@Subscribe
	@Override
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		
	}

}
