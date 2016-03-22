package com.sicpa.standard.sasscl.controller.productionconfig.factory.mapping;

import com.sicpa.standard.sasscl.controller.productionconfig.IPrinterFactoryMapping;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterType;

public class PrinterFactoryMapping implements IPrinterFactoryMapping {

	@Override
	public String getPrinterBeanName(PrinterType printerType) {
		if (printerType.equals(PrinterType.DOMINO)) {
			return "printerDomino";
		} else if (printerType.equals(PrinterType.LEIBINGER)) {
			return "printerLeibinger";
		} else {
			throw new IllegalArgumentException(printerType + " not handled");
		}
	}

}
