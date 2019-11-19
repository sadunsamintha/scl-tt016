package com.sicpa.standard.gui.painter;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import javax.swing.JProgressBar;
import javax.swing.JScrollBar;

import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.painter.fill.StandardFillPainter;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.sicpa.standard.gui.utils.PaintUtils;

public class SicpaFillPainter extends StandardFillPainter {
	@Override
	public String getDisplayName() {
		return "SicpaFillPainter";
	}

	private void addShine(final Graphics2D g, final boolean hasShine, final Color topShineColor,
			final Color bottomShineColor, final int width, final int height, final Shape contour,
			final Color topFillColor, final Color midFillColorTop) {
		if (hasShine && (topShineColor != null) && (bottomShineColor != null)) {
			int shineHeight = (int) (height / 1.8);
			int kernelSize = (int) Math.min(12, Math.pow(Math.min(width, height), 0.8) / 4);
			if (kernelSize < 3)
				kernelSize = 3;

			BufferedImage blurredGhostContour = SubstanceCoreUtilities.getBlankImage(width + 2 * kernelSize, height + 2
					* kernelSize);
			Graphics2D blurredGhostGraphics = (Graphics2D) blurredGhostContour.getGraphics().create();
			PaintUtils.turnOnAntialias(blurredGhostGraphics);

			blurredGhostGraphics.setColor(Color.black);
			blurredGhostGraphics.translate(kernelSize, kernelSize);
			int step = kernelSize > 5 ? 2 : 1;
			for (int strokeSize = 2 * kernelSize - 1; strokeSize > 0; strokeSize -= step) {
				float transp = 1.0f - strokeSize / (2.0f * kernelSize);
				blurredGhostGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, transp));
				blurredGhostGraphics.setStroke(new BasicStroke(strokeSize));
				blurredGhostGraphics.draw(contour);
			}
			blurredGhostGraphics.dispose();

			BufferedImage reverseGhostContour = SubstanceCoreUtilities.getBlankImage(width + 2 * kernelSize, height + 2
					* kernelSize);
			Graphics2D reverseGraphics = (Graphics2D) reverseGhostContour.getGraphics();
			Color bottomShineColorTransp = new Color(bottomShineColor.getRed(), bottomShineColor.getGreen(),
					bottomShineColor.getBlue(), 64);
			GradientPaint gradientShine = new GradientPaint(0, kernelSize, topShineColor, 0, kernelSize + shineHeight,
					bottomShineColorTransp);
			reverseGraphics.setPaint(gradientShine);
			reverseGraphics.fillRect(0, kernelSize, width + 2 * kernelSize, kernelSize + shineHeight);
			reverseGraphics.setComposite(AlphaComposite.DstOut);
			reverseGraphics.drawImage(blurredGhostContour, 0, 0, null);

			g.drawImage(reverseGhostContour, 0, 0, width - 1, shineHeight, kernelSize, kernelSize, kernelSize + width
					- 1, kernelSize + shineHeight, null);
		}
	}

	@Override
	public void paintContourBackground(final Graphics g, final Component comp, final int width, final int height, final Shape contour,
			final boolean isFocused, final SubstanceColorScheme fillScheme, final boolean hasShine) {

		Graphics2D graphics = (Graphics2D) g.create();

		int w = contour.getBounds().width;
		int h = contour.getBounds().height;

		if (w == 0 || h == 0) {
			return;
		}
		if (comp instanceof JProgressBar) {
			// it paints only the not filled area by the progress
			graphics.setComposite(AlphaComposite.SrcOver.derive(0.2f));
			PaintUtils.paintHighlight(graphics, 0, 0, width, height);
		} else if (comp instanceof JScrollBar) {
			Point start = new Point(0, 0);
			Point end = new Point(0, height);

			Color c = fillScheme.getMidColor();
			Color[] colors = new Color[] { c, c, c.darker() };
			float[] fractions = new float[] { 0, 0.6f, 1f };
			LinearGradientPaint lgp = new LinearGradientPaint(start, end, fractions, colors);
			graphics.setPaint(lgp);
			graphics.fillRect(0, 0, width, height);
		} else {
//			double cycleCoef = 1.0 - cyclePos;
			super.paintContourBackground(graphics, comp, width, height, contour, isFocused, fillScheme, false);
			// ---- compute the colors
//			
//			Color topFillColor = this.getTopFillColor(fillScheme);
//			Color midFillColorTop = this.getMidFillColorTop(fillScheme);
//			Color midFillColorBottom = this.getMidFillColorBottom(fillScheme);
//			Color bottomFillColor = this.getBottomFillColor(fillScheme);
//			Color topShineColor = this.getTopShineColor(fillScheme);
//			Color bottomShineColor = this.getBottomShineColor(fillScheme);
//
//			// Fill background
//			// long millis000 = System.nanoTime();
//
//			// graphics.clip(contour);
//			MultipleGradientPaint gradient = new LinearGradientPaint(0, 0, 0,
//					height, new float[] { 0.0f, 0.4999999f, 0.5f, 1.0f },
//					new Color[] { topFillColor, midFillColorTop,
//							midFillColorBottom, bottomFillColor },
//					CycleMethod.REPEAT);
//			graphics.setPaint(gradient);
//			graphics.fill(contour);
//			graphics.clip(contour);
//
//			// shine
//			if (hasShine) {
////				graphics.setComposite(AlphaComposite.SrcOver.derive((float) cycleCoef * 0.25f));
////				addShine(graphics, hasShine, topShineColor, bottomShineColor, width, height, contour, topFillColor,
////						midFillColorTop);
//			}
		}

		graphics.dispose();
	}

