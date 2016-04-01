package com.sicpa.standard.sasscl.devices.remote.lifecheck;


public interface IMasterLifeCheckWorker {

	void start();

	void stop();

	void scheduleReconnection();

}