package com.sicpa.standard.sasscl.event;

public class ChangePrinterUserLevelEvent {
	
	private int level;

	public ChangePrinterUserLevelEvent(int level) {
		super();
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

}
