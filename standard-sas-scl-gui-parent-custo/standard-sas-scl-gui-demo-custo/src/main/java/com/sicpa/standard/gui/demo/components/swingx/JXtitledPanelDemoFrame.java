package com.sicpa.standard.gui.demo.components.swingx;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXTitledPanel;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class JXtitledPanelDemoFrame extends javax.swing.JFrame {

	private JXTitledPanel centerPanel;

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				JXtitledPanelDemoFrame inst = new JXtitledPanelDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public JXtitledPanelDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {

			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			getContentPane().add(new JXTitledPanel("WEST"), BorderLayout.WEST);
			getContentPane().add(new JXTitledPanel("NORTH"), BorderLayout.NORTH);
			getContentPane().add(new JXTitledPanel("SOUTH"), BorderLayout.SOUTH);
			getContentPane().add(new JXTitledPanel("EAST"), BorderLayout.EAST);
			getContentPane().add(getCenterPanel(), BorderLayout.CENTER);
			setSize(800, 600);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JXTitledPanel getCenterPanel() {
		if (this.centerPanel == null) {
			this.centerPanel = new JXTitledPanel("CENTER");
			Container contentPanel = this.centerPanel.getContentContainer();
			contentPanel.setLayout(new FlowLayout());
			contentPanel.add(new JButton("button1"));
			contentPanel.add(new JButton("button2"));

		}
		return this.centerPanel;
	}

}
