package com.sicpa.standard.gui.screen.machine.component.emergency;

import java.beans.PropertyChangeEvent;
import java.util.EventListener;

@Deprecated
public interface EmergencyChangeListener extends EventListener {

	void emergencyTextChanged(PropertyChangeEvent evt);
	void emergencyVisibilityChanged(PropertyChangeEvent evt);
	
}
