package com.sicpa.standard.sasscl.devices.camera.alert;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.alert.impl.Alert;
import com.sicpa.standard.sasscl.business.alert.task.model.CameraDuplicatedAlertTaskModel;
import com.sicpa.standard.sasscl.devices.camera.CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.ICameraAdaptor;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class DuplicatedCodeTest {

	MessageEvent message;
	DuplicatedCodeAlertTask task;
	int count = 0;

	@Test
	public void alertTaskDuplicatedCodeTest() throws InterruptedException {
		this.task = new DuplicatedCodeAlertTask() {
		};
		Alert alert = new Alert();
		alert.addTask(this.task);
		alert.start();
		ProductionParameters productionParameters = new ProductionParameters();
		productionParameters.setProductionMode(ProductionMode.STANDARD);

		CameraDuplicatedAlertTaskModel model = new CameraDuplicatedAlertTaskModel();
		model.setEnabled(true);
		model.setThreshold(10);

		task.setProductionParameters(productionParameters);
		this.task.setModel(model);

		EventBusService.register(new Object() {
			@SuppressWarnings("unused")
			@Subscribe
			public void notifyMessage(final MessageEvent evt) {
				// get message after threshold is reached
				if (evt.getKey() != null) {
					Assert.assertEquals(9, count);
					Assert.assertEquals(MessageEventKey.Alert.DUPLICATED_CODE, evt.getKey());
				}
			}
		});

		// receive same camera code
		for (int i = 1; i < 15; i++) {
			this.task.receiveCameraCode(new CameraGoodCodeEvent(new Code("1001001001"), Mockito
					.mock(ICameraAdaptor.class)));
			this.count = i;
			// stop the loop when the message event is triggered
			if (model.getThreshold() == i) {
				break;
			}
		}

		alert.stop();
	}

}
