package com.sicpa.standard.sasscl.devices.bis;

import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.bis.controller.BisRemoteServer;
import com.sicpa.standard.sasscl.devices.bis.model.BisModel;

public class BisAdapterTest {

	public static void main(String[] arg) throws InterruptedException, DeviceException {

		IBisModel model = new BisModel();
		model.setAddress("localhost");
		model.setPort(8020);
		model.setConnectionLifeCheckInterval(1000);
		model.setRecognitionResultRequestInterval(1000);
		model.setUnknownSkuThreshold(5);

		BisAdapter bisAdapter = new BisAdapter();
		bisAdapter.setController(new BisRemoteServer(model));
		bisAdapter.connect();

		Thread.sleep(3000);

		bisAdapter.disconnect();

		Thread.sleep(5000);

		bisAdapter.connect();

		Thread.sleep(5000);

		bisAdapter.disconnect();

		// bisAdapter.start();
		// Thread.sleep(10000);
		// bisAdapter.disconnect();

	}

}
