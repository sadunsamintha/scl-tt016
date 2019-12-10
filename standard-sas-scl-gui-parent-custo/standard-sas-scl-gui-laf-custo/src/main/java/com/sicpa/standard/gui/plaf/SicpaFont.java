package com.sicpa.standard.gui.plaf;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

public class SicpaFont {
	public static Font getFont(final int size) {
		return new Font(SicpaLookAndFeelConfig.getFontName(), Font.PLAIN, size);
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JPanel p = new JPanel(new MigLayout("wrap 1"));
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setSize(800, 600);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				p = new JPanel(new MigLayout("wrap 1"));
				
				f.getContentPane().setLayout(new BorderLayout());
				JScrollPane jsp = new JScrollPane(p);
				f.getContentPane().add(jsp);

				JLabel label;
				for (int i = 5; i < 51; i++) {
					label = new JLabel(i + "  qwertzuiopasdfghjklyxcvnm QWERTZUIOPASDFGHJKLYXCVNM");
					label.setFont(getFont(i));
					p.add(label);
				}
				f.setVisible(true);
			}
		});
	}
}
