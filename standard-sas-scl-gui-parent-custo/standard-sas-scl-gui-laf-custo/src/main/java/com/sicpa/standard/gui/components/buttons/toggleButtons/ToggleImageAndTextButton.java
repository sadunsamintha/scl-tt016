package com.sicpa.standard.gui.components.buttons.toggleButtons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.PaintUtils;
import com.sicpa.standard.gui.utils.TextUtils;

public class ToggleImageAndTextButton extends ToggleImageButton {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new MigLayout("fill"));
				AbstractButton b = new ToggleImageAndTextButton("MMMM\nnnnn\nooooo", ImageUtils
						.createRandomStrippedImage(100));
				f.getContentPane().add(b, "grow");
				f.pack();
				f.setVisible(true);
			}
		});
	}

	private String text;

	public ToggleImageAndTextButton(final String text) {
		this(text, null, 0.7f, 0.7f, true);
		setText(text);
	}

	public ToggleImageAndTextButton(final String text, final Image image) {
		this(text, image, 0.7f, 0.7f, true);
	}

	private BufferedImage originalIcon;

	public void setOriginalIcon(final BufferedImage originalIcon) {
		this.originalIcon = originalIcon;
	}

	public BufferedImage getOriginalIcon() {
		return this.originalIcon;
	}
	
	@Override
	public void setText(String text) {
		super.setText(text);
		this.text=text;
	}

	public ToggleImageAndTextButton(final String text, final Image icon, final float alphaMin, final float ratioMin,
			final boolean withSelectionRing) {
		super(icon, alphaMin, ratioMin, withSelectionRing);
		setText(text);
		this.originalIcon = ImageUtils.convertToBufferedImage(icon);
		setSelectionRingColor(SicpaColor.GREEN_DARK);
		this.text = text;

		BufferedImage dummy = GraphicsUtilities.createCompatibleImage(1, 1);
		Graphics g = dummy.createGraphics();

		g.setFont(getFont());
		Rectangle2D textBounds = TextUtils.getMultiLineStringBounds(text, g);
		g.dispose();
		Dimension d;

		if (icon != null) {
			d = new Dimension((int) (this.originalIcon.getWidth() + textBounds.getWidth()) + 60
					+ getSelectionRingThickness() * 2, (int) (Math.max(this.originalIcon.getHeight(),
					textBounds.getHeight())
					+ getSelectionRingThickness() * 2 + 20));
		} else {
			d = new Dimension((int) textBounds.getWidth() + 60 + getSelectionRingThickness() * 2,
					(int) textBounds.getHeight() + 20 + getSelectionRingThickness() * 2);
		}
		setPreferredSize(d);
		setMinimumSize(d);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(final ComponentEvent e) {
				setImage(getLocImage(ToggleImageAndTextButton.this.originalIcon));
			}

			@Override
			public void componentResized(final ComponentEvent e) {
				componentShown(e);
			}
		});
		// TODO find a better way
		addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(final HierarchyEvent e) {
				if (getHeight() > 0 && getWidth() > 0
						&& (e.getChangeFlags() | HierarchyEvent.DISPLAYABILITY_CHANGED) == 3) {
					setImage(getLocImage(ToggleImageAndTextButton.this.originalIcon));
				}
			}
		});
	}

	public void resetImage() {
		setImage(getLocImage(this.originalIcon));
	}

	private BufferedImage getLocImage(final BufferedImage icon) {

		if (getWidth() <= 0 || getHeight() <= 0) {
			return null;
		}

		BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(getWidth(), getHeight());

		Graphics2D g2 = img.createGraphics();
		g2.setFont(getFont());
		PaintUtils.turnOnAntialias(g2);

		final Point start = new Point(0, 0);
		final Point end = new Point(0, img.getHeight());
		final float[] fractions = new float[] { 0, 0.5f, 1 };
		final Color[] colors = new Color[] { SicpaColor.BLUE_LIGHT, SicpaColor.BLUE_LIGHT.darker(),
				SicpaColor.BLUE_LIGHT };

		final LinearGradientPaint lgp = new LinearGradientPaint(start, end, fractions, colors);
		g2.setPaint(lgp);
		g2.fillRoundRect(getSelectionRingThickness() + getBlurRadius() - 2, getSelectionRingThickness()
				+ getBlurRadius() - 2, img.getWidth() - getSelectionRingThickness() * 2 - getBlurRadius() * 2 + 4,
				img.getHeight() - getSelectionRingThickness() * 2 - getBlurRadius() * 2 + 4, 7, 7);

		if (icon != null) {
			g2.drawImage(icon, null, 20, getHeight() / 2 - icon.getHeight() / 2);
		}

		int xText;
		Rectangle textBounds = TextUtils.getMultiLineStringBounds(this.text, g2);

		if (icon == null) {
			xText = getSelectionRingThickness();
		} else {
			xText = getSelectionRingThickness() + 20 + icon.getWidth();
		}
		int textMaxWidth = getWidth() - xText - getSelectionRingThickness() * 2;
		int yText = (int) (getHeight() / 2 - textBounds.getHeight() / 2) - getSelectionRingThickness() / 2;

		PaintUtils.drawMultiLineText(g2, this.text, textMaxWidth, true, xText, yText, Color.BLACK, Color.GRAY);

		if (this.onDarkColor) {
			g2.setColor(Color.WHITE);
		} else {
			g2.setColor(Color.BLACK);
		}
		g2.drawRoundRect(2, 2, img.getWidth() - getBlurRadius() / 2 - 2, img.getHeight() - getBlurRadius() / 2 - 2, 20,
				20);

		g2.drawRoundRect(getSelectionRingThickness() + getBlurRadius() - 2, getSelectionRingThickness()
				+ getBlurRadius() - 2, img.getWidth() - getSelectionRingThickness() * 2 - getBlurRadius() * 2 + 4,
				img.getHeight() - getSelectionRingThickness() * 2 - getBlurRadius() * 2 + 4, 7, 7);
		return img;
	}

	public boolean onDarkColor = false;

}
