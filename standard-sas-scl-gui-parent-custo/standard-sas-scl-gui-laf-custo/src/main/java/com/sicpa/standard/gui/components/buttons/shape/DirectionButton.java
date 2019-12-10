package com.sicpa.standard.gui.components.buttons.shape;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import com.sicpa.standard.gui.painter.SicpaFillPainter;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class DirectionButton extends AbstractShapeButton {
	private static final long serialVersionUID = 1L;

	public enum Direction {
		DOWN, UP, LEFT, RIGHT
	};

	private Direction direction;

	public DirectionButton(final Direction direction) {
		super();
		this.direction = direction;
		this.shape = getShape();
	}

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.getContentPane().setLayout(new BorderLayout());
				f.getContentPane().add(new DirectionButton(Direction.LEFT));
				f.setSize(400, 300);
				f.setVisible(true);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}

	public void setDirection(final Direction direction) {
		this.direction = direction;
		this.shape = getShape();
		this.shadowBuffer = null;
		repaint();

	}

	@Override
	protected Shape getShape() {
		if (this.direction == null) {
			return null;
		}
		GeneralPath shape = new GeneralPath();
		float w = getWidth() / 6;
		float h = getHeight() / 6;
		switch (this.direction) {
		case DOWN:
			shape.moveTo(3f * w, 6f * h);
			shape.lineTo(6f * w, 4f * h);
			shape.lineTo(5f * w, 4f * h);
			shape.lineTo(5f * w, 0f * h);
			shape.lineTo(1f * w, 0f * h);
			shape.lineTo(1f * w, 4f * h);
			shape.lineTo(0f * w, 4f * h);
			break;
		case UP:
			shape.moveTo(3f * w, 0f * h);
			shape.lineTo(6f * w, 2f * h);
			shape.lineTo(5f * w, 2f * h);
			shape.lineTo(5f * w, 6f * h);
			shape.lineTo(1f * w, 6f * h);
			shape.lineTo(1f * w, 2f * h);
			shape.lineTo(0f * w, 2f * h);
			break;
		case LEFT:
			if (getComponentOrientation().isLeftToRight()) {
				shape.moveTo(6f * w, 1.5f * h);
				shape.lineTo(2f * w, 1.5f * h);
				shape.lineTo(2f * w, 0f * h);
				shape.lineTo(0f * w, 3f * h);
				shape.lineTo(2f * w, 6f * h);
				shape.lineTo(2f * w, 4.5 * h);
				shape.lineTo(6f * w, 4.5 * h);
			} else {
				shape.moveTo(0f * w, 1.5f * h);
				shape.lineTo(4f * w, 1.5f * h);
				shape.lineTo(4f * w, 0f * h);
				shape.lineTo(6f * w, 3f * h);
				shape.lineTo(4f * w, 6f * h);
				shape.lineTo(4f * w, 4.5 * h);
				shape.lineTo(0f * w, 4.5 * h);
			}
			break;
		case RIGHT:
			if (getComponentOrientation().isLeftToRight()) {
				shape.moveTo(0f * w, 1.5f * h);
				shape.lineTo(4f * w, 1.5f * h);
				shape.lineTo(4f * w, 0f * h);
				shape.lineTo(6f * w, 3f * h);
				shape.lineTo(4f * w, 6f * h);
				shape.lineTo(4f * w, 4.5 * h);
				shape.lineTo(0f * w, 4.5 * h);
			} else {
				shape.moveTo(6f * w, 1.5f * h);
				shape.lineTo(2f * w, 1.5f * h);
				shape.lineTo(2f * w, 0f * h);
				shape.lineTo(0f * w, 3f * h);
				shape.lineTo(2f * w, 6f * h);
				shape.lineTo(2f * w, 4.5 * h);
				shape.lineTo(6f * w, 4.5 * h);
			}
			break;
		}

		shape.closePath();

		return shape;
	}

	@Override
	protected void paintShape(final Graphics2D g, final float fraction, final Shape shape) {

		if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			SicpaFillPainter painter = (SicpaFillPainter) SubstanceLookAndFeel.getCurrentSkin().getFillPainter();

			SubstanceColorScheme defaultScheme = SubstanceLookAndFeel.getCurrentSkin().getColorScheme(this,
					ComponentState.ENABLED);

			SubstanceColorScheme armedScheme = SubstanceLookAndFeel.getCurrentSkin().getColorScheme(this,
					ComponentState.ARMED);

			if (!isEnabled()) {
				defaultScheme = SubstanceLookAndFeel.getCurrentSkin().getColorScheme(this,
						ComponentState.DISABLED_DEFAULT);
				armedScheme = defaultScheme;
			}

			painter.paintContourBackground(g, this, getWidth(), getHeight(), getShape(), false, defaultScheme, true);
			g.setComposite(AlphaComposite.SrcOver.derive(getAnimProgress()));

			painter.paintContourBackground(g, this, getWidth(), getHeight(), getShape(), false, armedScheme, true);
		} else {
			g.setColor(SicpaColor.BLUE_MEDIUM);
			g.fill(getShape());
		}
	}
}
