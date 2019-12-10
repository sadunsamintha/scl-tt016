package com.sicpa.standard.gui.screen.machine.component.confirmation;

import java.beans.PropertyChangeEvent;
import java.util.EventListener;

public interface ConfirmationChangeListener extends EventListener {

	void questionTextChanged(PropertyChangeEvent evt);

	void confirmButtonTextChanged(PropertyChangeEvent evt);

	void cancelButtonTextChanged(PropertyChangeEvent evt);

	void cancelButtonVisibilityChanged(PropertyChangeEvent evt);

	void confirmButtonEnabilityChanged(PropertyChangeEvent evt);

	void askQuestion(PropertyChangeEvent evt);

	void abort();
}
