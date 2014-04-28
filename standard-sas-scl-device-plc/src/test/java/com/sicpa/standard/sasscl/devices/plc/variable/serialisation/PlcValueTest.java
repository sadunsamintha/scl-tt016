package com.sicpa.standard.sasscl.devices.plc.variable.serialisation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.unit.PlcUnit;

public class PlcValueTest {

	private PlcValue plcValue;
	private PlcValuesForAllVar plcValuesForAllVar;
	private PlcValueWithUnit plcValueWithUnit;

	private String name;
	private Object value;
	private PlcUnit plcUnit;

	@Before
	public void setUp() throws Exception {
		plcUnit = PlcUnit.PULSE;
		name = "test";
		value = new Object();
		plcValue = new PlcValue(value, name);

	}

	@Test
	public final void testPlcValue() {

		assertEquals(name, plcValue.getVarName());
		assertEquals(value, plcValue.getValue());
	}

	@Test
	public final void testPlcValuesForAllVar() {
		plcValuesForAllVar = new PlcValuesForAllVar();
		plcValuesForAllVar.add(this.plcValue);
		assertEquals(plcValue, plcValuesForAllVar.get(name));
		assertEquals(value, plcValuesForAllVar.getValue(name));

	}

	@Test
	public final void testPlcValueWithUnit() {
		plcValueWithUnit = new PlcValueWithUnit(value, name, plcUnit, "unitVar");
		assertEquals(plcUnit, plcValueWithUnit.getUnit());
		assertEquals("unitVar", plcValueWithUnit.getUnitVariable());
		assertEquals(value, plcValueWithUnit.getValue());
		assertEquals(name, plcValueWithUnit.getVarName());
	}

}
