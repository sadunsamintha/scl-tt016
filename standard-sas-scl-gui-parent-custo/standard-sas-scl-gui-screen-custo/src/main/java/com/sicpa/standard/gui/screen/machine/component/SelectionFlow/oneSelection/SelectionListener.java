package com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection;

import java.util.EventListener;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectionEvent;

public interface SelectionListener extends EventListener {

	void selectionChanged(SelectionEvent evt);
}
