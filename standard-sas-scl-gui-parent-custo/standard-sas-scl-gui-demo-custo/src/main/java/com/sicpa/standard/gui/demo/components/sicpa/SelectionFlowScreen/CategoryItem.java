package com.sicpa.standard.gui.demo.components.sicpa.SelectionFlowScreen;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.DefaultSelectableItem;

class CategoryItem extends DefaultSelectableItem {

	static enum Category {
		SODA, WINE, TOBACCO, SPIRIT, BEER
	};

	private Category category;

	public CategoryItem(final int index, final String text, final Category category) {
		super(index, text);
		this.category = category;
	}

	@Override
	public String getFormatedTextForSummary() {
		return "CATEGORY:" + getText();
	}

	public Category getCategory() {
		return this.category;
	}

	@Override
	public String toString() {
		return getText();
	}
}
