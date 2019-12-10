package com.sicpa.standard.gui.demo.worker;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import net.miginfocom.swing.MigLayout;

import org.divxdede.swing.busy.BusyModel;
import org.divxdede.swing.busy.DefaultBusyModel;
import org.divxdede.swing.busy.JBusyComponent;

import com.sicpa.standard.gui.components.buttons.StartStopButton;
import com.sicpa.standard.gui.components.buttons.StartStopButton.eStartStop;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class SwingWorkerDemo extends JFrame {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				SwingWorkerDemo f = new SwingWorkerDemo();
				f.setVisible(true);
			}
		});
	}

	private BusyModel busyModel;
	private JBusyComponent<JComponent> busyPanel;
	private JPanel mainpanel;
	private JButton buttonStart;

	public SwingWorkerDemo() {
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getBusyPanel());

		setSize(300, 300);
	}

	public JBusyComponent<JComponent> getBusyPanel() {
		if (this.busyPanel == null) {
			this.busyPanel = new JBusyComponent<JComponent>(getMainpanel());
			this.busyModel = new DefaultBusyModel();
			this.busyModel.setDeterminate(true);
			this.busyModel.setMinimum(0);
			this.busyModel.setMaximum(100);
			this.busyPanel.setBusyModel(this.busyModel);
		}
		return this.busyPanel;
	}

	private static class mySwingWorker extends SwingWorker<Object, Object> {
		@Override
		protected Object doInBackground() throws Exception {
			// do a long task
			for (int i = 0; i <= 100; i++) {
				setProgress(i);
				ThreadUtils.sleepQuietly(50);
			}
			return null;
		}
	}

	public JPanel getMainpanel() {
		if (this.mainpanel == null) {
			this.mainpanel = new JPanel(new MigLayout());
			this.mainpanel.add(getButtonStart(), "h 150!,w 150!");
		}
		return this.mainpanel;
	}

	public JButton getButtonStart() {
		if (this.buttonStart == null) {
			this.buttonStart = new StartStopButton(eStartStop.START);
			this.buttonStart.setText("START");
			this.buttonStart.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonStartActionPerformed();
				}
			});
		}
		return this.buttonStart;
	}

	private void buttonStartActionPerformed() {
		getBusyPanel().setBusy(true);
		mySwingWorker worker = new mySwingWorker();
		worker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				progressChanged(evt);
			}
		});
		worker.execute();
	}

	private void progressChanged(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("progress")) {
			Integer progress = (Integer) evt.getNewValue();
			this.busyModel.setValue(progress);

		} else if (evt.getPropertyName().equals("state")) {
			if (evt.getNewValue() == StateValue.DONE) {
				getBusyPanel().setBusy(false);
			}
		}
	}
}
