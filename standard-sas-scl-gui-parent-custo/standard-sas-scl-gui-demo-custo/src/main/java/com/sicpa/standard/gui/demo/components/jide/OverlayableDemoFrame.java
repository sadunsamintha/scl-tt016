package com.sicpa.standard.gui.demo.components.jide;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.OverlayableIconsFactory;
import com.jidesoft.swing.OverlayableUtils;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class OverlayableDemoFrame extends javax.swing.JFrame {

	private DefaultOverlayable buttonOverlayed;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				OverlayableDemoFrame inst = new OverlayableDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public OverlayableDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			getContentPane().setLayout(new FlowLayout());
			getContentPane().add(getButtonOverlayed());
			setSize(400, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DefaultOverlayable getButtonOverlayed() {
		if (this.buttonOverlayed == null) {
			JLabel info = new JLabel(OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.INFO));
			info.setToolTipText("Tooltip message here");
			info.setVisible(false);

			JButton b = new JButton("button 1") {
				@Override
				protected void paintComponent(final Graphics g) {
					super.paintComponent(g);
					OverlayableUtils.repaintOverlayable(this);
				}
			};

			this.buttonOverlayed = new DefaultOverlayable(b, info, DefaultOverlayable.NORTH_EAST);
			this.buttonOverlayed.setOverlayLocationInsets(new Insets(4, 0, 0, 4));
			info.setVisible(true);
		}
		return this.buttonOverlayed;
	}

}
