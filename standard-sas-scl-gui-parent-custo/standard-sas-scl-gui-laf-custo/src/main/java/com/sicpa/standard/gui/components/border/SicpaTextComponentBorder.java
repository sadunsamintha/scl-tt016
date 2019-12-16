package com.sicpa.standard.gui.components.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.utils.HashMapKey;
import org.pushingpixels.substance.internal.utils.LazyResettableHashMap;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceImageCreator;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class SicpaTextComponentBorder implements Border, UIResource {
	/**
	 * Cache of small border images.
	 */
	private static LazyResettableHashMap<BufferedImage> smallImageCache = new LazyResettableHashMap<BufferedImage>(
			"SubstanceTextComponentBorder");
	/**
	 * Insets of <code>this</code> border.
	 */
	protected Insets myInsets;

	/**
	 * Creates a new border with the specified insets.
	 * 
	 * @param insets
	 *            Insets.
	 */
	public SicpaTextComponentBorder(final Insets insets) {
		this.myInsets = new Insets(insets.top, insets.left, insets.bottom, insets.right);
	}

	/**
	 * Paints border instance for the specified component.
	 * 
	 * @param c
	 *            The component.
	 * @param g
	 *            Graphics context.
	 * @param x
	 *            Component left X (in graphics context).
	 * @param y
	 *            Component top Y (in graphics context).
	 * @param width
	 *            Component width.
	 * @param height
	 *            Component height.
	 * @param isEnabled
	 *            Component enabled status.
	 * @param hasFocus
	 *            Component focus ownership status.
	 * @param alpha
	 *            Alpha value.
	 */
	private void paintBorder(final JComponent c, final Graphics g, final int x, final int y, final int width,
			final int height, final boolean isEnabled, final boolean hasFocus) {
		// failsafe for LAF change
		if (!SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			return;
		}

		if ((width <= 0) || (height <= 0))
			return;

		Graphics2D graphics = (Graphics2D) g.create();

		float cyclePos = 1.0f;
		float radius = 2.0f * SubstanceSizeUtils.getClassicButtonCornerRadius(SubstanceSizeUtils
				.getComponentFontSize(c));

		SubstanceColorScheme borderColorScheme;

		if (c.isEnabled()) {
			borderColorScheme = SubstanceColorSchemeUtilities.getColorScheme(c, ColorSchemeAssociationKind.BORDER,
					ComponentState.DEFAULT);
		} else {
			borderColorScheme = SubstanceColorSchemeUtilities.getColorScheme(c, ColorSchemeAssociationKind.BORDER,
					ComponentState.DISABLED_DEFAULT);
		}

		boolean editable = false;
		if (c instanceof JTextComponent) {
			editable = ((JTextComponent) c).isEditable();
		} else if (c instanceof JComboBox) {
			editable = ((JComboBox) c).isEditable();
		}

		if (width * height < 100000) {
			HashMapKey hashKey = SubstanceCoreUtilities.getHashKey(

			SubstanceSizeUtils.getComponentFontSize(c), width, height, radius,

			borderColorScheme.getDisplayName(),

			cyclePos,

			SubstanceLookAndFeel.getDecorationType(c) + "",

			c.isEnabled(),

			editable

			);

			if (!smallImageCache.containsKey(hashKey)) {
				BufferedImage toCache = SubstanceCoreUtilities.getBlankImage(width, height);
				Graphics2D g2d = toCache.createGraphics();
				SubstanceImageCreator.paintTextComponentBorder(c, g2d, 0, 0, width, height, radius, borderColorScheme);
				g2d.dispose();
				smallImageCache.put(hashKey, toCache);
			} else {
				// System.out.println(true);
			}
			graphics.drawImage(smallImageCache.get(hashKey), x, y, null);
		} else {
			// for borders larger than 100000 pixels, use simple
			// painting

			graphics.translate(x, y);
			SubstanceImageCreator.paintSimpleBorder(c, graphics, width, height, borderColorScheme);
		}

		graphics.dispose();
	}

	public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width,
			final int height) {
		paintBorder((JComponent) c, g, x, y, width, height, c.isEnabled(), c.hasFocus());
	}

	public Insets getBorderInsets(final Component c) {
		return this.myInsets;
	}

	public boolean isBorderOpaque() {
		return false;
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new MigLayout("wrap 1"));
				f.getContentPane().add(new JTextField(25), "");
				f.getContentPane().add(new JTextField(20), "");
				f.getContentPane().add(new JTextField(15), "");
				f.getContentPane().add(new JTextField(10), "");
				f.pack();
				f.setVisible(true);
			}
		});
	}
}
