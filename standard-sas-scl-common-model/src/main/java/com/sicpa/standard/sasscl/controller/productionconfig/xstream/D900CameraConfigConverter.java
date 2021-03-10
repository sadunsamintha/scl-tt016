package com.sicpa.standard.sasscl.controller.productionconfig.xstream;

import com.sicpa.standard.sasscl.controller.productionconfig.config.D900CameraConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.D900CameraType;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class D900CameraConfigConverter extends AbstractLayoutConfigConverter {

	public D900CameraConfigConverter() {
		super(D900CameraConfig.class);
	}

	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		D900CameraConfig config = (D900CameraConfig) source;
		writer.addAttribute("deviceType", config.getCameraType().getDescription());
		writer.addAttribute("id", config.getId());
		writeConfig(writer, config.getProperties());
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		D900CameraConfig config = new D900CameraConfig();
		config.setCameraType(new D900CameraType(reader.getAttribute("deviceType")));
		config.setId(reader.getAttribute("id"));
		readConfig(reader, config.getProperties());
		return config;
	}

}
