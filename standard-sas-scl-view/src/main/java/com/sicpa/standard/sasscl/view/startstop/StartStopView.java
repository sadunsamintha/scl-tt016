package com.sicpa.standard.sasscl.view.startstop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.buttons.StartStopButton;
import com.sicpa.standard.gui.components.buttons.StartStopButton.eStartStop;
import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

@SuppressWarnings("serial")
public class StartStopView extends AbstractStartStopView {

	private AbstractButton buttonStart;
	private AbstractButton buttonStop;

	public StartStopView() {
		setOpaque(false);
		initGUI();
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill, inset 0 0 0 0"));
		add(getButtonStart(), "sg 1, w 110!,h 110!");
		add(getButtonStop(), "sg 1");
	}

	public AbstractButton getButtonStart() {
		if (buttonStart == null) {
			buttonStart = new StartStopButton(eStartStop.START);
			buttonStart.setText(GUIi18nManager.get(AbstractMachineFrame.I18N_START));
			buttonStart.setEnabled(false);
			buttonStart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonStartActionPerformed();
				}
			});
		}
		return buttonStart;
	}

	private void buttonStartActionPerformed() {
		getButtonStart().setEnabled(false);
		fireStart();
	}

	public AbstractButton getButtonStop() {
		if (buttonStop == null) {
			buttonStop = new StartStopButton(eStartStop.STOP);
			buttonStop.setEnabled(false);
			buttonStop.setText(GUIi18nManager.get(AbstractMachineFrame.I18N_STOP));
			buttonStop.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonStopActionPerformed();
				}
			});
		}
		return buttonStop;
	}

	private void buttonStopActionPerformed() {
		getButtonStop().setEnabled(false);
		fireStop();
	}

	@Override
	public void modelChanged() {
		getButtonStart().setEnabled(model.isStartEnabled());
		getButtonStop().setEnabled(model.isStopEnabled());
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		getButtonStart().setText(GUIi18nManager.get(AbstractMachineFrame.I18N_START));
		getButtonStop().setText(GUIi18nManager.get(AbstractMachineFrame.I18N_STOP));
	}

}
