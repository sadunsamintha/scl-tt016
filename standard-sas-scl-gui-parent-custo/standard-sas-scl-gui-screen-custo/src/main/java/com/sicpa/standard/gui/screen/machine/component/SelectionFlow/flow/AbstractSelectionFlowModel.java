package com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;

public abstract class AbstractSelectionFlowModel {

	protected EventListenerList listeners;
	private List<SelectableItem> selections;
	private SelectableItem[] previousSelection;

	public AbstractSelectionFlowModel() {
		this.listeners = new EventListenerList();
		this.selections = new ArrayList<SelectableItem>();
	}

	public abstract boolean isLeaf(SelectableItem[] selections);

	protected abstract void populate(SelectableItem[] selections, List<SelectableItem> nextOptions);

	public abstract String getTitle(SelectableItem[] selection);

	public SelectableItem getSelectedValue(final int index) {
		return this.selections.get(index);
	}

	public SelectableItem[] getSelectedValues() {
		SelectableItem[] res = new SelectableItem[this.selections.size()];
		res = this.selections.toArray(res);
		return res;
	}

	protected void fireSelectionComplete(final SelectableItem[] selection, final boolean oldSelection) {
		Object[] listeners = this.listeners.getListenerList();
		SelectionFlowEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SelectionFlowListener.class) {
				if (e == null) {
					e = new SelectionFlowEvent(selection, "", true, oldSelection);
				}
				((SelectionFlowListener) listeners[i + 1]).selectionComplete(e);
			}
		}
	}

	protected void fireOldSelectionChanged(final SelectableItem[] oldSelection) {
		Object[] listeners = this.listeners.getListenerList();
		SelectionFlowEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SelectionFlowListener.class) {
				if (e == null) {
					e = new SelectionFlowEvent(oldSelection, "", true);
				}
				((SelectionFlowListener) listeners[i + 1]).previousSelectionChanged(e);
			}
		}
	}

	protected void fireSelectionCancel() {
		Object[] listeners = this.listeners.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SelectionFlowListener.class) {
				((SelectionFlowListener) listeners[i + 1]).cancelSelection();
			}
		}
	}

	protected void fireSelectionChanged(final SelectableItem[] selection) {
		Object[] listeners = this.listeners.getListenerList();
		SelectionFlowEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SelectionFlowListener.class) {
				if (e == null) {
					if (isLeaf(getSelectedValues())) {
						e = new SelectionFlowEvent(getSelectedValues(), "", true);
					} else {
						e = new SelectionFlowEvent(selection, getTitle(selection), true);
					}

				}
				((SelectionFlowListener) listeners[i + 1]).selectionChanged(e);
			}
		}
	}

	protected void firePopuplateComplete(final SelectableItem[] items, final boolean movingNext) {
		Object[] listeners = this.listeners.getListenerList();
		SelectionFlowEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SelectionFlowListener.class) {
				if (e == null) {
					e = new SelectionFlowEvent(items, getTitle(getSelectedValues()), movingNext);
				}
				((SelectionFlowListener) listeners[i + 1]).populateNextComplete(e);
			}
		}
	}

	public void addSelectedItems(final SelectableItem item) {
		// =>ignore fast selection of the same item
		if (this.selections.size() != 0) {
			if (this.selections.get(this.selections.size() - 1) == item) {
				return;
			}
		}

		this.selections.add(item);
		fireSelectionChanged(getSelectedValues());
		if (isLeaf(getSelectedValues())) {
			fireSelectionComplete(getSelectedValues(), false);
			this.previousSelection = getSelectedValues();
		} else {
			startPopulating(true);
		}
	}

	private void startPopulating(final boolean movingNext) {
		ArrayList<SelectableItem> items = new ArrayList<SelectableItem>();
		populate(getSelectedValues(), items);
		firePopuplateComplete(items.toArray(new SelectableItem[] {}), movingNext);
	}

	public void init() {
		ArrayList<SelectableItem> items = new ArrayList<SelectableItem>();
		populate(new SelectableItem[0], items);
		firePopuplateComplete(items.toArray(new SelectableItem[] {}), true);
		fireOldSelectionChanged(this.previousSelection);
	}

	public void resetSelections() {
		this.selections.clear();
		fireSelectionChanged(getSelectedValues());
	}

	public void addSelectionFlowListener(final SelectionFlowListener listener) {
		this.listeners.add(SelectionFlowListener.class, listener);
	}

	public void removeSelectionFlowListener(final SelectionFlowListener listener) {
		this.listeners.remove(SelectionFlowListener.class, listener);
	}

	public void removeLastSelection() {
		if (this.selections.size() == 0) {
			fireSelectionCancel();
		} else {
			this.selections.remove(this.selections.size() - 1);
			fireSelectionChanged(getSelectedValues());
			startPopulating(false);
		}
	}

	public SelectableItem[] getPreviousSelection() {
		return this.previousSelection;
	}

	public void setPreviousSelection(final SelectableItem[] previousSelection) {
		this.previousSelection = previousSelection;
		fireOldSelectionChanged(previousSelection);
	}

	public void restorePreviousSelection() {
		this.selections.clear();
		for (SelectableItem item : this.previousSelection) {
			this.selections.add(item);
		}
		fireSelectionComplete(getSelectedValues(), true);
	}

	public void saveSelectedSku(final File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(getSelectedValues());
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void restoreSelectedSku(final File file) {
		if (file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				SelectableItem[] items = (SelectableItem[]) ois.readObject();
				ois.close();
				setPreviousSelection(items);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
