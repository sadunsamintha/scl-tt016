package com.sicpa.standard.gui.demo.paint;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.utils.PaintUtils;

public class RadialGradientDemoFrame extends JPanel {

	private CycleMethod[] rules = { CycleMethod.NO_CYCLE, CycleMethod.REFLECT, CycleMethod.REPEAT };

	private String[] rulesNames = { "NO_CYCLE", "REFLECT", "REPEAT" };

	public RadialGradientDemoFrame() {
		initGUI();
	}

	private void initGUI() {
		setBackground(Color.GRAY);
		setLayout(new MigLayout());

		add(new JLabel("center X"));
		{
			final JSlider slider = new JSlider();
			slider.setMinimum(0);
			slider.setMaximum(100);
			slider.setValue(50);
			add(slider);
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(final ChangeEvent e) {
					RadialGradientDemoFrame.this.centerX = slider.getValue();
					repaint();
				}
			});
		}
		add(new JLabel("center Y"));
		{
			final JSlider slider = new JSlider();
			slider.setMinimum(0);
			slider.setMaximum(100);
			slider.setValue(50);
			add(slider, "wrap");
			slider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					RadialGradientDemoFrame.this.centerY = slider.getValue();
					repaint();
				}
			});
		}
		add(new JLabel("focus X"));
		{
			final JSlider slider = new JSlider();
			slider.setMinimum(0);
			slider.setMaximum(100);
			slider.setValue(50);
			add(slider);
			slider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					RadialGradientDemoFrame.this.focusX = slider.getValue();
					repaint();
				}
			});
		}
		add(new JLabel("focus Y"));
		{
			final JSlider slider = new JSlider();
			slider.setMinimum(0);
			slider.setMaximum(100);
			slider.setValue(50);
			add(slider);
			slider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					RadialGradientDemoFrame.this.focusY = slider.getValue();
					repaint();
				}
			});
		}
		add(new JLabel("radius"));
		{
			final JSlider slider = new JSlider();
			slider.setMinimum(1);
			slider.setMaximum(200);
			slider.setValue(25);
			add(slider);
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(final ChangeEvent e) {
					RadialGradientDemoFrame.this.radius = slider.getValue();
					repaint();
				}
			});
		}

	}

	int centerX = 50;
	int centerY = 50;
	int focusX = 50;
	int focusY = 50;
	int radius = 25;

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		for (int x = 20, i = 0; i < this.rules.length; x += 130, i++) {
			BufferedImage img = GraphicsUtilities.createCompatibleImage(100, 100);
			Graphics2D g2 = (Graphics2D) img.getGraphics();
			Point center = new Point(this.centerX, this.centerY);
			Point focus = new Point(this.focusX, this.focusY);
			float[] dist = { 0.0f, 0.2f, 1.0f };
			Color[] colors = { Color.RED, Color.WHITE, Color.BLUE };
			RadialGradientPaint rgp = new RadialGradientPaint(center, this.radius, focus, dist, colors, this.rules[i]);
			g2.setPaint(rgp);
			g2.fillRect(0, 0, 100, 100);
			g2.dispose();
			g.drawImage(img, x, 100, null);
			g.drawString(this.rulesNames[i], x, 220);
		}

		// ---
		g.drawString("Examples of a radial gradient use", 50, 270);
		paintRafialEffect1(g);
		paintRafialEffect2(g);

	}

	protected void paintRafialEffect1(final Graphics g) {
		int width = 100;
		int height = 100;
		Point center = new Point(10, 10);
		int radius = 80;
		Point focus = new Point(-15, -15);
		float[] dist = { 0f, 1.0f };
		Color[] colors = { Color.WHITE, Color.red.darker() };
		RadialGradientPaint rgp = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
		BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(width, height);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		PaintUtils.turnOnAntialias(g2);
		g2.setPaint(rgp);
		g2.fillOval(0, 0, width, height);
		g2.dispose();
		g.drawImage(img, 50, 300, null);
		g.setColor(Color.BLACK);
	}

	protected void paintRafialEffect2(final Graphics g) {
		int w = 100;
		Color color = Color.RED;

		BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(getWidth(), getHeight());

		Graphics2D g2 = (Graphics2D) img.createGraphics();
		PaintUtils.turnOnAntialias(g2);

		g2.setColor(color);
		g2.fillOval(1, 1, w - 2, w - 2);

		g2.setComposite(AlphaComposite.DstIn.derive(0.9f));

		Point center = new Point(w / 2, w / 2);
		float[] dist = { 0.0f, 0.7f, 1.0f };
		Color[] colors = { new Color(0, 0, 0, 255), new Color(0, 0, 0, 255), new Color(0, 0, 0, 0) };
		int radius = w / 2;

		RadialGradientPaint rgp = new RadialGradientPaint(center, radius, center, dist, colors, CycleMethod.NO_CYCLE);
		g2.setPaint(rgp);

		g2.fillOval(0, 0, w, w);
		g2.dispose();

		g.drawImage(img, 200, 300, null);
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("radial gradient paint");
				frame.add(new RadialGradientDemoFrame());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(850, 450);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});

	}
}
