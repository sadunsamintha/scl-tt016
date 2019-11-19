package com.sicpa.standard.gui.components.renderers;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;

import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;

import com.sicpa.standard.gui.plaf.SicpaSkin;

public class CellRenderersUtils {
	public static void applyStripping(final Component comp, final JTable table, final int row, final boolean isSelected) {
		SubstanceColorScheme cs;
		if (table.isEnabled()) {
			cs = SubstanceColorSchemeUtilities.getColorScheme(null, SicpaSkin.TABLE_DECORATION_AREA_TYPE,
					ComponentState.DEFAULT);
		} else {
			cs = SubstanceColorSchemeUtilities.getColorScheme(null, SicpaSkin.TABLE_DECORATION_AREA_TYPE,
					ComponentState.DISABLED_DEFAULT);
		}
		if (isSelected) {
			comp.setForeground(cs.getExtraLightColor());
		} else {
			comp.setForeground(cs.getMidColor());
		}
		comp.setFont(table.getFont());

		if (row % 2 == 0) {
			comp.setBackground(cs.getLightColor());
		} else {
			comp.setBackground(cs.getDarkColor());
		}
	}

	public static void prepareRenderer(final Component comp, final JList list, final int row, final boolean isSelected) {
		SubstanceColorScheme cs;
		if (list.isEnabled()) {
			cs = SubstanceColorSchemeUtilities.getColorScheme(null, SicpaSkin.TABLE_DECORATION_AREA_TYPE,
					ComponentState.DEFAULT);
		} else {
			cs = SubstanceColorSchemeUtilities.getColorScheme(null, SicpaSkin.TABLE_DECORATION_AREA_TYPE,
					ComponentState.DISABLED_DEFAULT);
		}
		if (isSelected) {
			comp.setForeground(cs.getExtraLightColor());
		} else {
			comp.setForeground(cs.getMidColor());
		}
		comp.setFont(list.getFont());

		if (row % 2 == 0) {
			comp.setBackground(cs.getLightColor());
		} else {
			comp.setBackground(cs.getDarkColor());
		}
	}

	public static void prepareRendererForCheckBox(final Component comp, final JTable table, final int row,
			final boolean isSelected) {
		SubstanceColorScheme cs;
		if (table.isEnabled()) {
			cs = SubstanceColorSchemeUtilities.getColorScheme(null, SicpaSkin.TABLE_DECORATION_AREA_TYPE,
					ComponentState.DEFAULT);
		} else {
			cs = SubstanceColorSchemeUtilities.getColorScheme(null, SicpaSkin.TABLE_DECORATION_AREA_TYPE,
					ComponentState.DISABLED_DEFAULT);
		}
		comp.setFont(table.getFont());
		comp.setForeground(cs.getMidColor());
		if (row % 2 == 0) {
			comp.setBackground(cs.getLightColor());
		} else {
			comp.setBackground(cs.getDarkColor());
		}
	}

	public static void prepareRenderer(final Component comp, final JTree tree, final int row, final boolean isSelected) {
		SubstanceColorScheme cs;
		if (tree.isEnabled()) {
			cs = SubstanceColorSchemeUtilities.getColorScheme(null, SicpaSkin.TABLE_DECORATION_AREA_TYPE,
					ComponentState.DEFAULT);
		} else {
			cs = SubstanceColorSchemeUtilities.getColorScheme(null, SicpaSkin.TABLE_DECORATION_AREA_TYPE,
					ComponentState.DISABLED_DEFAULT);
		}

		if (isSelected) {
			comp.setForeground(cs.getExtraLightColor());
		} else {
			comp.setForeground(cs.getMidColor());
		}
		comp.setFont(tree.getFont());
		if (row % 2 == 0) {
			comp.setBackground(cs.getLightColor());
		} else {
			comp.setBackground(cs.getDarkColor());
		}
	}
}
