package com.sicpa.standard.gui.screen.machine.component.IdInput;

import java.util.EventListener;

public interface IdInputListener extends EventListener {

	void idChanged(IdInputEvent evt);

	void descriptionChanged(IdInputEvent evt);

	void complete(IdInputEvent evt);

	void error(IdInputEvent evt);

}
