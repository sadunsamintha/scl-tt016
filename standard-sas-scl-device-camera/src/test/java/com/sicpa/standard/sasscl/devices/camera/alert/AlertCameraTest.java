package com.sicpa.standard.sasscl.devices.camera.alert;

import static com.sicpa.standard.sasscl.model.ProductionMode.MAINTENANCE;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.alert.impl.Alert;
import com.sicpa.standard.sasscl.business.alert.task.IAlertTask;
import com.sicpa.standard.sasscl.business.alert.task.model.CameraCountAlertTaskModel;
import com.sicpa.standard.sasscl.business.alert.task.scheduled.AbstractScheduledOverTimeAlertTask;
import com.sicpa.standard.sasscl.devices.camera.CameraBadCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.ICameraAdaptor;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class AlertCameraTest {

	int count = 0;
	private Timer timer;
	private AtomicReference<TimerTask> timerTaskReference;

	public class IncrementingMessageListener {
		@Subscribe
		public void notifyMessage(MessageEvent evt) {
			count++;
		}
	}

	@Before
	public void setUp() throws Exception {

		timer = mock(Timer.class);
		timerTaskReference = new AtomicReference<TimerTask>();

		doAnswer(new Answer<Object>() {

			public Object answer(InvocationOnMock invocation) {

				timerTaskReference.compareAndSet(null, (TimerTask) invocation.getArguments()[0]);

				return null;
			}
		}).when(timer).scheduleAtFixedRate(Matchers.<TimerTask> anyObject(), anyLong(), anyLong());

	}

	@Test
	public void alertTaskGoodBadTest() {

		ICameraAdaptor cameraAdaptor = Mockito.mock(ICameraAdaptor.class);
		Mockito.when(cameraAdaptor.getName()).thenReturn("camera-1");

		CameraCountAlertTask cca = new CameraCountAlertTask();
		CameraCountAlertTaskModel model = new CameraCountAlertTaskModel();
		model.setSampleSize(10);
		model.setMaxUnreadCount(5);
		model.setDelayInSec(1);
		cca.setModel(model);

		ProductionParameters productionParameters = new ProductionParameters();
		productionParameters.setProductionMode(ProductionMode.STANDARD);
		cca.setProductionParameters(productionParameters);

		EventBusService.register(new IncrementingMessageListener());

		cca.setTimer(timer);

		Alert alert = new Alert(new IAlertTask[] { cca });
		alert.start();

		// bad code
		for (int i = 0; i < 15; i++) {
			cca.receiveCameraCodeError(new CameraBadCodeEvent(new Code("E"), cameraAdaptor));
		}

		executeScheduledTask();

		Assert.assertTrue(count > 0);

		alert.stop();

		assertThat(cca.getSampleSize(), equalTo(10));
		assertThat(cca.getThreshold(), equalTo(5));
	}

	private void executeScheduledTask() {

		timerTaskReference.get().run();
	}

	@Test
	public void alertTaskOverTimeTest() {

		AbstractScheduledOverTimeAlertTask task = new AbstractScheduledOverTimeAlertTask() {

			@Override
			protected MessageEvent getAlertMessage() {

				return new MessageEvent("alert camera key");
			}

			@Override
			public String getAlertName() {

				return null;
			}

			@Override
			protected int getThreshold() {
				return 03;
			}

			@Override
			protected int getSampleSize() {
				return 5;
			}

			@Override
			public long getDelay() {
				return 1000;
			}

			@Override
			protected boolean isEnabled() {
				return true;
			}
		};
		EventBusService.register(new IncrementingMessageListener());
		task.setTimer(timer);
		task.start();

		// bad code
		for (int i = 0; i < 5; i++) {
			task.increment();
			executeScheduledTask();
		}

		executeScheduledTask();

		assertThat(count, equalTo(3));
	}

	@Test
	public void alertTaskGoodBadInMaintenanceModeTest() {

		ICameraAdaptor cameraAdaptor = Mockito.mock(ICameraAdaptor.class);
		Mockito.when(cameraAdaptor.getName()).thenReturn("camera-1");

		ProductionParameters productionParameters = new ProductionParameters();
		productionParameters.setProductionMode(MAINTENANCE);

		CameraCountAlertTask cca = new CameraCountAlertTask();
		CameraCountAlertTaskModel model = new CameraCountAlertTaskModel();
		model.setSampleSize(10);
		model.setMaxUnreadCount(10);
		model.setDelayInSec(1);
		cca.setModel(model);

		cca.setProductionParameters(productionParameters);
		EventBusService.register(new IncrementingMessageListener());
		cca.setTimer(timer);

		Alert alert = new Alert(new IAlertTask[] { cca });
		alert.start();

		// bad code
		for (int i = 0; i < 15; i++) {
			cca.receiveCameraCodeError(new CameraBadCodeEvent(new Code("E"), cameraAdaptor));
		}

		for (int i = 0; i < 5; i++)
			executeScheduledTask();

		// no camera alert in export or in maintenance
		assertThat(count, equalTo(0));
	}
}
