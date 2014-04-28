package com.sicpa.standard.sasscl.view.option;

import com.sicpa.standard.client.common.view.mvc.AbstractView;

@SuppressWarnings("serial")
public abstract class AbstractOptionsView extends AbstractView<IOptionsViewListeners, OptionsModel> {

	protected void fireDisplayOptions() {
		for (IOptionsViewListeners l : listeners) {
			l.displayOptions();
		}
	}
}
