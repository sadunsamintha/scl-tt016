package com.sicpa.standard.sasscl.devices.plc;

import java.util.Map;

import com.sicpa.standard.client.common.utils.StringMap;

public interface IPlcValuesLoader {
	void sendValues();

	 Map<Integer, StringMap> getValues();

	void saveLineNewValue(String varName, String value, int lineIndex);

	void saveCabinetNewValue(String varName, String value);

	int getLineCount();
}
