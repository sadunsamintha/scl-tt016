package com.sicpa.standard.gui.components.slider;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.utils.PaintUtils;

public class SicpaSlider extends JPanel {

	private static final long serialVersionUID = 1L;
	protected int min;
	protected int max;
	protected int value;

	protected Color iconTopColor = SicpaColor.BLUE_DARK;
	protected Color iconBottomColor = SicpaColor.BLUE_MEDIUM.darker();
	protected Color iconBorderColor = SicpaColor.BLUE_DARK;
	protected Color textColor = SicpaColor.BLUE_ULTRA_LIGHT;
	protected Color backgroundColor = SicpaColor.BLUE_LIGHT;
	protected Color backgroundBorderColor = SicpaColor.BLUE_DARK;

	public SicpaSlider(int min, int max, int value) {
		this.min = min;
		this.max = max;
		this.value = value;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				handleMouseEvent(e);
			}
		});
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				handleMouseEvent(e);
			}
		});
	}

	protected void handleMouseEvent(MouseEvent e) {
		if (isEnabled()) {
			setValue(valueAtX(e.getX()-getIconWidth()/2));
		}
	}

	protected int valueAtX(int x) {
		int fx = (int) ((((float) max - min) / getTrackWidth()) * x);
		if (fx < min) {
			fx = min;
		} else if (fx > max) {
			fx = max;
		}
		return fx;
	}

	protected int xForValue(int value) {
		int range = max - min;
		int amount = max - value;
		int x = (getTrackWidth() - (amount * getTrackWidth()) / range);
		return x;
	}

	protected int getTrackWidth() {
		return getWidth() - getIconWidth() - 2;
	}

	protected int getIconWidth() {
		return 30;
	}

	protected int getIconHeight() {
		return getHeight() - 6;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g.create();
		PaintUtils.turnOnAntialias(g2);

		// background
		RoundRectangle2D casing = new RoundRectangle2D.Double(1, 0, getWidth() - 2, getHeight() - 3, getHeight(),
				getHeight());

		g2.setColor(backgroundColor);
		g2.fill(casing);

		Stroke stroke = g2.getStroke();
		g2.setStroke(new BasicStroke(2.0f));
		g2.setColor(backgroundBorderColor);
		g2.draw(casing);
		g2.setStroke(stroke);

		// icon
		int iconWidth = getIconWidth();
		int iconHeight = getIconHeight();

		int position = xForValue(value);
		g2.translate(position, 0);

		Ellipse2D thumb = new Ellipse2D.Double(0, 1, iconWidth, iconHeight);

		// shadow
		g2.translate(2, 2);
		g2.setPaint(Color.GRAY);
		g2.fill(thumb);
		g2.translate(-2, -2);

		g2.setPaint(new GradientPaint(0, 2, iconTopColor, 0, getIconHeight(), iconBottomColor));
		g2.fill(thumb);

		g2.setColor(iconBorderColor);
		g2.setStroke(new BasicStroke(2.0f));

		g2.draw(thumb);
		g2.setStroke(stroke);

		// text
		String text = String.valueOf(value);
		int textLength = SwingUtilities.computeStringWidth(g2.getFontMetrics(), text);
		g2.setColor(textColor);
		g2.drawString(text, getIconWidth() / 2 - textLength / 2, getHeight() / 2 + g2.getFont().getSize() / 2 - 2);

		g2.dispose();

		// g2 = (Graphics2D) g.create();
		// g2.setColor(Color.red);
		// g2.drawLine(0, getHeight() / 2 - 1, getWidth(), getHeight() / 2 - 1);

	}

	public void setValue(int value) {
		int oldValue = this.value;
		this.value = value;
		repaint();
		firePropertyChange("value", oldValue, value);
	}

	public int getValue() {
		return value;
	}

	public Color getIconTopColor() {
		return iconTopColor;
	}

	public void setIconTopColor(Color iconTopColor) {
		this.iconTopColor = iconTopColor;
	}

	public Color getIconBottomColor() {
		return iconBottomColor;
	}

	public void setIconBottomColor(Color iconBottomColor) {
		this.iconBottomColor = iconBottomColor;
	}

	public Color getIconBorderColor() {
		return iconBorderColor;
	}

	public void setIconBorderColor(Color iconBorderColor) {
		this.iconBorderColor = iconBorderColor;
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getBackgroundBorderColor() {
		return backgroundBorderColor;
	}

	public void setBackgroundBorderColor(Color backgroundBorderColor) {
		this.backgroundBorderColor = backgroundBorderColor;
	}

}