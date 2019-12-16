package com.sicpa.standard.gui.utils;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.plaf.SicpaFont;

public class TextUtils {
	/**
	 * return the bounds of a text painted on multiple line, with \n as a delimiter
	 * 
	 * @param text
	 * @param g
	 *            the graphics where the text is being painted
	 * @return the bounds of the text
	 */
	public static Rectangle getMultiLineStringBounds(final String text, final Graphics g) {
		Rectangle rect = new Rectangle();
		if (g == null) {
			throw new IllegalArgumentException("the graphics cannot be null");
		}

		if (text != null && !text.isEmpty()) {
			int h = 0;
			int w = 0;
			FontMetrics fm = g.getFontMetrics();
			Rectangle textBounds;
			for (String line : text.split("\n")) {
				textBounds = fm.getStringBounds(line, g).getBounds();
				w = Math.max(w, textBounds.width);
				h += textBounds.height;
			}
			rect.height = h;
			rect.width = w;
		}
		return rect;
	}

	/**
	 * transform the text of the label into html in order to make the width of the label smaller than the given width
	 * 
	 * @param label
	 *            the label to transform
	 * @param width
	 *            the max width of the label
	 */
	public static void wrapHTMLLabel(final JLabel label, final int width) {
		if (label == null || label.getText().length() == 0) {
			return;
		}
		label.setText(getWrappedHTLMText(label.getText(), width, label.getFont()));
	}

	/**
	 * return the text transformed into html to meet the max width
	 * 
	 * @param text
	 *            the text to transform
	 * @param width
	 *            the max width wished
	 * @param font
	 *            the font of the text to compute it width
	 * @return the html text
	 */
	public static String getWrappedHTLMText(final String text, final int width, final Font font) {
		if (text == null || text.length() == 0) {
			return text;
		}

		if (text.trim().toUpperCase().startsWith("<HTML>")) {
			throw new IllegalArgumentException("the text cannot be already html");
		}

		BufferedImage b = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = b.getGraphics();
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		StringBuffer res = new StringBuffer("<html>");
		String[] split = text.split(" ");
		boolean modified = false;
		int remaining = width;
		for (String s : split) {
			int wordSize = SwingUtilities.computeStringWidth(fm, s);
			if (remaining - wordSize >= 0) {
				res.append(s).append(" ");
				remaining -= wordSize;
			} else {
				res.append("<br>").append(s).append(" ");
				remaining = width - wordSize;
				modified = true;
			}
		}
		g.dispose();

		return modified ? res.toString() : text;
	}

	/**
	 * Make the label width to always samller than the given width by decrease the font size
	 * 
	 * @param label
	 * @param width
	 *            the max width
	 */
	public static void registerLabelForOptimumFont(final JLabel label, final int width) {
		label.addPropertyChangeListener("text", new PropertyChangeListener() {
			private final Font defaultFont = label.getFont();

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				label.setFont(getOptimumFont(label.getText(), width, this.defaultFont));
			}
		});
		label.setFont(getOptimumFont(label.getText(), width, label.getFont()));
	}

	/**
	 * Compute and return the font that match the given width if the text with the default font is larger than the given
	 * width
	 * 
	 * @param text
	 *            the text
	 * @param width
	 *            the maximum width of the text
	 * @param font
	 *            the default font
	 * @return the font that make the text to have a smaller width than the given width
	 */
	public static Font getOptimumFont(final String text, final int width, final Font font) {
		if (text == null || text.length() == 0) {
			return font;
		}
		String line;
		String up = text.toUpperCase();
		if (up.startsWith("<HTML>")) {
			int i = up.indexOf("<BR>");
			if (i == -1) {
				i = up.indexOf("<BR/>");
			}

			if (i == -1) {
				line = up.substring("<HTML>".length());
			} else {
				line = up.substring("<HTML>".length(), i);
			}
		} else {
			line = text;
		}

		BufferedImage b = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = b.getGraphics();

		boolean notfound = true;

		int fontSize = font.getSize();
		Font optimalFont = font.deriveFont((float) fontSize);
		boolean first = true;

		while (notfound) {
			if (fontSize == 1) {
				optimalFont = optimalFont.deriveFont((float) 1);
				notfound = false;
			} else {
				if (!first) {
					fontSize--;
				} else {
					first = false;
				}
				optimalFont = font.deriveFont((float) fontSize);
				g.setFont(optimalFont);

				int lineSize = SwingUtilities.computeStringWidth(g.getFontMetrics(), line);
				if (lineSize <= width) {
					notfound = false;
				}
			}
		}
		g.dispose();
		return optimalFont;
	}

	public static void main(String[] args) {
		String res = getWrappedLFText(".stComm.stPar.nPaperSpeed ( 4 ) must be >= 5 and <=60  ", 200,
				SicpaFont.getFont(15));
		System.out.println(res);
	}

	/**
	 * return the text transformed with added \n to meet the max width
	 * 
	 * @param text
	 *            the text to transform
	 * @param width
	 *            the max width wished
	 * @param font
	 *            the font of the text to compute it width
	 * @return the html text
	 */
	public static String getWrappedLFText(final String text, final int width, final Font font) {
		if (text == null || text.length() == 0 || width <= 0) {
			return text;
		}
		String res = "";
		String[] lines = text.split("\n");
		if (lines.length > 1) {

			for (String aLine : lines) {
				res += getWrappedLFText(aLine, width, font) + '\n';
			}
			return res;
		} else {

			BufferedImage b = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
			Graphics g = b.getGraphics();
			g.setFont(font);

			FontMetrics fm = g.getFontMetrics();

			String[] split = text.split(" ");
			int remaining = width;
			boolean oneWordAdded = false;
			for (int i = 0; i < split.length; i++) {
				String s;
				if (i == 0) {
					s = split[i];
				} else {
					s = " " + split[i];
				}
				if (s.isEmpty()) {
					continue;
				}

				int wordSize = SwingUtilities.computeStringWidth(fm, s);

				int delta = remaining - wordSize;
				if (delta >= 5) {
					res += s;
					remaining -= wordSize;
					oneWordAdded = true;
				} else {
					if (oneWordAdded) {
						res += '\n' + getWrappedLFText(s, width, font);
						remaining = width - wordSize;
					} else {// if the word is bigger than the length available split it in 2
						res += getWrappedLFText(s.substring(0, s.length() / 2) + "\n" + s.substring(s.length() / 2),
								width, font);
						remaining = width - wordSize / 2;
					}
				}
			}
			g.dispose();
			return res.replace("\n\n", "\n");
		}
	}

	public static String objectToString(final Object value) {
		if (value == null) {
			return null;
		} else {
			return value.toString();
		}
	}
}
