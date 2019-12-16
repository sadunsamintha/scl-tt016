package com.sicpa.standard.gui.debug;

import java.awt.Component;

import javax.swing.JFrame;

public class TestFrame {

	public static JFrame createTestFrame(int w, int h, Component panel) {
		JFrame f = new JFrame();
		f.setSize(w, h);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(panel);
		return f;
	}

	public static JFrame createTestFrame(Component panel) {
		return createTestFrame(800, 600, panel);
	}

}
