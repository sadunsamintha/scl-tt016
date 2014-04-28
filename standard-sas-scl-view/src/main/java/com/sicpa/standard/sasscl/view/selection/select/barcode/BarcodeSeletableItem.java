package com.sicpa.standard.sasscl.view.selection.select.barcode;

import javax.swing.ImageIcon;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;

public class BarcodeSeletableItem implements SelectableItem {


	private static final long serialVersionUID = 1L;
	protected String barcode;

	public BarcodeSeletableItem(final String barcode) {
		super();
		this.barcode = barcode;
	}

	@Override
	public boolean isShownOnSummary() {
		return true;
	}

	@Override
	public String getText() {
		return this.barcode;
	}

	@Override
	public ImageIcon getImage() {
		return null;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public String getFormatedTextForSummary() {
		return "Barcode:" + this.barcode;
	}

}
