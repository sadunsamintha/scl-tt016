package com.sicpa.standard.gui.components.preview;

import java.util.ArrayList;

import javax.swing.JPanel;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public abstract class PreviewPager extends JPanel {
	protected ArrayList<Preview> previews;

	public PreviewPager() {
		SicpaLookAndFeelCusto.flagAsWorkArea(this);
	}

	public void setPreviews(final ArrayList<Preview> previews) {
		this.previews = previews;
	}
}
