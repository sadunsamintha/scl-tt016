package com.sicpa.standard.sasscl.devices.printer;

import com.sicpa.standard.client.common.xstream.IXStreamConfigurator;
import com.sicpa.standard.printer.controller.model.DominoPrinterModel;
import com.sicpa.standard.printer.leibinger.model.LeibingerPrinterModel;
import com.thoughtworks.xstream.XStream;

public class PrinterXStreamConfigurator implements IXStreamConfigurator {

	@Override
	public void configure(XStream xstream) {

		xstream.alias("DominoPrinterModel", DominoPrinterModel.class);
		xstream.alias("LeibingerPrinterModel", LeibingerPrinterModel.class);

	}

}
