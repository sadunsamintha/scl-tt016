package com.sicpa.standard.gui.demo.components.sicpa.SelectionFlowScreen;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.DefaultSelectableItem;

class QtyLabelItem extends DefaultSelectableItem {

	private int labelQty;

	public QtyLabelItem(final int index, final String text, final int labelQty) {
		super(index, text);
		this.labelQty = labelQty;
	}

	@Override
	public String getFormatedTextForSummary() {
		return "LABEL QUANTITY:" + getText();
	}

	public int getLabelQty() {
		return this.labelQty;
	}

	@Override
	public String toString() {
		return getText();
	}
}
