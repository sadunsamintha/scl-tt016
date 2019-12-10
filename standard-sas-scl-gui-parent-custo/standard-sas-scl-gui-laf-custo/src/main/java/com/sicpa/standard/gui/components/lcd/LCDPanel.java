package com.sicpa.standard.gui.components.lcd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.PaintUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class LCDPanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {

		SicpaLookAndFeelCusto.install();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				final LCDPanel timer = new LCDPanel();
				f.getContentPane().setLayout(new MigLayout());
				f.getContentPane().add(timer);
				f.setSize(300, 175);
				f.setVisible(true);
				//
				new Thread(new Runnable() {
					@Override
					public void run() {
						int i = 0;
						while (true) {
							timer.setValue(i++);
							ThreadUtils.sleepQuietly(50);
						}
					}
				}).start();
			}
		});
	}

	protected BufferedImage[] DIGIT_ARRAY;

	protected BufferedImage[] createDigitImages(final Color digitColor) {
		return new BufferedImage[] { createDigit(0, digitColor), createDigit(1, digitColor),
				createDigit(2, digitColor), createDigit(3, digitColor), createDigit(4, digitColor),
				createDigit(5, digitColor), createDigit(6, digitColor), createDigit(7, digitColor),
				createDigit(8, digitColor), createDigit(9, digitColor), createDigit(-1, digitColor) };
	}

	protected BufferedImage backgroundImage;

	public LCDPanel() {
		this(new Color(0xEEFFEE));
	}

	public void setDigitColor(final Color digitColor) {
		this.DIGIT_ARRAY = createDigitImages(digitColor);
		repaint();
	}

	public LCDPanel(final Color digitColor) {

		this.DIGIT_ARRAY = createDigitImages(digitColor);

		setPreferredSize(new Dimension(275, 100));
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				LCDPanel.this.backgroundImage = null;
				repaint();
			}
		});
	}

	protected int value = 0;
	protected int minimunOfDigit = 5;
	private double scale = 1;

	public void setScale(final double scale) {
		this.scale = scale;
		repaint();
	}

	public void setMinimunOfDigit(final int minDigit) {
		this.minimunOfDigit = minDigit;
	}

	public void setValue(final int value) {
		this.value = value;
		repaint();
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (!isShowing()) {
			return;
		}

		Graphics2D g2 = (Graphics2D) g.create();
		PaintUtils.turnOnQualityRendering(g2);

		if (this.backgroundImage == null) {
			this.backgroundImage = createBackground(getWidth(), getHeight());
		}

		g2.drawImage(this.backgroundImage, 0, 0, this);

		g2.scale(this.scale, this.scale);

		int w = (int) (this.backgroundImage.getWidth() / this.scale);

		String sValue = this.value + "";

		int currentDigit = 1;
		int totalDigit = sValue.length();

		for (int i = totalDigit - 1; i >= 0; i--) {
			g2.drawImage(this.DIGIT_ARRAY[Integer.parseInt(sValue.charAt(i) + "")], w - 50 * currentDigit - 5, 20, this);
			currentDigit++;
		}

		int delta = this.minimunOfDigit - totalDigit;
		if (delta > 0) {
			for (int j = totalDigit + 1; j < this.minimunOfDigit + 1; j++) {
				g2.drawImage(this.DIGIT_ARRAY[10], w - 50 * j - 5, 20, this);
			}
		}

		g2.dispose();
	}

	protected BufferedImage createBackground(final int width, final int height) {
		BufferedImage image = GraphicsUtilities.createCompatibleTranslucentImage(width, height);

		Graphics2D g2 = (Graphics2D) image.getGraphics();
		PaintUtils.turnOnQualityRendering(g2);

		final float[] FRACTIONS = { 0.0f, 1.0f };

		final Point2D START_HIGHLIGHT = new Point2D.Float(0, 79);
		final Point2D STOP_HIGHLIGHT = new Point2D.Float(0, 166);

		final float[] FRACTIONS_HIGHLIGHT = { 0.0f, 0.9f, 1.0f };

		final Color[] COLORS_HIGHLIGHT = { new Color(0x000000), new Color(0x000000), new Color(0x6C8095) };

		final Point2D START = new Point2D.Float(0, 80);
		final Point2D STOP = new Point2D.Float(0, 165);

		final Color[] COLORS_LEFT = { new Color(0x2F4566), new Color(0x243D54) };

		// bottom
		final LinearGradientPaint GRADIENT_HIGHLIGHT = new LinearGradientPaint(START_HIGHLIGHT, STOP_HIGHLIGHT,
				FRACTIONS_HIGHLIGHT, COLORS_HIGHLIGHT);
		g2.setPaint(GRADIENT_HIGHLIGHT);
		g2.fillRect(0, 0, image.getWidth() - 1, image.getHeight() - 1);

		// background behind digit
		final LinearGradientPaint GRADIENT_LEFT = new LinearGradientPaint(START, STOP, FRACTIONS, COLORS_LEFT);
		g2.setPaint(GRADIENT_LEFT);
		g2.fillRect(1, 1, image.getWidth() - 3, image.getHeight() - 3);

		g2.dispose();

		return image;
	}

	private static BufferedImage createDigit(final int digit, final Color digitColor) {
		BufferedImage IMAGE = GraphicsUtilities.createCompatibleTranslucentImage(46, 65);

		final Color COLOR_ON = digitColor;
		final Color FRAME_COLOR_ON = new Color(50, 200, 100, 128);
		final Color COLOR_OFF = new Color(0x4E5571);
		final Color FRAME_COLOR_OFF = new Color(61, 57, 142, 128);
		final BasicStroke FRAME_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

		Graphics2D g2 = (Graphics2D) IMAGE.getGraphics();
		PaintUtils.turnOnQualityRendering(g2);

		// A
		GeneralPath segment_a = new GeneralPath();
		segment_a.moveTo(17, 0);
		segment_a.lineTo(38, 0);
		segment_a.lineTo(37, 8);
		segment_a.lineTo(16, 8);
		segment_a.closePath();

		// B
		GeneralPath segment_b = new GeneralPath();
		segment_b.moveTo(39, 0);
		segment_b.lineTo(41, 0);
		segment_b.quadTo(45, 0, 45, 5);
		segment_b.lineTo(41, 32);
		segment_b.lineTo(38, 32);
		segment_b.lineTo(35, 28);
		segment_b.closePath();

		// C
		GeneralPath segment_c = new GeneralPath();
		segment_c.moveTo(37, 33);
		segment_c.lineTo(41, 33);
		segment_c.lineTo(37, 60);
		segment_c.quadTo(36, 65, 32, 65);
		segment_c.lineTo(30, 65);
		segment_c.lineTo(34, 37);
		segment_c.closePath();

		// D
		GeneralPath segment_d = new GeneralPath();
		segment_d.moveTo(9, 57);
		segment_d.lineTo(30, 57);
		segment_d.lineTo(29, 65);
		segment_d.lineTo(8, 65);
		segment_d.closePath();

		// E
		GeneralPath segment_e = new GeneralPath();
		segment_e.moveTo(4, 33);
		segment_e.lineTo(8, 33);
		segment_e.lineTo(11, 37);
		segment_e.lineTo(7, 65);
		segment_e.lineTo(4, 65);
		segment_e.quadTo(0, 65, 0, 60);
		segment_e.closePath();

		// F
		GeneralPath segment_f = new GeneralPath();
		segment_f.moveTo(8, 5);
		segment_f.quadTo(8, 0, 13, 0);
		segment_f.lineTo(16, 0);
		segment_f.lineTo(12, 28);
		segment_f.lineTo(8, 32);
		segment_f.lineTo(4, 32);
		segment_f.closePath();

		// G
		GeneralPath segment_g = new GeneralPath();
		segment_g.moveTo(14, 29);
		segment_g.lineTo(34, 29);
		segment_g.lineTo(36, 33);
		segment_g.lineTo(32, 37);
		segment_g.lineTo(13, 37);
		segment_g.lineTo(9, 33);
		segment_g.closePath();

		g2.setStroke(FRAME_STROKE);

		switch (digit) {
		case -1:
			g2.setColor(COLOR_OFF);
			g2.fill(segment_a);
			g2.fill(segment_b);
			g2.fill(segment_c);
			g2.fill(segment_d);
			g2.fill(segment_e);
			g2.fill(segment_f);
			g2.fill(segment_g);
			g2.draw(segment_a);
			g2.draw(segment_b);
			g2.draw(segment_c);
			g2.draw(segment_d);
			g2.draw(segment_e);
			g2.draw(segment_f);
			g2.draw(segment_g);
			break;
		case 0:
			g2.setColor(COLOR_ON);
			g2.fill(segment_a);
			g2.fill(segment_b);
			g2.fill(segment_c);
			g2.fill(segment_d);
			g2.fill(segment_e);
			g2.fill(segment_f);
			g2.setColor(COLOR_OFF);
			g2.fill(segment_g);
			g2.setColor(FRAME_COLOR_ON);
			g2.draw(segment_a);
			g2.draw(segment_b);
			g2.draw(segment_c);
			g2.draw(segment_d);
			g2.draw(segment_e);
			g2.draw(segment_f);
			g2.setColor(FRAME_COLOR_OFF);
			g2.draw(segment_g);
			break;
		case 1:
			g2.setColor(COLOR_ON);
			g2.fill(segment_b);
			g2.fill(segment_c);
			g2.setColor(COLOR_OFF);
			g2.fill(segment_a);
			g2.fill(segment_d);
			g2.fill(segment_e);
			g2.fill(segment_f);
			g2.fill(segment_g);
			g2.setColor(FRAME_COLOR_ON);
			g2.draw(segment_b);
			g2.draw(segment_c);
			g2.setColor(FRAME_COLOR_OFF);
			g2.draw(segment_a);
			g2.draw(segment_d);
			g2.draw(segment_e);
			g2.draw(segment_f);
			g2.draw(segment_g);
			break;
		case 2:
			g2.setColor(COLOR_ON);
			g2.fill(segment_a);
			g2.fill(segment_b);
			g2.fill(segment_d);
			g2.fill(segment_e);
			g2.fill(segment_g);
			g2.setColor(COLOR_OFF);
			g2.fill(segment_c);
			g2.fill(segment_f);
			g2.setColor(FRAME_COLOR_ON);
			g2.draw(segment_a);
			g2.draw(segment_b);
			g2.draw(segment_d);
			g2.draw(segment_e);
			g2.draw(segment_g);
			g2.setColor(FRAME_COLOR_OFF);
			g2.draw(segment_c);
			g2.draw(segment_f);
			break;
		case 3:
			g2.setColor(COLOR_ON);
			g2.fill(segment_a);
			g2.fill(segment_b);
			g2.fill(segment_c);
			g2.fill(segment_d);
			g2.fill(segment_g);
			g2.setColor(COLOR_OFF);
			g2.fill(segment_e);
			g2.fill(segment_f);
			g2.setColor(FRAME_COLOR_ON);
			g2.draw(segment_a);
			g2.draw(segment_b);
			g2.draw(segment_c);
			g2.draw(segment_d);
			g2.draw(segment_g);
			g2.setColor(FRAME_COLOR_OFF);
			g2.draw(segment_e);
			g2.draw(segment_f);
			break;
		case 4:
			g2.setColor(COLOR_ON);
			g2.fill(segment_b);
			g2.fill(segment_c);
			g2.fill(segment_f);
			g2.fill(segment_g);
			g2.setColor(COLOR_OFF);
			g2.fill(segment_a);
			g2.fill(segment_d);
			g2.fill(segment_e);
			g2.setColor(FRAME_COLOR_ON);
			g2.draw(segment_b);
			g2.draw(segment_c);
			g2.draw(segment_f);
			g2.draw(segment_g);
			g2.setColor(FRAME_COLOR_OFF);
			g2.draw(segment_a);
			g2.draw(segment_d);
			g2.draw(segment_e);
			break;
		case 5:
			g2.setColor(COLOR_ON);
			g2.fill(segment_a);
			g2.fill(segment_c);
			g2.fill(segment_d);
			g2.fill(segment_f);
			g2.fill(segment_g);
			g2.setColor(COLOR_OFF);
			g2.fill(segment_b);
			g2.fill(segment_e);
			g2.setColor(FRAME_COLOR_ON);
			g2.draw(segment_a);
			g2.draw(segment_c);
			g2.draw(segment_d);
			g2.draw(segment_f);
			g2.draw(segment_g);
			g2.setColor(FRAME_COLOR_OFF);
			g2.draw(segment_b);
			g2.draw(segment_e);
			break;
		case 6:
			g2.setColor(COLOR_ON);
			g2.fill(segment_a);
			g2.fill(segment_c);
			g2.fill(segment_d);
			g2.fill(segment_e);
			g2.fill(segment_f);
			g2.fill(segment_g);
			g2.setColor(COLOR_OFF);
			g2.fill(segment_b);
			g2.setColor(FRAME_COLOR_ON);
			g2.draw(segment_a);
			g2.draw(segment_c);
			g2.draw(segment_d);
			g2.draw(segment_e);
			g2.draw(segment_f);
			g2.draw(segment_g);
			g2.setColor(FRAME_COLOR_OFF);
			g2.draw(segment_b);
			break;
		case 7:
			g2.setColor(COLOR_ON);
			g2.fill(segment_a);
			g2.fill(segment_b);
			g2.fill(segment_c);
			g2.setColor(COLOR_OFF);
			g2.fill(segment_d);
			g2.fill(segment_e);
			g2.fill(segment_f);
			g2.fill(segment_g);
			g2.setColor(FRAME_COLOR_ON);
			g2.draw(segment_a);
			g2.draw(segment_b);
			g2.draw(segment_c);
			g2.setColor(FRAME_COLOR_OFF);
			g2.draw(segment_d);
			g2.draw(segment_e);
			g2.draw(segment_f);
			g2.draw(segment_g);
			break;
		case 8:
			g2.setColor(COLOR_ON);
			g2.fill(segment_a);
			g2.fill(segment_b);
			g2.fill(segment_c);
			g2.fill(segment_d);
			g2.fill(segment_e);
			g2.fill(segment_f);
			g2.fill(segment_g);
			g2.setColor(FRAME_COLOR_ON);
			g2.draw(segment_a);
			g2.draw(segment_b);
			g2.draw(segment_c);
			g2.draw(segment_d);
			g2.draw(segment_e);
			g2.draw(segment_f);
			g2.draw(segment_g);
			break;
		case 9:
			g2.setColor(COLOR_ON);
			g2.fill(segment_a);
			g2.fill(segment_b);
			g2.fill(segment_c);
			g2.fill(segment_d);
			g2.fill(segment_f);
			g2.fill(segment_g);
			g2.setColor(COLOR_OFF);
			g2.fill(segment_e);
			g2.setColor(FRAME_COLOR_ON);
			g2.draw(segment_a);
			g2.draw(segment_b);
			g2.draw(segment_c);
			g2.draw(segment_d);
			g2.draw(segment_f);
			g2.draw(segment_g);
			g2.setColor(FRAME_COLOR_OFF);
			g2.draw(segment_e);
			break;
		default:
			g2.setColor(COLOR_OFF);
			g2.fill(segment_a);
			g2.fill(segment_b);
			g2.fill(segment_c);
			g2.fill(segment_d);
			g2.fill(segment_e);
			g2.fill(segment_f);
			g2.fill(segment_g);
			g2.setColor(FRAME_COLOR_OFF);
			g2.draw(segment_a);
			g2.draw(segment_b);
			g2.draw(segment_c);
			g2.draw(segment_d);
			g2.draw(segment_e);
			g2.draw(segment_f);
			g2.draw(segment_g);
			break;
		}

		g2.dispose();

		return IMAGE;
	}
}
