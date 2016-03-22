package com.sicpa.tt018.scl.remoteServer.constants;

import java.util.LinkedList;
import java.util.List;

import com.sicpa.standard.crypto.interfaces.IEncoder;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.tt018.scl.model.productionParameters.AlbaniaProductionMode;

public class AlbaniaRemoteServerConstants {

	public static final String SUBSYSTEM_ID_PROPERTY = "com.sicpa.subsystemId";
	public static final String PROPERTY_USERNAME = "com.sicpa.username";
	public static final String PROPERTY_PASSWORD = "com.sicpa.password";

	public final static ProductionMode[] COUNTING_MODES_SCL = { AlbaniaProductionMode.EXPORT, AlbaniaProductionMode.SOFT_DRINK, AlbaniaProductionMode.MAINTENANCE };

	public static final List<IEncoder> EMTY_ENODER_LIST = new LinkedList<IEncoder>();

}
