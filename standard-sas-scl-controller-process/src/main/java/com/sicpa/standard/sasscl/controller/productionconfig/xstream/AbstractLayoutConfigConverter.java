package com.sicpa.standard.sasscl.controller.productionconfig.xstream;

import java.util.Map.Entry;

import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.sasscl.controller.productionconfig.config.AbstractLayoutConfig;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public abstract class AbstractLayoutConfigConverter implements Converter {

	protected String entryName = "property";
	protected String keyName = "key";
	protected String valueName = "value";

	protected Class<? extends AbstractLayoutConfig> clazz;

	public AbstractLayoutConfigConverter(Class<? extends AbstractLayoutConfig> clazz) {
		this.clazz = clazz;
	}

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return type.equals(clazz);
	}

	protected void writeConfig(HierarchicalStreamWriter writer, StringMap config) {
		for (Entry<String, String> entry : config.entrySet()) {
			writer.startNode(entryName);
			writer.addAttribute("key", entry.getKey());
			writer.addAttribute(valueName, entry.getValue());
			writer.endNode();
		}
	}

	protected void readConfig(HierarchicalStreamReader reader, StringMap config) {
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if (reader.getNodeName().equals(entryName)) {
				String key = reader.getAttribute(keyName);
				String value = reader.getAttribute(valueName);
				config.put(key, value);
			}
			reader.moveUp();
		}
	}
}