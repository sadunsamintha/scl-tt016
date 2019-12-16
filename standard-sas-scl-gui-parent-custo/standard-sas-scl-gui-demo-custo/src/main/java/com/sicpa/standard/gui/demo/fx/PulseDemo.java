package com.sicpa.standard.gui.demo.fx;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.ColorTintFilter;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;

import com.jhlabs.image.GaussianFilter;
import com.sicpa.standard.gui.utils.PaintUtils;

public class PulseDemo extends JPanel {

	private float alpha;

	public PulseDemo() {
		Timeline timeline = new Timeline(this);
		timeline.setDuration(500);
		timeline.addPropertyToInterpolate("alpha", 0f, 1f);
		timeline.playLoop(RepeatBehavior.REVERSE);
	}

	@Override
	protected void paintComponent(final Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		int x = 50;
		int y = 50;
		int radius = 105;

		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, getWidth(), getHeight());

		g2.setComposite(AlphaComposite.SrcOver.derive(this.alpha));
		g2.drawImage(getBlurredImage(radius), x - radius, y - radius, null);
		g2.setComposite(AlphaComposite.SrcOver.derive(1f));
		g2.drawImage(getImage(), x, y, null);
		g2.dispose();
	}

	private BufferedImage img;
	private BufferedImage blurred;

	private BufferedImage getImage() {
		if (this.img == null) {
			this.img = GraphicsUtilities.createCompatibleTranslucentImage(150, 150);
			Graphics2D g2 = (Graphics2D) this.img.getGraphics();
			PaintUtils.turnOnAntialias(g2);
			g2.setColor(Color.RED);
			g2.fillOval(0, 0, this.img.getWidth(), this.img.getHeight());
			g2.dispose();
		}
		return this.img;
	}

	private BufferedImage getBlurredImage(final int radius) {
		if (this.blurred == null) {
			this.blurred = GraphicsUtilities.createCompatibleTranslucentImage(getImage().getWidth() + radius * 2,
					getImage().getHeight() + radius * 2);

			Graphics2D g2 = (Graphics2D) this.blurred.getGraphics();
			g2.drawImage(getImage(), radius, radius, null);
			g2.dispose();
			new ColorTintFilter(Color.WHITE, 1f).filter(this.blurred, this.blurred);
			new GaussianFilter(radius).filter(this.blurred, this.blurred);
		}
		return this.blurred;
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(350, 350);
				f.getContentPane().setLayout(new BorderLayout());
				f.getContentPane().add(new PulseDemo(), BorderLayout.CENTER);
				f.setVisible(true);
			}
		});
	}

	public void setAlpha(final float alpha) {
		this.alpha = alpha;
		repaint();
	}
}
