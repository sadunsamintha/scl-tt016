package com.sicpa.standard.sasscl.controller.scheduling;


public class SchedulerConfigSCL extends SchedulerConfig {

	public SchedulerConfigSCL() {
		super();
	}

	public SchedulerConfigSCL(String file) {
		super(file);
	}

	// get encoders from remote server timer
	protected int getEncodersTimer_sec;

	protected void setDefaultValues() {
		super.setDefaultValues();
		setGetEncodersTimer_sec(300);// 5 min
	}

	public int getGetEncodersTimer_sec() {
		return getEncodersTimer_sec;
	}

	public void setGetEncodersTimer_sec(int getEncodersTimer_sec) {
		this.getEncodersTimer_sec = getEncodersTimer_sec;
	}
}
