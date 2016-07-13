package com.sicpa.tt016.devices.camera;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorConfig;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController;
import com.sicpa.tt016.devices.plc.PlcCameraResultParser;
import com.sicpa.tt016.model.PlcCameraResult;
import com.sicpa.tt016.model.event.PlcCameraResultEvent;

import java.nio.ByteBuffer;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTING;

public class TT016CameraSimulatorController extends CameraSimulatorController {

	public TT016CameraSimulatorController() {
	}

	public TT016CameraSimulatorController(CameraSimulatorConfig config) {
		super(config);
	}

	//byte order: last byte of encrypted code, index, decoding time, product status
	private byte[] plcCameraResultBytes = new byte[] {0x00, 0x00, 0x00, 0x00};

	@Override
	public void fireGoodCode(String code) {
		super.fireGoodCode(code);
		sendPlcCameraResult(true);
	}

	@Override
	public void fireBadCode(String code) {
		super.fireBadCode(code);
		sendPlcCameraResult(false);
	}

	@Subscribe
	public void resetPlcCameraResultIndex(ApplicationFlowStateChangedEvent event) {
		if (event.getCurrentState().equals(STT_STARTING)) {
			plcCameraResultBytes[1] = 0x00;
		}
	}

	private void sendPlcCameraResult(boolean valid) {
		plcCameraResultBytes[3] = valid ? (byte) 0x01 : (byte) 0x02;

		PlcCameraResult plcCameraResult = PlcCameraResultParser.getPlcCameraResultEvent(ByteBuffer.wrap
				(plcCameraResultBytes).getInt());

		plcCameraResultBytes[1]++;

		EventBusService.post(new PlcCameraResultEvent(plcCameraResult));
	}
}
