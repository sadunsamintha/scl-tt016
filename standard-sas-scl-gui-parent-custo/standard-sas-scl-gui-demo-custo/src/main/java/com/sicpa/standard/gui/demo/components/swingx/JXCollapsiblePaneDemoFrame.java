package com.sicpa.standard.gui.demo.components.swingx;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXCollapsiblePane;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class JXCollapsiblePaneDemoFrame extends javax.swing.JFrame {

	private JXCollapsiblePane collapsiblePane;
	private JPanel panelWest;
	private JToggleButton toggle;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				JXCollapsiblePaneDemoFrame inst = new JXCollapsiblePaneDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public JXCollapsiblePaneDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			setLayout(new BorderLayout());

			getContentPane().add(getPanelWest(), BorderLayout.CENTER);
			getContentPane().add(getToggle(), BorderLayout.SOUTH);

			setSize(400, 300);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JXCollapsiblePane getCollapsiblePane() {
		if (this.collapsiblePane == null) {
			this.collapsiblePane = new JXCollapsiblePane();
			this.collapsiblePane.setAnimated(true);
			this.collapsiblePane.setLayout(new MigLayout("wrap 2"));
			this.collapsiblePane.setBorder(BorderFactory.createTitledBorder("collapsible pane"));
			for (int i = 0; i < 10; i++) {
				this.collapsiblePane.add(new JButton(String.valueOf(i)));
			}
		}
		return this.collapsiblePane;
	}

	public JToggleButton getToggle() {
		if (this.toggle == null) {
			this.toggle = new JToggleButton("toggle");
			this.toggle.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					JXCollapsiblePaneDemoFrame.this.collapsiblePane.setCollapsed(JXCollapsiblePaneDemoFrame.this.toggle
							.isSelected());
				}
			});
		}
		return this.toggle;
	}

	public JPanel getPanelWest() {
		if (this.panelWest == null) {
			this.panelWest = new JPanel();
			this.panelWest.add(getCollapsiblePane());
		}
		return this.panelWest;
	}
}
