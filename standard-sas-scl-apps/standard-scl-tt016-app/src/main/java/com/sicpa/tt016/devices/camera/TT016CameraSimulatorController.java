package com.sicpa.tt016.devices.camera;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraConstants;
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
	private byte[] plcCameraResultBytes = new byte[]{0x00, 0x00, 0x00, 0x00};
	//ASCII character 'x' is sent from PLC when camera couldn't read any code - 'x' has a hex value of 78
	private static byte NO_CODE_HEX = (byte) 0x78;

	@Override
	public void fireGoodCode(String code) {
		super.fireGoodCode(code);

		if (!isCounting()) {
			sendPlcCameraResult(code, true);
		}
	}

	@Override
	public void fireBadCode(String code) {
		super.fireBadCode(code);

		if (!isCounting()) {
			sendPlcCameraResult(code, false);
		}
	}
	
	@Subscribe
	public void resetPlcCameraResultIndex(ApplicationFlowStateChangedEvent event) {
		if (event.getCurrentState().equals(STT_STARTING)) {
			plcCameraResultBytes[1] = 0x00;
		}
	}

	private void sendPlcCameraResult(String code, boolean valid) {
		plcCameraResultBytes[0] = getLastByteOfEncryptedCode(code, valid);
		plcCameraResultBytes[1]++;
		plcCameraResultBytes[3] = getProductStatusByte(code);

		PlcCameraResult plcCameraResult = PlcCameraResultParser.getPlcCameraResultEvent(ByteBuffer.wrap
				(plcCameraResultBytes).getInt());

		EventBusService.post(new PlcCameraResultEvent(plcCameraResult));
	}

	private byte getProductStatusByte(String code) {
		if (CameraConstants.getCameraBlobErrorCode().getStringCode().equals(code)) {
			return (byte) 0x02;
		} else if (CameraConstants.getCameraErrorCode().getStringCode().equals(code)) {
			return (byte) 0x03;
		} else {
			return (byte) 0x01;
		}
	}

	private byte getLastByteOfEncryptedCode(String code, boolean valid) {
		if (valid) {
			byte[] codeBytes = code.getBytes();
			return codeBytes[codeBytes.length - 1];
		} else {    //INK_DETECTED and NO_INK
			return NO_CODE_HEX;
		}
	}
}
