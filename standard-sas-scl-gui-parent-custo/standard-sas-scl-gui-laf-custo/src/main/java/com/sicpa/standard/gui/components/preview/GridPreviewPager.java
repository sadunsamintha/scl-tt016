package com.sicpa.standard.gui.components.preview;

import java.util.ArrayList;

import net.miginfocom.swing.MigLayout;

public class GridPreviewPager extends PreviewPager {
	public GridPreviewPager() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,wrap 3"));
	}

	@Override
	public void setPreviews(final ArrayList<Preview> previews) {
		super.setPreviews(previews);
		removeAll();
		for (Preview p : previews) {
			add(p, "grow, sizegroup preview");
		}
	}
}
