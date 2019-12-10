package com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow;

import java.util.EventListener;

public interface SelectionFlowListener extends EventListener {

	void selectionChanged(SelectionFlowEvent evt);

	void selectionComplete(SelectionFlowEvent evt);

	void populateNextComplete(SelectionFlowEvent evt);
	
	void cancelSelection();
	
	void previousSelectionChanged(SelectionFlowEvent evt);
}
