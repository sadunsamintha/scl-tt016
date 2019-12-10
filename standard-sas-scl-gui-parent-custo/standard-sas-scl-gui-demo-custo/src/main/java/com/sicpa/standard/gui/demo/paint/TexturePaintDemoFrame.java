package com.sicpa.standard.gui.demo.paint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.PaintUtils;

public class TexturePaintDemoFrame extends JPanel {

	public TexturePaintDemoFrame() {
		initGUI();
	}

	private void initGUI() {

	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		TexturePaint tp = new TexturePaint(getTexture(), new Rectangle(0, 0, 20, 20));
		g2.setPaint(tp);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.dispose();
	}

	private BufferedImage texture;

	public BufferedImage getTexture() {
		if (this.texture == null) {
			this.texture = GraphicsUtilities.createCompatibleImage(20, 20);
			Graphics2D g = (Graphics2D) this.texture.getGraphics();
			PaintUtils.turnOnAntialias(g);
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.BLACK);
			g.setStroke(new BasicStroke(2f));
			g.drawLine(0, 0, this.texture.getWidth(), this.texture.getHeight());
			g.drawLine(this.texture.getWidth(), 0, 0, this.texture.getHeight());
			g.dispose();
			ImageUtils.showImage(this.texture);
		}
		return this.texture;
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("texture gradient paint");
				frame.add(new TexturePaintDemoFrame());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(850, 450);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});

	}
}
