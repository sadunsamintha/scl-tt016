package com.sicpa.standard.sasscl.devices.plc;

import com.sicpa.standard.client.common.xstream.IXStreamConfigurator;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValue;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValueWithUnit;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValuesForAllVar;
import com.thoughtworks.xstream.XStream;

public class PlcXStreamConfigurator implements IXStreamConfigurator {

	@Override
	public void configure(XStream xstream) {
		xstream.alias("PlcValuesForAllVar", PlcValuesForAllVar.class);
		xstream.addImplicitCollection(PlcValuesForAllVar.class, "listValues");

		xstream.alias("PlcValueWithUnit", PlcValueWithUnit.class);

		xstream.alias("PlcValue", PlcValue.class);
		xstream.useAttributeFor(PlcValue.class, "varName");
		xstream.useAttributeFor(PlcValueWithUnit.class, "unit");
		xstream.useAttributeFor(PlcValueWithUnit.class, "unitVariable");

	}
}
