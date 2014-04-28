package com.sicpa.standard.sasscl.devices.bis;


public class BisEvent {
	
	private Object value;
	private Object source;
	
	public BisEvent(){
		
	}
	
	public BisEvent(String value, Object source) {
		this.value = value;
		this.source = source;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

}
