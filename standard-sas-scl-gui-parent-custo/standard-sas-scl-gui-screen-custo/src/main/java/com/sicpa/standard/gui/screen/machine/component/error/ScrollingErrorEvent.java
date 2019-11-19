package com.sicpa.standard.gui.screen.machine.component.error;
@Deprecated
public class ScrollingErrorEvent {
	private ErrorItem item;

	public ErrorItem getItem() {
		return this.item;
	}

	public ScrollingErrorEvent(final ErrorItem item) {
		super();
		this.item = item;
	}
}
