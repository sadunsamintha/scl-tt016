package com.sicpa.standard.sasscl.model.crypto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;

public class EncoderEmptyExceptionTest {

	@Test
	public final void testExceptions() {

		int exceptions = 0;

		try {
			throw new EncoderEmptyException("");
		} catch (EncoderEmptyException e) {
			exceptions++;
		}
		try {
			throw new EncoderEmptyException();
		} catch (EncoderEmptyException e) {
			exceptions++;
		}
		try {
			throw new EncoderEmptyException("", new Throwable());
		} catch (EncoderEmptyException e) {
			exceptions++;
		}
		try {
			throw new EncoderEmptyException(new Throwable(), "", "", "");
		} catch (EncoderEmptyException e) {
			exceptions++;
		}

		assertEquals(4, exceptions);
	}

}