//	@Override
//	public void paintContourBackground(final Graphics arg0, final Component comp, final int width, final int height,
//			final Shape contour, final boolean isFocused, final SubstanceColorScheme colorScheme1,
//			final SubstanceColorScheme colorScheme2, final float cyclePos, final boolean hasShine,
//			final boolean useCyclePosAsInterpolation) {
//		Graphics2D graphics = (Graphics2D) arg0.create();
//		SubstanceColorScheme interpolationScheme1 = colorScheme1;
//		SubstanceColorScheme interpolationScheme2 = useCyclePosAsInterpolation ? colorScheme2 : colorScheme1;
//
//		int w = contour.getBounds().width;
//		int h = contour.getBounds().height;
//
//		if (w == 0 || h == 0) {
//			return;
//		}
//
//		// -------------------------------------------------
//
//		// Fill background
//		if (comp instanceof JProgressBar) {
//			// it paints only the not filled area by the progress
//			graphics.setComposite(AlphaComposite.SrcOver.derive(0.2f));
//			PaintUtils.paintHighlight(graphics, 0, 0, width, height);
//		} else if (comp instanceof JScrollBar) {
//			Point start = new Point(0, 0);
//			Point end = new Point(0, height);
//
//			Color c = colorScheme1.getMidColor();
//			Color[] colors = new Color[] { c, c, c.darker() };
//			float[] fractions = new float[] { 0, 0.6f, 1f };
//			LinearGradientPaint lgp = new LinearGradientPaint(start, end, fractions, colors);
//			graphics.setPaint(lgp);
//			graphics.fillRect(0, 0, width, height);
//		} else {
//			double cycleCoef = 1.0 - cyclePos;
//
//			// ---- compute the colors
//			Color topFillColor;
//			if (comp instanceof AbstractButton && !(comp instanceof AbstractMorphingButton) && w > 50 && h > 50) {
//				Color c = getTopFillColor(interpolationScheme1, interpolationScheme2, cycleCoef,
//						useCyclePosAsInterpolation);
//				Color c2 = SubstanceColorUtilities.getInterpolatedColor(Color.WHITE, c, 0.3);
//				topFillColor = SubstanceColorUtilities.getInterpolatedColor(c2, Color.BLACK, cycleCoef);
//			} else {
//				topFillColor = this.getTopFillColor(interpolationScheme1, interpolationScheme2, cycleCoef,
//						useCyclePosAsInterpolation);
//			}
//			Color midFillColorTop = this.getMidFillColorTop(interpolationScheme1, interpolationScheme2, cycleCoef,
//					useCyclePosAsInterpolation);
//			Color midFillColorBottom = this.getMidFillColorBottom(interpolationScheme1, interpolationScheme2,
//					cycleCoef, useCyclePosAsInterpolation);
//			Color bottomFillColor;
//			if (comp instanceof AbstractButton && !(comp instanceof AbstractMorphingButton) && w > 50 && h > 50) {
//				Color c = getBottomFillColor(interpolationScheme1, interpolationScheme2, cycleCoef,
//						useCyclePosAsInterpolation);
//				Color c2 = SubstanceColorUtilities.getInterpolatedColor(c, Color.BLACK, 0.2);
//				bottomFillColor = SubstanceColorUtilities.getInterpolatedColor(c2, Color.WHITE, cycleCoef);
//			} else {
//				bottomFillColor = this.getBottomFillColor(interpolationScheme1, interpolationScheme2, cycleCoef,
//						useCyclePosAsInterpolation);
//			}
//
//			Color topShineColor = this.getTopShineColor(interpolationScheme1, interpolationScheme2, cycleCoef,
//					useCyclePosAsInterpolation);
//			Color bottomShineColor = this.getBottomShineColor(interpolationScheme1, interpolationScheme2, cycleCoef,
//					useCyclePosAsInterpolation);
//
//			float i1 = 0.08f;
//			float i2 = 0.9f;
//			LinearGradientPaint lgp = new LinearGradientPaint(0, 0, 0, height, new float[] { 0.0f, i1, i2, 1.0f },
//					new Color[] { topFillColor, midFillColorTop, midFillColorBottom, bottomFillColor },
//					MultipleGradientPaint.CycleMethod.NO_CYCLE);
//			graphics.setPaint(lgp);
//			graphics.fill(contour);
//			graphics.clip(contour);
//
//			// shine
//			if (hasShine) {
//				graphics.setComposite(AlphaComposite.SrcOver.derive((float) cycleCoef * 0.25f));
//				addShine(graphics, hasShine, topShineColor, bottomShineColor, width, height, contour, topFillColor,
//						midFillColorTop);
//			}
//		}
//		graphics.dispose();
//	}
}
