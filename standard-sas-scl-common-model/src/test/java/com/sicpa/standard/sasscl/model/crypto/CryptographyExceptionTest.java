package com.sicpa.standard.sasscl.model.crypto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sicpa.standard.sasscl.sicpadata.CryptographyException;

public class CryptographyExceptionTest {

	@Test
	public final void testExceptions() {

		int exceptions = 0;

		try {
			throw new CryptographyException();
		} catch (CryptographyException e) {
			exceptions++;
		}
		try {
			throw new CryptographyException("");
		} catch (CryptographyException e) {
			exceptions++;
		}
		try {
			throw new CryptographyException(new Throwable());
		} catch (CryptographyException e) {
			exceptions++;
		}
		try {
			throw new CryptographyException("", new Throwable());
		} catch (CryptographyException e) {
			exceptions++;
		}
		try {
			throw new CryptographyException(new Throwable(), "", "", "");
		} catch (CryptographyException e) {
			exceptions++;
		}

		assertEquals(5, exceptions);
	}

}
