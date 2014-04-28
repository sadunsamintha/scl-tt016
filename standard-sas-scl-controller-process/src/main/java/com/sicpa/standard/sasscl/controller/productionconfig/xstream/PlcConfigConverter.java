package com.sicpa.standard.sasscl.controller.productionconfig.xstream;

import com.sicpa.standard.sasscl.controller.productionconfig.config.PlcConfig;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class PlcConfigConverter extends AbstractLayoutConfigConverter {

	public static String LINE_1_FILENAME = "line1File";
	public static String LINE_2_FILENAME = "line2File";
	public static String LINE_3_FILENAME = "line3File";

	public static String LINE_1_INDEX = "line1Index";
	public static String LINE_2_INDEX = "line2Index";
	public static String LINE_3_INDEX = "line3Index";

	public PlcConfigConverter() {
		super(PlcConfig.class);
	}

	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		PlcConfig config = (PlcConfig) source;
		writer.addAttribute(LINE_1_FILENAME, config.getLine1ConfigFile());
		writer.addAttribute(LINE_2_FILENAME, config.getLine2ConfigFile());
		writer.addAttribute(LINE_3_FILENAME, config.getLine3ConfigFile());
		writer.addAttribute(LINE_1_INDEX, config.getLine1Index());
		writer.addAttribute(LINE_2_INDEX, config.getLine2Index());
		writer.addAttribute(LINE_3_INDEX, config.getLine3Index());
		writeConfig(writer, config.getProperties());
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		PlcConfig config = new PlcConfig();
		config.setLine1ConfigFile(reader.getAttribute(LINE_1_FILENAME));
		config.setLine2ConfigFile(reader.getAttribute(LINE_2_FILENAME));
		config.setLine3ConfigFile(reader.getAttribute(LINE_3_FILENAME));
		config.setLine1Index(reader.getAttribute(LINE_1_INDEX));
		config.setLine2Index(reader.getAttribute(LINE_2_INDEX));
		config.setLine3Index(reader.getAttribute(LINE_3_INDEX));
		readConfig(reader, config.getProperties());
		return config;
	}
}
