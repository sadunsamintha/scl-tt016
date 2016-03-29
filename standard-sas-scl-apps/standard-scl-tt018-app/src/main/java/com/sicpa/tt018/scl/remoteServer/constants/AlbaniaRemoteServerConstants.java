package com.sicpa.tt018.scl.remoteServer.constants;

import static com.sicpa.standard.sasscl.model.ProductionMode.EXPORT;
import static com.sicpa.standard.sasscl.model.ProductionMode.MAINTENANCE;
import static com.sicpa.tt018.scl.model.productionParameters.AlbaniaProductionMode.SOFT_DRINK;

import java.util.LinkedList;
import java.util.List;

import com.sicpa.standard.crypto.interfaces.IEncoder;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class AlbaniaRemoteServerConstants {

	public static final String SUBSYSTEM_ID_PROPERTY = "com.sicpa.subsystemId";
	public static final String PROPERTY_USERNAME = "com.sicpa.username";
	public static final String PROPERTY_PASSWORD = "com.sicpa.password";

	public final static ProductionMode[] COUNTING_MODES_SCL = { EXPORT, SOFT_DRINK, MAINTENANCE };

	public static final List<IEncoder> EMTY_ENODER_LIST = new LinkedList<IEncoder>();

}
