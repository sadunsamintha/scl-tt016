package com.sicpa.standard.sasscl.controller.productionconfig.factory.mapping;

import com.sicpa.standard.sasscl.controller.productionconfig.IPrinterFactoryMapping;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterType;

public class PrinterFactoryMapping implements IPrinterFactoryMapping {

	@Override
	public String getPrinterBeanName(PrinterType printerType) {
		if (printerType.equals(PrinterType.DOMINO) || printerType.equals(PrinterType.DOMINO2)) {
			return "printerDomino";
		} else if(printerType.equals(PrinterType.LEIBINGER)) {
            return"printerLeibinger";
        } else if(printerType.equals(PrinterType.OI2JET)) {
            return"printerOi2Jet";
        } else {
             throw new IllegalArgumentException(printerType + " not handled");
        }
	}

}
