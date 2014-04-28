package com.sicpa.standard.sasscl.business.coding.validator;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import com.sicpa.standard.sasscl.config.GlobalBean;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;

public class EncoderSubsystemIdValidatorTest {

	@Test
	public void testValid() {
		EncoderSubsystemIdValidator validator = new EncoderSubsystemIdValidator();
		GlobalBean global = new GlobalBean();
		global.setSubsystemId(13);
		validator.setGlobalBean(global);
		IEncoder encoder = Mockito.mock(IEncoder.class);
		Mockito.doReturn(13L).when(encoder).getSubsystemId();

		EncoderValidatorResult res = validator.isEncoderValid(encoder);
		Assert.assertTrue(res.isValid());
	}

	@Test
	public void testInvlid() {
		EncoderSubsystemIdValidator validator = new EncoderSubsystemIdValidator();
		GlobalBean global = new GlobalBean();
		global.setSubsystemId(13);
		validator.setGlobalBean(global);
		IEncoder encoder = Mockito.mock(IEncoder.class);
		Mockito.doReturn(14L).when(encoder).getSubsystemId();

		EncoderValidatorResult res = validator.isEncoderValid(encoder);
		res = validator.isEncoderValid(encoder);
		Assert.assertFalse(res.isValid());
	}

}
