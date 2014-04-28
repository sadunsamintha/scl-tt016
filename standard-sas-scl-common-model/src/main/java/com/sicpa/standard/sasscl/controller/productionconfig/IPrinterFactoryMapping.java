package com.sicpa.standard.sasscl.controller.productionconfig;

import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterType;


public interface IPrinterFactoryMapping {

	String getPrinterBeanName(PrinterType printerType);

}
