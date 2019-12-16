package com.sicpa.standard.gui.demo.components.jxlayer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.divxdede.swing.busy.DefaultBusyModel;
import org.divxdede.swing.busy.JBusyComponent;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

@SuppressWarnings("serial")
public class BusyComponentDemoFrame extends JFrame {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				BusyComponentDemoFrame f = new BusyComponentDemoFrame();
				f.setSize(800, 600);
				f.setVisible(true);
			}
		});
	}

	private JButton determiateProgressButton;
	private JButton indetermiateProgressButton;
	private JPanel panelBusyAble;
	private JBusyComponent<JPanel> busyPanel;

	public BusyComponentDemoFrame() {
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getBusyPanel(), BorderLayout.CENTER);
	}

	public JButton getIndetermiateProgressButton() {
		if (indetermiateProgressButton == null) {
			indetermiateProgressButton = new JButton("inderteminate");
			indetermiateProgressButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					indetermiateProgressButtonActionPerformed();
				}
			});
		}
		return this.indetermiateProgressButton;
	}

	public JButton getDetermiateProgressButton() {
		if (determiateProgressButton == null) {
			determiateProgressButton = new JButton("determiate");
			determiateProgressButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					determiateProgressButtonActionPerformed();
				}
			});
		}
		return this.determiateProgressButton;
	}

	public JBusyComponent<JPanel> getBusyPanel() {
		if (busyPanel == null) {
			busyPanel = new JBusyComponent<JPanel>();
			busyPanel.setView(getPanelBusyAble());
		}
		return busyPanel;
	}

	private void indetermiateProgressButtonActionPerformed() {
		DefaultBusyModel model = new DefaultBusyModel();
		model.setDescription("random action in progress...");
		busyPanel.setBusyModel(model);
		model.setDeterminate(false);
		busyPanel.setBusy(true);

		new Thread(new Runnable() {
			public void run() {
				ThreadUtils.sleepQuietly(5000);
				busyPanel.setBusy(false);
			}
		}).start();
	}

	private void determiateProgressButtonActionPerformed() {
		DefaultBusyModel model = new DefaultBusyModel();
		model.setDescription("determinate action in progress");
		model.setDeterminate(true);
		model.setMinimum(0);
		model.setMaximum(100);
		busyPanel.setBusyModel(model);
		busyPanel.setBusy(true);
		new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i <= 100; i++) {
					ThreadUtils.sleepQuietly(100);
					busyPanel.getBusyModel().setValue(i);
				}
				busyPanel.setBusy(false);
			}
		}).start();
	}

	public JPanel getPanelBusyAble() {
		if (panelBusyAble == null) {
			panelBusyAble = new JPanel(new MigLayout(""));
			panelBusyAble.add(getDetermiateProgressButton(), "");
			panelBusyAble.add(getIndetermiateProgressButton(), "");
		}
		return panelBusyAble;
	}
}
