package com.sicpa.standard.gui.utils;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.ColorTintFilter;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.utils.SubstanceImageCreator;

import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.PerspectiveFilter;
import com.jhlabs.image.RotateFilter;
import com.sicpa.standard.gui.plaf.SicpaColor;

public class ImageUtils {

	public static void showImage(final Image img) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.getContentPane().setLayout(new BorderLayout());
				f.setSize(800, 600);
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				JXImagePanel panel = new JXImagePanel();
				panel.setImage(img);
				f.getContentPane().add(panel);
				f.setVisible(true);
			}
		});
	}

	public static BufferedImage getShadowedImage(final Image img) {
		return getShadowedImage(img, 2, 2, 5, Color.BLACK, 1f, 0.6f, true);
	}

	public static BufferedImage getShadowedImage(final Image img, final int xoffset, final int yoffset,
			final int blurrRadius, final Color shadowColor, final float shadowColorLikeness, final float alpha,
			final boolean increaseSize) {
		if (img == null) {
			return null;
		}

		BufferedImage shadow;
		if (increaseSize) {
			shadow = GraphicsUtilities.createCompatibleTranslucentImage(img.getWidth(null) + xoffset + blurrRadius,
					img.getHeight(null) + yoffset + blurrRadius);
		} else {
			shadow = GraphicsUtilities.createCompatibleTranslucentImage(img.getWidth(null), img.getHeight(null));
		}

		// create the shadow
		Graphics2D g2 = (Graphics2D) shadow.getGraphics();
		g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
		g2.drawImage(img, xoffset, yoffset, null);
		g2.dispose();

		new ColorTintFilter(shadowColor, shadowColorLikeness).filter(shadow, shadow);
		if (blurrRadius > 0) {
			new GaussianFilter(blurrRadius).filter(shadow, shadow);
		}

		// paint the original image
		g2 = (Graphics2D) shadow.getGraphics();
		g2.drawImage(img, 0, 0, null);
		g2.dispose();

		return shadow;
	}

	/**
	 * 
	 * @param direction
	 *            SwingConstant.South/North/East/West
	 * @param color
	 * @return
	 */
	public static ImageIcon getArrowIcon(final int direction, final Color color, int fontSize) {
		final SubstanceColorScheme mainActiveScheme = SubstanceLookAndFeel.getCurrentSkin().getActiveColorScheme(
				SubstanceLookAndFeel.getDecorationType(new JLabel()));
		Icon icon = SubstanceImageCreator.getArrowIcon(fontSize, direction, mainActiveScheme);
		BufferedImage buff = GraphicsUtilities.createCompatibleTranslucentImage(icon.getIconWidth(),
				icon.getIconHeight());
		Graphics2D g = (Graphics2D) buff.getGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.setComposite(AlphaComposite.SrcAtop);
		g.setColor(color);
		g.fillRect(0, 0, buff.getWidth(), buff.getHeight());
		g.dispose();

		return new ImageIcon(buff);
	}

	public static BufferedImage createRandomColorCirlceImage() {
		return createRandomColorCirlceImage(100);
	}

	public static BufferedImage createRandomColorCirlceImage(final int size) {

		BufferedImage image = GraphicsUtilities.createCompatibleTranslucentImage(size, size);
		Graphics g = image.createGraphics();
		Random r = new Random();

		PaintUtils.fillCircle(g, size, new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)), 0, 0);

		g.dispose();
		return image;
	}

	public static BufferedImage createRandomStrippedImage(final int size) {
		return createRandomStrippedImage(size, size);
	}

	public static BufferedImage createRandomStrippedImage(final int w, final int h) {
		return createRandomStrippedImage(w, h, 10);
	}

	public static BufferedImage createRandomStrippedImage(final int w, final int h, final int l) {

		BufferedImage image = GraphicsUtilities.createCompatibleTranslucentImage(w, h);
		Graphics g = image.createGraphics();
		Random r = new Random();
		g.setColor(Color.BLACK);
		Color c = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));

		Point start = new Point(0, 0);
		Point end = new Point(l, l);
		float[] fractions = new float[] { 0, 0.5f, 1 };
		Color[] colors = new Color[] { SicpaColor.BLUE_ULTRA_LIGHT, c, SicpaColor.BLUE_ULTRA_LIGHT };

		LinearGradientPaint lgp = new LinearGradientPaint(start, end, fractions, colors, CycleMethod.REFLECT);

		Graphics2D g2 = (Graphics2D) g.create();
		PaintUtils.turnOnAntialias(g2);

		g2.setPaint(lgp);
		g2.fillRoundRect(0, 0, image.getWidth(), image.getHeight(), 20, 20);

		g2.dispose();

		g.dispose();
		return image;
	}

	public static BufferedImage changeSizeKeepRatio(final Image image, final int w, final int h) {

		BufferedImage buff = ImageUtils.convertToBufferedImage(image);
		if (buff == null) {
			return null;
		}

		float ratioW = (float) buff.getWidth() / w;
		float ratioH = (float) buff.getHeight() / h;
		float ratio = Math.max(ratioH, ratioW);

		int newW = (int) ((float) buff.getWidth() / ratio);
		int newH = (int) ((float) buff.getHeight() / ratio);

		BufferedImage newimage = GraphicsUtilities.createCompatibleTranslucentImage(newW, newH);
		Graphics2D g2 = newimage.createGraphics();
		PaintUtils.turnOnQualityRendering(g2);
		g2.drawImage(image, 0, 0, newW, newH, null);
		g2.dispose();

		return newimage;
	}

	public static BufferedImage createThumbnailKeepRatio(final Image image, final int w, final int h) {

		BufferedImage buff = convertToBufferedImage(image);
		if (buff == null) {
			return null;
		}

		if (w >= buff.getWidth() && h >= buff.getHeight()) {
			return buff;
		}

		float ratioW = (float) buff.getWidth() / w;
		float ratioH = (float) buff.getHeight() / h;
		float ratio = Math.max(ratioH, ratioW);

		int newW = (int) ((float) buff.getWidth() / ratio);
		int newH = (int) ((float) buff.getHeight() / ratio);

		return GraphicsUtilities.createThumbnail(buff, newW, newH);
	}

	public static BufferedImage convertToBufferedImage(final Icon icon) {
		if (icon == null) {
			return null;
		}
		BufferedImage image = GraphicsUtilities.createCompatibleTranslucentImage(icon.getIconWidth(),
				icon.getIconHeight());
		Graphics g = image.getGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		return image;
	}

	public static BufferedImage convertToBufferedImage(final Image image) {
		if (image == null) {
			return null;
		}

		BufferedImage buff;
		if (image instanceof BufferedImage) {
			buff = (BufferedImage) image;
		} else {
			buff = GraphicsUtilities.convertToBufferedImage(image);
		}
		buff = GraphicsUtilities.convertToBufferedImage(image);

		return buff;
	}

	public static BufferedImage createCameraImage() {
		BufferedImage image = GraphicsUtilities.createCompatibleImage(200, 200);
		BufferedImage dmImg = createDatamatrix(16, 16, 5, 1, 3);
		Graphics2D g = image.createGraphics();
		g.drawImage(dmImg, 50, 50, null);
		g.dispose();
		return image;
	}

	public static BufferedImage createDatamatrix(int row, int col) {
		return createDatamatrix(row, col, 3, 1, 2);
	}

	public static BufferedImage createDatamatrix(int row, int col, int dotSize, int gap, int blur) {
		BufferedImage image = GraphicsUtilities.createCompatibleImage((dotSize + gap) * col + blur, (dotSize + gap)
				* row + blur);

		Graphics2D g = image.createGraphics();

		g.translate(blur, blur);

		g.setColor(Color.GRAY);

		for (int currentRow = 0; currentRow < row; currentRow++) {
			for (int currentCol = 0; currentCol < col; currentCol++) {
				double ran = 0;
				if (currentCol == 0 || currentRow == row - 1) {
					ran = 1;
				} else {
					ran = Math.random();
				}
				if (ran > 0.5) {
					g.fillRect(currentCol * (dotSize + gap), currentRow * (dotSize + gap), dotSize, dotSize);
				}
			}
		}
		g.dispose();

		if (blur > 0) {
			new GaussianFilter(blur).filter(image, image);
		}

		return image;
	}

	public static BufferedImage createBarcodeImage(final int w, final int h) {
		BufferedImage barcodeImage;

		int cpt = 0;

		Color dark = new Color(220, 220, 220);
		Color light = Color.WHITE;
		// generate the barcode
		barcodeImage = GraphicsUtilities.createCompatibleTranslucentImage(w, 1);
		Graphics2D g = barcodeImage.createGraphics();
		int test;
		Random r = new Random();
		for (int i = 0; i < w; i += 2) {
			test = r.nextInt(2);
			if (test == 0) {
				if (cpt == 3) {
					g.setColor(light);
					cpt = 0;
				} else {
					g.setColor(dark);
					cpt++;
				}
			} else {
				if (cpt == -3) {
					g.setColor(dark);
					cpt = 0;
				} else {
					g.setColor(light);
					cpt--;
				}
			}
			g.drawLine(i, 0, i, 1);
			g.drawLine(i + 1, 0, i + 1, 1);
		}
		g.dispose();
		int offset = 5;
		int div = 12;

		// create top center bottom image
		BufferedImage center = GraphicsUtilities.createCompatibleTranslucentImage(w, (div - 2) * h / div);
		g = center.createGraphics();
		g.drawImage(barcodeImage, 0, 0, w, h, null);
		g.dispose();

		BufferedImage top = GraphicsUtilities.createCompatibleTranslucentImage(w + offset, h / div);
		g = top.createGraphics();
		g.drawImage(barcodeImage, 0, 0, w + offset, h, null);
		g.dispose();

		BufferedImage bottom = GraphicsUtilities.createCompatibleTranslucentImage(w + offset, h / div);
		g = bottom.createGraphics();
		g.drawImage(barcodeImage, 0, 0, w + offset, h, null);
		g.dispose();

		// add perpesctive to top and bottom
		PerspectiveFilter p = new PerspectiveFilter();
		p.setCorners(offset, 0, w + offset, 0, w, h / div, 0, h / div);
		p.filter(top, top);

		p = new PerspectiveFilter();
		p.setCorners(0, 0, w, 0, w + offset, h / div, offset, h / div);
		p.filter(bottom, bottom);

		// agregate the 3 images
		BufferedImage tmp = GraphicsUtilities.createCompatibleTranslucentImage(w + 50, h);
		g = tmp.createGraphics();
		g.drawImage(top, 0, 0, null);
		g.drawImage(center, 0, top.getHeight(), null);
		g.drawImage(bottom, 0, top.getHeight() + center.getHeight(), null);
		g.dispose();

		// add shadow effect at top and bottom
		// how it work
		// 1=> make the edges a bit translucent using dstIn
		// 2=> the shadow image appear at the edges because it was made translucent in 1/
		barcodeImage = GraphicsUtilities.createCompatibleTranslucentImage(w + offset, h);
		g = barcodeImage.createGraphics();
		g.drawImage(tmp, 0, 0, null);
		Color colorEdge = new Color(0, 0, 0, 50);
		Color colorOpaque = new Color(255, 255, 255, 255);
		Point start = new Point(0, 0);
		Point end = new Point(0, h);
		Color[] colors = new Color[] { colorEdge, colorOpaque, colorOpaque, colorOpaque, colorEdge };
		float[] fractions = new float[] { 0, 0.1f, 0.5f, 0.9f, 1 };
		LinearGradientPaint lgp = new LinearGradientPaint(start, end, fractions, colors);
		g.setPaint(lgp);
		g.setComposite(AlphaComposite.DstIn);
		g.fillRect(0, 0, w + offset, h);
		g.dispose();
		barcodeImage = ImageUtils.getShadowedImage(barcodeImage);

		return barcodeImage;
	}

	public static BufferedImage rotate(Image img, double angleInRadian) {

		BufferedImage input = convertToBufferedImage(img);

		RotateFilter2 filter = new RotateFilter2((float) angleInRadian, true);
		Rectangle rect = new Rectangle(0, 0, input.getWidth(), input.getHeight());
		filter.transformSpace(rect);

		BufferedImage output = GraphicsUtilities.createCompatibleTranslucentImage((int) rect.getWidth(),
				(int) rect.getHeight());

		filter.filter(input, output);

		return output;
	}

	private static class RotateFilter2 extends RotateFilter {
		public RotateFilter2(float angle, boolean resize) {
			super(angle, resize);
		}

		@Override
		protected void transformSpace(Rectangle rect) {
			super.transformSpace(rect);
		}
	}
}
