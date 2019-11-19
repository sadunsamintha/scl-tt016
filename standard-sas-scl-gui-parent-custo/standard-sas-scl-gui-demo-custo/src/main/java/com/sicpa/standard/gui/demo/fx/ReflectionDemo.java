package com.sicpa.standard.gui.demo.fx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.graphics.ReflectionRenderer;

import com.jhlabs.image.GaussianFilter;
import com.sicpa.standard.gui.utils.ImageUtils;

public class ReflectionDemo {
	public static void main(final String[] args) {
		manualReflection();
		swingXReflection();
	}

	public static void manualReflection() {
		try {
			BufferedImage clock = ImageUtils.createRandomColorCirlceImage();//GraphicsUtilities.loadCompatibleImage(ReflectionDemo.class.getResource("clock.PNG"));
			BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(clock.getWidth(), clock.getHeight());
			Graphics2D g2 = (Graphics2D) img.createGraphics();
			g2.translate(0, clock.getHeight());
			g2.scale(1.0, -1.0);
			g2.drawImage(clock, 0, 0, null);
			g2.scale(1.0, -1.0);
			g2.translate(0, -clock.getHeight());

			g2.setPaint(new GradientPaint(0.0f, 0.0f, new Color(1.0f, 1.0f, 1.0f, 0.5f), 0.0f, img.getHeight(),
					new Color(0.0f, 0.0f, 0.0f, 0.0f), true));
			g2.setComposite(AlphaComposite.DstIn);
			g2.fillRect(0, 0, img.getWidth(), img.getHeight());
			g2.dispose();

			new GaussianFilter(5).filter(img, img);

			BufferedImage img2 = GraphicsUtilities.createCompatibleTranslucentImage(clock.getWidth(),
					clock.getHeight() * 2);
			g2 = (Graphics2D) img2.createGraphics();
			g2.drawImage(clock, 0, 0, null);
			g2.drawImage(img, 0, img.getHeight() + 5, null);
			g2.dispose();
//
			ImageUtils.showImage(img2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void swingXReflection() {
		try {
			BufferedImage clock =ImageUtils.createRandomColorCirlceImage();// GraphicsUtilities.loadCompatibleImage(ReflectionDemo.class.getResource("clock.PNG"));
			ReflectionRenderer rr = new ReflectionRenderer();
			rr.setBlurEnabled(true);
			BufferedImage img = rr.appendReflection(clock);
			ImageUtils.showImage(img);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
