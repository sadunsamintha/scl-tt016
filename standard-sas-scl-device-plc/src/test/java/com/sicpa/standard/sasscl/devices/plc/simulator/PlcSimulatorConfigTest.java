/**
 * Author	: YYang
 * Date		: Oct 5, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.plc.simulator;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.plc.value.IPlcVariable;

/**
 *
 */
public class PlcSimulatorConfigTest {

	/**
	 * to test to load the plc simulator config model
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@Test
	public void loadModelTest() throws IOException, URISyntaxException {
		PlcSimulatorConfig config = ConfigUtils.load(PlcSimulatorConfig.class, "config/plcsimulatorconfig.xml");

		Assert.assertNotNull(config.getPlcVariables());

		Assert.assertTrue(config.getPlcVariables().size() > 0);

		for (IPlcVariable<?> var : config.getPlcVariables()) {
			Assert.assertNotNull(var.getVariableName());
		}

		for (PlcSimulatorNotificationConfig var : config.getNotificationVariables()) {
			Assert.assertNotNull(var.getVarName());
		}
	}
}
