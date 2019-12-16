package com.sicpa.standard.gui.screen.machine.component.SelectionFlow;

import java.awt.Image;

import javax.swing.ImageIcon;

public class DefaultSelectableItem implements SelectableItem {
	private static final long serialVersionUID = 1L;
	private int index;
	private String text;
	private ImageIcon image;

	public DefaultSelectableItem(final int index, final String text) {
		this(index, text, null);
	}

	public DefaultSelectableItem(final int index, final String text, final Image image) {
		this.index = index;
		this.text = text;
		if (image != null) {
			this.image = new ImageIcon(image);
		} else {
			this.image = new ImageIcon();
		}

	}

	@Override
	public int getId() {
		return this.index;
	}

	@Override
	public ImageIcon getImage() {
		return this.image;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public String toString() {
		return this.index + " " + this.text;
	}

	@Override
	public String getFormatedTextForSummary() {
		return this.text;
	}

	public boolean isShownOnSummary() {
		return true;
	}

	public void setText(final String text) {
		this.text = text;
	}
}
