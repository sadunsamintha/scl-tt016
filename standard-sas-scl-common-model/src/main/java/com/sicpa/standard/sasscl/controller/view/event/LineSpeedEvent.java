package com.sicpa.standard.sasscl.controller.view.event;

/**
 * 
 * event used to notify the gui to change the line speed
 * 
 * @author YYang
 * 
 */
public class LineSpeedEvent {

	private final int line;

	private final String speed;

	public LineSpeedEvent(int line, String speed) {
		this.speed = speed;
		this.line = line;
	}

	public String getSpeed() {
		return speed;
	}

	public int getLine() {
		return line;
	}

}
