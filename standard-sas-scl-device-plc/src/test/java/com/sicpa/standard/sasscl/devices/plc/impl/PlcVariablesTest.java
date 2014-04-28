package com.sicpa.standard.sasscl.devices.plc.impl;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import com.sicpa.standard.sasscl.devices.plc.PlcVariableMap;

public class PlcVariablesTest {

	private static final String VALUE = "VALUE";


	@Test
	public void testGetVariableName() {
		HashMap<String, String> maps = new HashMap<String, String>();
		maps.put(PlcVariables.ERR_CONSECUTIVE_INVLD_CODES.toString(), VALUE);
		new PlcVariableMap(maps);
		assertEquals(VALUE,PlcVariables.ERR_CONSECUTIVE_INVLD_CODES.getVariableName());
	}

}
