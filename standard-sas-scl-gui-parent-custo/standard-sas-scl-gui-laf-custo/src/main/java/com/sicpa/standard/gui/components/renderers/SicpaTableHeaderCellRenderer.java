package com.sicpa.standard.gui.components.renderers;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableHeaderCellRenderer;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;

import com.sicpa.standard.gui.plaf.SicpaSkin;

public class SicpaTableHeaderCellRenderer extends SubstanceDefaultTableHeaderCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
			final int column)
	{
		JLabel res = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		SubstanceColorScheme cs = SubstanceColorSchemeUtilities.getColorScheme(table,SicpaSkin.TABLE_DECORATION_AREA_TYPE, ComponentState.DEFAULT);

		res.setForeground(cs.getForegroundColor());

		// replace the default sort icon of substance
		// because it's black and we need white
		Icon i = res.getIcon();
		if (i != null)
		{
			BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(i.getIconWidth(), i.getIconHeight());
			Graphics2D g2 = img.createGraphics();
			i.paintIcon(res, g2, 0, 0);
			g2.setComposite(AlphaComposite.SrcAtop);

			g2.setColor(res.getForeground());

			g2.fillRect(0, 0, i.getIconWidth(), i.getIconHeight());
			g2.dispose();
			res.setIcon(new ImageIcon(img));
		}
		return res;
	}
}
