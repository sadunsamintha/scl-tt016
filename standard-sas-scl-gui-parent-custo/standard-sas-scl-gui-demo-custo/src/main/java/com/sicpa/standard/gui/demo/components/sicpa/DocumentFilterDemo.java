package com.sicpa.standard.gui.demo.components.sicpa;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.components.text.filter.SizeDocumentFilter;
import com.sicpa.standard.gui.components.text.filter.UpperCaseDocumentFilter;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class DocumentFilterDemo {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.getContentPane().setLayout(new FlowLayout());
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JTextField text = new JTextField(10);
				SizeDocumentFilter sdf = new SizeDocumentFilter(5);
				new UpperCaseDocumentFilter(sdf).installFilter(text);
				f.getContentPane().add(text);
				f.setSize(200, 200);
				f.setVisible(true);
			}
		});
	}
}
