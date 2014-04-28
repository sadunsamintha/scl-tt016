package com.sicpa.standard.sasscl.devices.bis;

public interface IScheduleWorker {

	void create();

	void start();

	void stop();

	void dispose();

	void addController(IBisController controller);

}
