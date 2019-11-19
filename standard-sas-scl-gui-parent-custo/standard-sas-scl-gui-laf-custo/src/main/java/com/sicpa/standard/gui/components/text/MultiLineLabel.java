package com.sicpa.standard.gui.components.text;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class MultiLineLabel extends JTextArea {
	private static final long serialVersionUID = 1L;

	public MultiLineLabel() {
		this("");
	}

	public MultiLineLabel(final String text) {
		setEditable(false);
		setOpaque(false);
		setLineWrap(true);
		setWrapStyleWord(true);
		setBorder(BorderFactory.createEmptyBorder());
		setText(text);
		setHighlighter(null);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(0, 0);
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				f.setSize(350, 300);
				MultiLineLabel l = new MultiLineLabel();
				l.setText("This is multi line label, it looks like a JLabel but it is in fact a modified JTextArea");
				f.getContentPane().add((l));
				f.setVisible(true);
			}
		});
	}
}
