package com.sicpa.standard.sasscl.controller.productionconfig.xstream;

import com.sicpa.standard.sasscl.controller.productionconfig.config.BisConfig;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class BisConfigConverter extends AbstractLayoutConfigConverter {

	public BisConfigConverter() {
		super(BisConfig.class);
	}

	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		BisConfig config = (BisConfig) source;
		writer.addAttribute("id", config.getId());
		writeConfig(writer, config.getProperties());
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		BisConfig config = new BisConfig();
		config.setId(reader.getAttribute("id"));
		readConfig(reader, config.getProperties());
		return config;
	}

}
