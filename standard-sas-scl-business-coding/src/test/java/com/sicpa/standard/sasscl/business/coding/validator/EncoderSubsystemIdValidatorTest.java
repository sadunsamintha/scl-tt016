package com.sicpa.standard.sasscl.business.coding.validator;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;

public class EncoderSubsystemIdValidatorTest {

	@Test
	public void testValid() {
		EncoderSubsystemIdValidator validator = new EncoderSubsystemIdValidator();
		SubsystemIdProvider provider = new SubsystemIdProvider(13);
		validator.setSubsystemIdProvider(provider);
		IEncoder encoder = Mockito.mock(IEncoder.class);
		Mockito.doReturn(13L).when(encoder).getSubsystemId();

		EncoderValidatorResult res = validator.isEncoderValid(encoder);
		Assert.assertTrue(res.isValid());
	}

	@Test
	public void testInvlid() {
		EncoderSubsystemIdValidator validator = new EncoderSubsystemIdValidator();
		SubsystemIdProvider provider = new SubsystemIdProvider(13);
		validator.setSubsystemIdProvider(provider);
		IEncoder encoder = Mockito.mock(IEncoder.class);
		Mockito.doReturn(14L).when(encoder).getSubsystemId();

		EncoderValidatorResult res = validator.isEncoderValid(encoder);
		res = validator.isEncoderValid(encoder);
		Assert.assertFalse(res.isValid());
	}

}
