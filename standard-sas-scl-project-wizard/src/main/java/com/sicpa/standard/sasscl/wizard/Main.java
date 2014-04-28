package com.sicpa.standard.sasscl.wizard;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.sasscl.wizard.context.ProjectContext;
import com.sicpa.standard.sasscl.wizard.view.MainPanel;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					SicpaLookAndFeel.install();
					JFrame frame = new JFrame();
					frame.setSize(1024, 768);
					frame.setTitle("STD SAS-SCL project generation tool "+ProjectContext.getVersion());
					frame.getContentPane().add(new MainPanel());
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
