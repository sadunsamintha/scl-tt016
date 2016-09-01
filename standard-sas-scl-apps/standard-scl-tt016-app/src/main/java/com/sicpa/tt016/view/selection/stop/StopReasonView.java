package com.sicpa.tt016.view.selection.stop;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.client.common.i18n.Messages;

import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import static com.sicpa.tt016.view.selection.stop.StopReason.*;

@SuppressWarnings("serial")
public class StopReasonView extends AbstractView<IStopReasonListener, StopReasonModel> {

	protected StopReasonPanel stopReasonPanel;

	public StopReasonView() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("ltr,fill"));
		add(new JLabel(Messages.get("stopreason.view.title")));
		add(new JSeparator(), "growx, pushx, wrap");
		add(getStopReasonPanel(), "span, split 2, pushy, growx, growy");

	}


	public StopReasonPanel getStopReasonPanel() {
		if (stopReasonPanel == null) {
			stopReasonPanel = new StopReasonPanel();
		}

		return stopReasonPanel;
	}

	@Override
	public void modelChanged() {
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(1024, 640);
		frame.setContentPane(new StopReasonView());
		frame.setVisible(true);
	}

	private class StopReasonPanel extends JPanel {

		public StopReasonPanel() {
			initGui();
		}

		private void initGui() {
			setLayout(new MigLayout("ltr,fill"));
			add(getStopReasonButton(PRODUCT_CHANGE), "growx, growy");
			add(getStopReasonButton(END_OF_PRODUCTION), "growx, growy, wrap");
			add(getStopReasonButton(PURGE_PRODUCTION), "growx, growy");
			add(getStopReasonButton(PREVENTIVE_MAINTENANCE), "growx, growy, wrap");
			add(getStopReasonButton(CORRECTIVE_MAINTENANCE), "growx, growy");
		}


		private JButton getStopReasonButton(StopReason stopReason) {
			JButton stopReasonButton = new JButton(Messages.get(stopReason.getKey()));
			stopReasonButton.addActionListener(e -> fireStopReasonSelected(stopReason));

			return stopReasonButton;
		}
	}

	private void fireStopReasonSelected(StopReason stopReason) {
		for (IStopReasonListener listener : listeners) {
			listener.stopReasonSelected(stopReason);
		}
	}
}
