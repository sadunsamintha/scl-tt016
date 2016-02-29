package com.sicpa.standard.sasscl.device;

import com.sicpa.standard.printer.controller.model.command.PrinterMessage;
import com.sicpa.standard.printer.controller.model.command.PrinterMessageId;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class PrinterErrorTestSCL extends AbstractFunctionnalTest {

	@Override
	protected ProductionMode getProductionMode() {
		return SCL_MODE;
	}

	public void test() {
		init();
		setProductionParameter();
		checkApplicationStatusCONNECTED();
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
		printer.onMessageReceived(message);
	}

	public void recoverPrinterError() {
		PrinterMessage message = new PrinterMessage(PrinterMessageId.CHARGE_FAULT);
		message.setIssueSolved(true);
		printer.onMessageReceived(message);
	}
}
