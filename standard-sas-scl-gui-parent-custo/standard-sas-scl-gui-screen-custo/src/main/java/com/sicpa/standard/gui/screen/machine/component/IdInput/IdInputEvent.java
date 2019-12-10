package com.sicpa.standard.gui.screen.machine.component.IdInput;

public class IdInputEvent {

	private String id;
	private String description;
	private String error;

	public IdInputEvent(final String description, final String id) {
		super();
		this.description = description;
		this.id = id;
	}

	public IdInputEvent(final String description, final String id, final String error) {
		super();
		this.description = description;
		this.id = id;
		this.error = error;
	}

	public String getId() {
		return this.id;
	}

	public String getDescription() {
		return this.description;
	}

	public String getError() {
		return this.error;
	}
}
