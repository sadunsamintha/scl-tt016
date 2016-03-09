package com.sicpa.standard.sasscl.controller.productionconfig.xstream;

import java.util.Map.Entry;

import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PlcConfig;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class PlcConfigConverter extends AbstractLayoutConfigConverter {

	private static final String CABINET_ELT = "cabinet";
	private static final String LINE_ELT = "line";
	private static final String LINE_INDEX_ATR = "index";

	public PlcConfigConverter() {
		super(PlcConfig.class);
	}

	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		PlcConfig config = (PlcConfig) source;

		writeCabinet(config, writer);
		writeLines(config, writer);
	}

	private void writeCabinet(PlcConfig config, HierarchicalStreamWriter writer) {
		writer.startNode(CABINET_ELT);
		writeConfig(writer, config.getCabinetProperties());
		writer.endNode();
	}

	private void writeLines(PlcConfig config, HierarchicalStreamWriter writer) {
		for (Entry<Integer, StringMap> entry : config.getLinesProperties().entrySet()) {
			writeLine(entry.getKey(), entry.getValue(), writer);
		}
	}

	private void writeLine(int lineIndex, StringMap values, HierarchicalStreamWriter writer) {
		writer.startNode(LINE_ELT);
		writer.addAttribute(LINE_INDEX_ATR, lineIndex + "");
		writeConfig(writer, values);
		writer.endNode();
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		PlcConfig config = new PlcConfig();

		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if (reader.getNodeName().equals(CABINET_ELT)) {
				readCabinet(config, reader);
			} else if (reader.getNodeName().equals(LINE_ELT)) {
				readLine(config, reader);
			}
			reader.moveUp();
		}

		return config;
	}

	private void readCabinet(PlcConfig config, HierarchicalStreamReader reader) {
		readConfig(reader, config.getCabinetProperties());
	}

	private void readLine(PlcConfig config, HierarchicalStreamReader reader) {
		int lineIndex = Integer.parseInt(reader.getAttribute(LINE_INDEX_ATR));
		StringMap map = new StringMap();
		readConfig(reader, map);
		config.getLinesProperties().put(lineIndex, map);
	}
}
