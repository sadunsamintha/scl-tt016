package com.sicpa.standard.gui.components.buttons.shape.spinner;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

public class SpinnerLeftButton extends SpinnerButton {

	private static final long serialVersionUID = 1L;

	@Override
	protected Paint getPaint() {
		return new GradientPaint(0, 0, Color.WHITE, (int) (getWidth() / 1.5), (int) (getHeight() / 1.5), Color.BLACK);
	}

	@Override
	protected Shape getShape() {
		int w = getWidth();
		int h = getHeight();

		GeneralPath shape = new GeneralPath();
		shape.moveTo(w, 0);
		shape.lineTo(w, h);
		shape.lineTo(0, h / 2);

		shape.closePath();
		return shape;
	}
}
