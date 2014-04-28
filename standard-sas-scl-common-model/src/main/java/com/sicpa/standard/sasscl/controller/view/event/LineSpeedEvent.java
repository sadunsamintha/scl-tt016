package com.sicpa.standard.sasscl.controller.view.event;

/**
 * 
 * event used to notify the gui to change the line speed
 * 
 * @author YYang
 * 
 */
public class LineSpeedEvent {

	protected final String line;

	protected final String speed;

	public LineSpeedEvent(String line, String speed) {
		this.speed = speed;
		this.line = line;
	}

	public String getSpeed() {
		return speed;
	}

	public String getLine() {
		return line;
	}

}
