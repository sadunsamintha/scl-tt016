package com.sicpa.standard.gui.plaf.ui;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.ui.SubstanceLabelUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class SicpaLabelUI extends SubstanceLabelUI {


	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaLabelUI();
	}

	public SicpaLabelUI() {
	}

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JLabel label = new JLabel("TESTEST/ESTEST");
				label.setFont(new Font("arial", Font.BOLD, 55));
				f.getContentPane().add(label);
				f.pack();
				f.setVisible(true);
			}
		});
	}
}
