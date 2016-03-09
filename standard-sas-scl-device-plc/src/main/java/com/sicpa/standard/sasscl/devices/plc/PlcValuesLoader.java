package com.sicpa.standard.sasscl.devices.plc;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.PropertiesUtils;
import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class PlcValuesLoader implements IPlcValuesLoader {

	private static final Logger logger = LoggerFactory.getLogger(PlcValuesLoader.class);

	private static final String LINE_INDEX_PLACEHOLDER = "#x";

	private String cabinetConfigFile = "cabinet.properties";
	private String lineConfigFile = "line-" + LINE_INDEX_PLACEHOLDER + ".properties";
	private final Map<Integer, StringMap> valuesByLines = new HashMap<>();

	private int lineCount = 3;
	private String configFolder;
	private IPlcParamSender paramSender;

	private void loadProperties() {
		try {
			valuesByLines.put(0, loadCabinetProperties());
			for (int lineIndex = 1; lineIndex < lineCount + 1; lineIndex++) {
				valuesByLines.put(lineIndex, loadLineProperties(lineIndex));
			}
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	private StringMap loadCabinetProperties() throws IOException {
		return loadProperties(cabinetConfigFile);
	}

	private StringMap loadLineProperties(int lineIndex) throws IOException {
		String file = getLinePropertiesFileName(lineIndex);
		StringMap res = loadProperties(file);
		res = replaceLinePlaceholderInProperties(lineIndex, res);

		return res;
	}

	private StringMap replaceLinePlaceholderInProperties(int lineIndex, StringMap properties) {
		StringMap res = new StringMap();
		for (Entry<String, String> entry : properties.entrySet()) {
			res.put(entry.getKey().replace(LINE_INDEX_PLACEHOLDER, lineIndex + ""), entry.getValue());
		}
		return res;
	}

	private String getLinePropertiesFileName(int lineIndex) {
		return lineConfigFile.replace(LINE_INDEX_PLACEHOLDER, lineIndex + "");
	}

	@Override
	public Map<Integer, StringMap> getValues() {

		if (valuesByLines.isEmpty()) {
			loadProperties();
		}

		return valuesByLines;
	}

	private StringMap loadProperties(String file) throws IOException {
		Properties prop = new Properties();
		prop.load(new FileReader(new File(configFolder + "/" + file)));

		StringMap res = new StringMap();
		for (Entry<Object, Object> entry : prop.entrySet()) {
			res.put(entry.getKey() + "", entry.getValue() + "");
		}
		return res;
	}

	@Override
	public void sendValues() {
		for (Entry<Integer, StringMap> entry : valuesByLines.entrySet()) {
			sendValuesForLine(entry.getKey(), entry.getValue());
		}
	}

	private void sendValuesForLine(int lineIndex, StringMap values) {
		for (Entry<String, String> entry : values.entrySet()) {
			String value = entry.getValue();
			String plcVarName = entry.getKey();
			sendToPlc(plcVarName, value, lineIndex);
		}
	}

	@Override
	public void saveCabinetNewValue(String varName, String value) {
		StringMap values = valuesByLines.get(0);
		values.put(varName, value);
		save(cabinetConfigFile, values);
	}

	@Override
	public void saveLineNewValue(String varName, String value, int lineIndex) {
		StringMap values = valuesByLines.get(lineIndex);
		values.put(varName, value);
		save(getLinePropertiesFileName(lineIndex), values);
	}

	private void save(String fileName, StringMap values) {
		try {
			PropertiesUtils.savePropertiesKeepOrderAndComment(new File(configFolder + "/" + fileName), values);
		} catch (ConfigurationException e) {
			logger.error("", e);
		}
	}

	@Override
	public int getLineCount() {
		return lineCount;
	}

	protected void sendToPlc(String plcLogicalVarName, String value, int lineIndex) {
		try {
			paramSender.sendToPlc(plcLogicalVarName, value, lineIndex);
		} catch (PlcAdaptorException e) {
			logger.error("failed to send param " + plcLogicalVarName + ":" + value, e);
			EventBusService.post(new MessageEvent(this, MessageEventKey.PLC.ERROR_SENDING_PARAM, plcLogicalVarName,
					value + ""));
		}
	}

	public void setParamSender(IPlcParamSender paramSender) {
		this.paramSender = paramSender;
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}

	public void setConfigFolder(String configFolder) {
		this.configFolder = configFolder;
	}
}
