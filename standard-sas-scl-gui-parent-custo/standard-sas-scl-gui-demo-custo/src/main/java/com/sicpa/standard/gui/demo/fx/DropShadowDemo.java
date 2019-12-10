package com.sicpa.standard.gui.demo.fx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.PaintUtils;

public class DropShadowDemo {
	public static BufferedImage getImage() {
		int h = 150;
		int w = 150;
		BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(w, h);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		PaintUtils.turnOnAntialias(g2);
		g2.setColor(Color.BLUE);
		g2.fill(getShape(w, h));

		img = ImageUtils.getShadowedImage(img);
		g2 = (Graphics2D) img.getGraphics();
		// show the bounds of the image
		g2.setColor(Color.BLACK);
		g2.drawRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
		g2.dispose();

		return img;
	}

	private static Shape getShape(final int width, final int height) {
		int h = height / 6;
		int w = width / 6;
		GeneralPath shape = new GeneralPath();
		shape.moveTo(2f * w, 0);
		shape.lineTo(4f * w, 0);
		shape.lineTo(4f * w, 2f * h);
		shape.lineTo(6 * w, 2f * h);
		shape.lineTo(6 * w, 4f * h);
		shape.lineTo(4f * w, 4f * h);
		shape.lineTo(4f * w, 6 * h);
		shape.lineTo(2f * w, 6 * h);
		shape.lineTo(2f * w, 4f * h);
		shape.lineTo(0, 4f * h);
		shape.lineTo(0, 2f * h);
		shape.lineTo(2f * w, 2f * h);
		shape.closePath();
		return shape;
	}

	public static void main(final String[] args) {
		ImageUtils.showImage(getImage());
	}
}
