package com.sicpa.standard.gui.screen.machine.component.applicationStatus;

import java.beans.PropertyChangeEvent;
import java.util.EventListener;

public interface ApplicationStatusListener extends EventListener {

	void applicationStatusChanged(PropertyChangeEvent evt);

}
