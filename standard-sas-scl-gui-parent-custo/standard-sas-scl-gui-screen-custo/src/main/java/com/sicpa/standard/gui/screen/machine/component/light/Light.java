package com.sicpa.standard.gui.screen.machine.component.light;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.pushingpixels.trident.Timeline;

import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.utils.PaintUtils;

public class Light extends JComponent {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().add(new Light());
				f.setSize(300, 300);
				f.setVisible(true);
			}
		});
	}

	private static final long serialVersionUID = 1L;
	private Color color = Color.red;
	private int animDuration = 400;

	private Timeline animator;

	public Light() {
		this(Color.red);
	}

	public Light(final Color color) {
		this.color = color;
		setOpaque(false);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				repaint();
			}
		});
	}

	@Override
	protected void paintComponent(final Graphics g) {
		int w = getWidth();
		int h = getHeight();
		int l = Math.min(w, h);
		PaintUtils.fillCircle(g, l, this.color, 0, 0);
	}

	public void showGreenColor() {
		showColor(SicpaColor.GREEN_DARK);
	}

	public void showRedColor() {
		showColor(SicpaColor.RED);
	}

	public void showYellowColor() {
		showColor(SicpaColor.YELLOW);
	}

	public void showColor(final Color color) {
		
		if (this.animator != null) {
			this.animator.abort();
		}
		
		if (isVisible()) {
			this.animator = new Timeline(this);
			this.animator.addPropertyToInterpolate("color", getColor(), color);
			this.animator.setDuration(this.animDuration);
			this.animator.play();
		} else {
			setColor(color);
		}
	}

	public Color getColor() {
		return this.color;
	}

	public void setColor(final Color color) {
		this.color = color;
		this.repaint();
	}

	public void setAnimDuration(final int animDuration) {
		this.animDuration = animDuration;
	}

	public int getAnimDuration() {
		return this.animDuration;
	}

	@Override
	public Color getForeground() {
		return getColor();
	}

}
