package com.sicpa.standard.gui.screen.machine.component.configPassword;

@Deprecated
public class ConfigPasswordEvent {

	private boolean valid;

	public boolean isValid() {
		return this.valid;
	}

	public ConfigPasswordEvent(final boolean isValid) {
		this.valid = isValid;
	}

}
