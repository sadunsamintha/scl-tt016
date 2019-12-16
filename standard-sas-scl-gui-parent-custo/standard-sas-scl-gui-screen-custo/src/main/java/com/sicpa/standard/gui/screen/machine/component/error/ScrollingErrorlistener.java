package com.sicpa.standard.gui.screen.machine.component.error;

import java.util.EventListener;
@Deprecated
public interface ScrollingErrorlistener extends EventListener {

	void errorAdded(ScrollingErrorEvent evt);

	void errorRemoved(ScrollingErrorEvent evt);

}
