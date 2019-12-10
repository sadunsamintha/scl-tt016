package com.sicpa.standard.gui.components.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTreeCellRenderer;

public class SicpaTreeCellRenderer extends SubstanceDefaultTreeCellRenderer
{
	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf,
			final int row, final boolean hasFocus)
	{
		JLabel res = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		CellRenderersUtils.prepareRenderer(res,tree, row, sel);
		return res;
	}
}
