package com.sicpa.standard.gui.plaf;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;


public class alignControlFrame extends javax.swing.JFrame {

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();

				alignControlFrame inst = new alignControlFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public alignControlFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new MigLayout());
		SicpaLookAndFeelCusto.flagAsWorkArea((JComponent) getContentPane());

		getContentPane().add(new JTextField(5));
		getContentPane().add(new JPasswordField(5));
		getContentPane().add(new JSpinner());
		DefaultComboBoxModel model = new DefaultComboBoxModel(new Object[] { 132, 132, 132 });
		getContentPane().add(new JComboBox(model), "w 150");
		getContentPane().add(new JButton("test"));
		getContentPane().add(new JToggleButton("test"));
		getContentPane().add(new JCheckBox("check"));
		getContentPane().add(new JRadioButton("radio"));

		pack();
	}

}
