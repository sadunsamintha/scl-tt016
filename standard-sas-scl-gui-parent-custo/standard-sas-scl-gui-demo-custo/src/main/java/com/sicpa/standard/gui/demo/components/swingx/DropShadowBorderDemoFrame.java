package com.sicpa.standard.gui.demo.components.swingx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.border.DropShadowBorder;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class DropShadowBorderDemoFrame extends javax.swing.JFrame {

	private JPanel centerPanel;

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				DropShadowBorderDemoFrame inst = new DropShadowBorderDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public DropShadowBorderDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			getContentPane().setLayout(new BorderLayout());
			SicpaLookAndFeelCusto.flagAsWorkArea(getCenterPanel());
			getContentPane().add(getCenterPanel(), BorderLayout.CENTER);

			setSize(400, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JPanel getCenterPanel() {
		if (this.centerPanel == null) {
			this.centerPanel = new JPanel();

			JScrollPane jsp = new JScrollPane(new JTree());
			jsp.setPreferredSize(new Dimension(200, 200));
			this.centerPanel.add(jsp);
			jsp.setBorder(new DropShadowBorder(Color.BLACK, 10, 1f, 15, false, false, true, true));

		}
		return this.centerPanel;
	}

}
