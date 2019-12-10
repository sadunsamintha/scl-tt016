package com.sicpa.standard.gui.demo.components.swing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.listener.Draggable;
import com.sicpa.standard.gui.utils.WindowsUtils;

public class NonOpaqueWindowDemo extends javax.swing.JDialog {

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				NonOpaqueWindowDemo inst = new NonOpaqueWindowDemo();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private JPanel panel;

	public NonOpaqueWindowDemo() {
		super();
		initGUI();
	}

	private void initGUI() {
		getContentPane().setLayout(new BorderLayout());
		Draggable.makeDraggable(this);
		setAlwaysOnTop(true);

		RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
		WindowsUtils.setOpaque(this, false);

		getContentPane().add(getPanel());
		setSize(400, 300);
	}

	public BufferedImage getTexture() {
		BufferedImage texture;

		texture = GraphicsUtilities.createCompatibleTranslucentImage(20, 20);
		Graphics2D g = (Graphics2D) texture.getGraphics();
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(3f));
		g.drawLine(0, 0, texture.getWidth(), texture.getHeight());
		g.drawLine(texture.getWidth(), 0, 0, texture.getHeight());
		g.dispose();

		return texture;
	}

	public JPanel getPanel() {
		if (this.panel == null) {
			this.panel = new JPanel() {
				@Override
				protected void paintComponent(final Graphics g) {
					Graphics2D g2 = (Graphics2D) g.create();
					TexturePaint tp = new TexturePaint(getTexture(), new Rectangle(0, 0, 20, 20));
					g2.setPaint(tp);
					g2.fillOval(0, 0, getWidth(), getHeight());
				}
			};
		}
		return this.panel;
	}
}
