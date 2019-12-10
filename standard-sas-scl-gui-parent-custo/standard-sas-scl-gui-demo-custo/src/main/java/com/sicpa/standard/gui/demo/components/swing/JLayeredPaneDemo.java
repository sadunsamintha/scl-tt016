package com.sicpa.standard.gui.demo.components.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.DMS.MasterPCCClientTestFrame;

public class JLayeredPaneDemo extends MasterPCCClientTestFrame {

	private myLayer bluePanel;
	private myLayer redPanel;
	private myLayer greenPanel;
	private myLayer yellowPanel;

	public JLayeredPaneDemo() {
		super();

		getLayeredPane().add(getYellowPanel());
		getLayeredPane().setLayer(getYellowPanel(), JLayeredPane.DRAG_LAYER - 1);

		getLayeredPane().add(getRedPanel());
		getLayeredPane().setLayer(getRedPanel(), JLayeredPane.DRAG_LAYER - 2);

		getLayeredPane().add(getBluePanel());
		getLayeredPane().setLayer(getBluePanel(), JLayeredPane.DRAG_LAYER - 3);

		getLayeredPane().add(getGreenPanel());
		getLayeredPane().setLayer(getGreenPanel(), JLayeredPane.DRAG_LAYER - 4);

		// track the size change
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent arg0) {
				thisComponentResized();
			}
		});
	}

	private void thisComponentResized() {
		getGreenPanel().setSize(getSize());
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JLayeredPaneDemo f = new JLayeredPaneDemo();
				f.setVisible(true);
			}
		});
	}

	@Override
	protected BufferedImage getSicpaLogo() {
		return null;
	}

	public myLayer getBluePanel() {
		if (this.bluePanel == null) {
			this.bluePanel = new myLayer(Color.BLUE, 0.2f);
			this.bluePanel.setBounds(100, 100, 500, 600);
		}
		return this.bluePanel;
	}

	public myLayer getRedPanel() {
		if (this.redPanel == null) {
			this.redPanel = new myLayer(Color.RED, 0.2f);
			this.redPanel.setBounds(0, 0, 800, 600);

			this.redPanel.setLayout(new MigLayout("wrap 2"));
			for (int i = 0; i < 15; i++) {
				this.redPanel.add(new JButton("button"));
			}
		}
		return this.redPanel;
	}

	public myLayer getGreenPanel() {
		if (this.greenPanel == null) {
			this.greenPanel = new myLayer(Color.green, 0.2f);
			this.greenPanel.setBounds(0, 0, 0, 0);
		}
		return this.greenPanel;
	}

	public myLayer getYellowPanel() {
		if (this.yellowPanel == null) {
			this.yellowPanel = new myLayer(Color.YELLOW, 0.8f);
			this.yellowPanel.setBounds(50, 50, 100, 100);
		}
		return this.yellowPanel;
	}

	private static class myLayer extends JPanel {
		private Color color;
		private float alpha;

		public myLayer(final Color c, final float alpha) {
			this.color = c;
			this.alpha = alpha;
			setOpaque(false);
		}

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setComposite(AlphaComposite.SrcOver.derive(this.alpha));
			g2.setColor(this.color);
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.dispose();
		}
	}
}
