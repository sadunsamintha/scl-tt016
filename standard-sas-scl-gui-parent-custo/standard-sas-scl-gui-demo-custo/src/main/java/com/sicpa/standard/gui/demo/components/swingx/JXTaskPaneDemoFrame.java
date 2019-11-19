package com.sicpa.standard.gui.demo.components.swingx;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

@SuppressWarnings("serial")
public class JXTaskPaneDemoFrame extends javax.swing.JFrame {

	private JXTaskPaneContainer westPanel;
	private JXTaskPane taskPane1;
	private JXTaskPane taskPane2;

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				JXTaskPaneDemoFrame inst = new JXTaskPaneDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public JXTaskPaneDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new MigLayout("fill"));
		getContentPane().add(getWestPanel(), "grow");
		setSize(400, 500);
		pack();
	}

	public JXTaskPaneContainer getWestPanel() {
		if (westPanel == null) {
			westPanel = new JXTaskPaneContainer();
			westPanel.add(getTaskPane1());
			westPanel.add(getTaskPane2());
		}
		return westPanel;
	}

	public JXTaskPane getTaskPane1() {
		if (taskPane1 == null) {
			taskPane1 = new JXTaskPane();
			taskPane1.setTitle("Abstract actions");
			taskPane1.add(new AbstractAction("copy") {

				@Override
				public void actionPerformed(final ActionEvent e) {
				}
			});
			taskPane1.add(new AbstractAction("paste") {

				@Override
				public void actionPerformed(final ActionEvent e) {
				}
			});
			taskPane1.add(new AbstractAction("find") {

				@Override
				public void actionPerformed(final ActionEvent e) {
				}
			});
		}
		return taskPane1;
	}

	public JXTaskPane getTaskPane2() {
		if (taskPane2 == null) {
			taskPane2 = new JXTaskPane();
			taskPane2.setTitle("Components");
			taskPane2.add(new JButton(" button"));
			taskPane2.add(new JLabel("JTree:"));
			JScrollPane scroll = new JScrollPane(new JTree());
			scroll.setPreferredSize(new Dimension(250, 200));
			taskPane2.add(scroll);

		}
		return taskPane2;
	}
}
