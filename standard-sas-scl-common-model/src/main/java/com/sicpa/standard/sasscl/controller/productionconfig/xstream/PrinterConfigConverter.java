package com.sicpa.standard.sasscl.controller.productionconfig.xstream;

import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterType;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class PrinterConfigConverter extends AbstractLayoutConfigConverter {

	public PrinterConfigConverter() {
		super(PrinterConfig.class);
	}

	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		PrinterConfig config = (PrinterConfig) source;
		writer.addAttribute("deviceType", config.getPrinterType().getDescription());
		writer.addAttribute("id", config.getId());
		writer.addAttribute("validatedBy", config.getValidatedBy());
		writeConfig(writer, config.getProperties());
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		PrinterConfig config = new PrinterConfig();
		config.setPrinterType(new PrinterType(reader.getAttribute("deviceType")));
		config.setId(reader.getAttribute("id"));
		config.setValidatedBy(reader.getAttribute("validatedBy"));
		readConfig(reader, config.getProperties());
		return config;
	}

}
