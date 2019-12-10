package com.sicpa.standard.gui.screen.machine.component.configPassword;

import java.util.EventListener;

@Deprecated
public interface ConfigPasswordListener extends EventListener {

	void canceled();

	void passwordChecked(ConfigPasswordEvent evt);

}
