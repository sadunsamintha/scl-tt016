package com.sicpa.standard.gui.screen.machine.component.infoHeader;

public class HeaderLabel {

	private String key;
	private String text;

	public String getKey() {
		return this.key;
	}

	public String getText() {
		return this.text;
	}

	public HeaderLabel(final String key, final String text) {
		this.key = key;
		this.text = text;
	}
}
