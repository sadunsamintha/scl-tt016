package com.sicpa.standard.sasscl.device;

import com.sicpa.standard.printer.controller.model.command.PrinterMessage;
import com.sicpa.standard.printer.controller.model.command.PrinterMessageId;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraAdaptorSimulator;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController;
import com.sicpa.standard.sasscl.devices.camera.simulator.CodeGetMethod;
import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterAdaptorSimulator;
import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.utils.printer.PrinterSimulatorThatProvidesCodes;

public class PrinterErrorTestSCL extends AbstractFunctionnalTest {
	private PrinterSimulatorThatProvidesCodes printerSimul;

	public void test() {
		init();
		setProductionParameter(1, 1, ProductionMode.STANDARD);
		this.checkApplicationStatusCONNECTED();
		startProduction();
		checkApplicationStatusRUNNING();
		triggerPrinterError();
		checkApplicationStatusRECOVERING();
		recoverPrinterError();
		checkApplicationStatusCONNECTED();
		exit();
	}

	public void triggerPrinterError() {
		PrinterMessage message = new PrinterMessage(PrinterMessageId.CHARGE_FAULT);
		message.setIssueSolved(false);
		printerSimul.firePrinterMessage(message);
	}

	public void recoverPrinterError() {
		PrinterMessage message = new PrinterMessage(PrinterMessageId.CHARGE_FAULT);
		message.setIssueSolved(true);
		printerSimul.firePrinterMessage(message);
	}

	@Override
	protected void configureDevices() {
		CameraAdaptorSimulator cameraDevice = (CameraAdaptorSimulator) devicesMap.get("qc_1");
		camera = (CameraSimulatorController) cameraDevice.getSimulatorController();
		cameraModel = camera.getCameraModel();
		cameraModel.setCodeGetMethod(CodeGetMethod.requested);

		PrinterAdaptorSimulator printerDevice = (PrinterAdaptorSimulator) devicesMap.get("pr_scl_1");

		printerSimul = new PrinterSimulatorThatProvidesCodes();
		printerSimul.setListener(printerDevice);
		printerSimul.setCodeByRequest(1);
	}

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfigSCL();
	}
}
