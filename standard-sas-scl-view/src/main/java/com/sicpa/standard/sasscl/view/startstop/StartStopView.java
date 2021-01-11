package com.sicpa.standard.sasscl.view.startstop;

import javax.swing.AbstractButton;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.buttons.StartStopButton;
import com.sicpa.standard.gui.components.buttons.StartStopButton.eStartStop;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.productionStatus.ProductionStatusView;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class StartStopView extends AbstractStartStopView {

	protected AbstractButton buttonStart;
	protected AbstractButton buttonStop;

	protected ProductionStatusView productionStatusView;

	public StartStopView() {
		setOpaque(false);
	}

	public void initGUI() {
		ThreadUtils.invokeLater(() -> {
			setLayout(new MigLayout("fill, inset 0 0 0 0"));
			add(getButtonStart(), "sg 1, w 110!,h 110!");
			add(getButtonStop(), "sg 1");
			add(productionStatusView, "w 50,h 50");
		});
	}

	public AbstractButton getButtonStart() {
		if (buttonStart == null) {
			buttonStart = new StartStopButton(eStartStop.START);
			buttonStart.setText(Messages.get("std.gui.machine.start"));
			buttonStart.setEnabled(false);
			buttonStart.addActionListener(e -> buttonStartActionPerformed());
		}
		return buttonStart;
	}

	protected void buttonStartActionPerformed() {
		getButtonStart().setEnabled(false);
		fireStart();
	}

	public AbstractButton getButtonStop() {
		if (buttonStop == null) {
			buttonStop = new StartStopButton(eStartStop.STOP);
			buttonStop.setEnabled(false);
			buttonStop.setText(Messages.get("std.gui.machine.stop"));
			buttonStop.addActionListener(e -> buttonStopActionPerformed());
		}
		return buttonStop;
	}

	protected void buttonStopActionPerformed() {
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
		getButtonStart().setText(Messages.get("std.gui.machine.start"));
		getButtonStop().setText(Messages.get("std.gui.machine.stop"));
	}

	public void setProductionStatusView(ProductionStatusView productionStatusView) {
		this.productionStatusView = productionStatusView;
	}

}
