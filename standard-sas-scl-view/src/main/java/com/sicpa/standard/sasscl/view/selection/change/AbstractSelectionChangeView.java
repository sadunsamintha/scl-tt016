package com.sicpa.standard.sasscl.view.selection.change;

import com.sicpa.standard.client.common.view.mvc.AbstractView;

@SuppressWarnings("serial")
public abstract class AbstractSelectionChangeView extends
		AbstractView<ISelectionChangeViewListener, SelectionChangeViewModel> {

	protected void fireChangeContext() {
		synchronized (listeners) {
			for (ISelectionChangeViewListener l : listeners) {
				l.changeContext();
			}
		}
	}

}
