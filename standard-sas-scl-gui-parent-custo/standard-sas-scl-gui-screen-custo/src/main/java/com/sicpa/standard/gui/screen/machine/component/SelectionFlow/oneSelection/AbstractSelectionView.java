package com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection;

import javax.swing.JPanel;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectionEvent;

public abstract class AbstractSelectionView extends JPanel {

	public void addSelectionListener(final SelectionListener listener) {
		this.listenerList.add(SelectionListener.class, listener);
	}

	public void removeSelectionListener(final SelectionListener listener) {
		this.listenerList.remove(SelectionListener.class, listener);
	}

	protected void fireSelectionChanged(final SelectableItem item) {
		Object[] listeners = this.listenerList.getListenerList();
		SelectionEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SelectionListener.class) {
				if (e == null) {
					e = new SelectionEvent(item);
				}
				((SelectionListener) listeners[i + 1]).selectionChanged(e);
			}
		}
	}

	public void beforePreviewInit() {
	}
}
