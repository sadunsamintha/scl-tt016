/**
 * Author	: YYang
 * Date		: Nov 15, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.controller.device.group;

import org.junit.Assert;
import org.junit.Test;

public class DevicesGroupTest {

	@Test
	public void testDevicesGroupEqualMethod() {
		DevicesGroup devicesGroup = new DevicesGroup("group1");
		DevicesGroup devicesGroup2 = new DevicesGroup("group1");
		DevicesGroup devicesGroup3 = new DevicesGroup("group2");
		Assert.assertTrue(devicesGroup.equals(devicesGroup2));
		Assert.assertFalse(devicesGroup2.equals(devicesGroup3));
	}

}
