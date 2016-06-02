package com.sicpa.standard.sasscl.devices.bis.worker;

public class RecognitionResultRequestWorker extends AbstractScheduleWorker {

	public RecognitionResultRequestWorker(long scheduleInterval) {
		super("BIS Recognition Result Request Worker", scheduleInterval);
	}

	@Override
	protected void doWork() {
		controller.sendRecognitionResultRequest();
	}

}
