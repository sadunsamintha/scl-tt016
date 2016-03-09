package com.sicpa.standard.sasscl.controller.productionconfig.xstream;

import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraType;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class CameraConfigConverter extends AbstractLayoutConfigConverter {

	public CameraConfigConverter() {
		super(CameraConfig.class);
	}

	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		CameraConfig config = (CameraConfig) source;
		writer.addAttribute("deviceType", config.getCameraType().getDescription());
		writer.addAttribute("id", config.getId());
		writeConfig(writer, config.getProperties());
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		CameraConfig config = new CameraConfig();
		config.setCameraType(new CameraType(reader.getAttribute("deviceType")));
		config.setId(reader.getAttribute("id"));
		readConfig(reader, config.getProperties());
		return config;
	}

}
