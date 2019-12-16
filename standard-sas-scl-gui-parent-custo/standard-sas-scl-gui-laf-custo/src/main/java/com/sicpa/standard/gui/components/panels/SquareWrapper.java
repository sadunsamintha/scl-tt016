package com.sicpa.standard.gui.components.panels;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

public class SquareWrapper extends JPanel {

	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new MigLayout("fill"));
				final SquareWrapper panel = new SquareWrapper(new JButton("sdfsf"));
				f.add(panel, "grow");
				f.setSize(200, 200);
				f.setVisible(true);
			}
		});
	}

	private JComponent comp;

	public SquareWrapper(final JComponent comp) {
		super();
		this.comp = comp;
		setLayout(null);
		add(comp);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				int w = Math.min(getWidth(), getHeight());
				SquareWrapper.this.comp.setSize(w, w);
			}
		});
	}
}
