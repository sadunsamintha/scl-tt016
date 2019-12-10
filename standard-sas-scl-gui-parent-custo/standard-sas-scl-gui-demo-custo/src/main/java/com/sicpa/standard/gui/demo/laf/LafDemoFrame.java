package com.sicpa.standard.gui.demo.laf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class LafDemoFrame extends javax.swing.JFrame {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LafDemoFrame inst = new LafDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private JComboBox lafCombo;
	private JButton buttonApplyLaf;

	public LafDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new MigLayout());
		getContentPane().add(getLafCombo(), "wrap,span");
		getContentPane().add(getButtonApplyLaf(), "wrap");
		getContentPane().add(new JCheckBox("check"), "wrap");
		getContentPane().add(new JRadioButton("radio"), "grow,wrap");
		getContentPane().add(new JSpinner(), "grow");
		getContentPane().add(Box.createGlue(), "wrap ");

		getContentPane().add(new JSlider(), "wrap");
		getContentPane().add(new JScrollPane(new JTree()), "wrap, w 200, h 100");
		setSize(800, 600);
	}

	public JComboBox getLafCombo() {
		if (this.lafCombo == null) {
			this.lafCombo = new JComboBox();
			this.lafCombo.addItem(new MetalLookAndFeel());
			try {

				this.lafCombo
						.addItem(Class.forName("com.sun.java.swing.plaf.windows.WindowsLookAndFeel").newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				this.lafCombo.addItem(Class.forName("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel").newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.lafCombo.addItem(new SubstanceBusinessBlueSteelLookAndFeel());
			this.lafCombo.addItem(new SicpaLookAndFeelCusto().laf);

		}
		return this.lafCombo;
	}

	public JButton getButtonApplyLaf() {
		if (this.buttonApplyLaf == null) {
			this.buttonApplyLaf = new JButton("Apply laf");
			this.buttonApplyLaf.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonApplyLafActionPerformed();
				}
			});
		}
		return this.buttonApplyLaf;
	}

	private void buttonApplyLafActionPerformed() {
		try {
			UIManager.setLookAndFeel((LookAndFeel) this.lafCombo.getSelectedItem());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

}
