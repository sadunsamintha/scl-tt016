/**
 * Author	: YYang
 * Date		: Oct 19, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.plc;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.plc.controller.model.PlcModel;

/**
 * @author YYang
 * 
 */
public class loadStdPlcConfigTest {

	@Test
	public void loadModelTest() throws IOException, URISyntaxException {
		PlcModel config = ConfigUtils.load(PlcModel.class, "config/stdplcconfig.xml");
		Assert.assertNotNull(config.getIp());
	}

}
