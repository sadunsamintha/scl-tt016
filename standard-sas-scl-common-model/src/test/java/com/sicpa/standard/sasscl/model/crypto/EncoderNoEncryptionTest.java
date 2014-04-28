package com.sicpa.standard.sasscl.model.crypto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.EncoderNoEncryptionSimulator;

public class EncoderNoEncryptionTest {

	private EncoderNoEncryptionSimulator test;

	@Before
	public void setUp() throws Exception {
		test = new EncoderNoEncryptionSimulator(0,1, 0, 1, 2010, 1);
		test.setId(1);
	}

	@Test
	public final void testEncoderNoEncryption() {

		assertEquals(1, test.getMax());
		assertEquals(2010, test.getYear());
		assertEquals(1, test.getId());

	}

	@Test
	public final void testGetEncryptedCode() {

		boolean exception = false;

		try {
			assertEquals("0", test.getEncryptedCode());
		} catch (CryptographyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			test.getEncryptedCode();
			test.getEncryptedCode();
		} catch (CryptographyException e) {

			exception = true;
		}
		assertTrue(exception);
	}

	@Test
	public final void testGetNumberOfAvailableEncryptedCode() {
		assertEquals(1, test.getNumberOfAvailableEncryptedCode());
	}

	@Test
	public final void testIsEncoderEmpty() {
		assertFalse(test.isEncoderEmpty());
		try {
			test.getEncryptedCode();
			test.getEncryptedCode();
			test.getEncryptedCode();
		} catch (CryptographyException e) {
		}

		assertTrue(test.isEncoderEmpty());
	}

	@Test
	public final void testGetEncryptedCodes() {
		try {
			assertEquals(2, test.getEncryptedCodes(2).size());
		} catch (CryptographyException e) {

		}
	}

	@Test
	public final void testEquals() {

		EncoderNoEncryptionSimulator tmp1 = new EncoderNoEncryptionSimulator(0,1, 0, 1, 2010, 1);
		tmp1.setId(1);
		EncoderNoEncryptionSimulator tmp2 = new EncoderNoEncryptionSimulator(0,1, 0, 0, 2010, 1);
		tmp2.setId(2);
		assertFalse(test.equals(null));
		assertTrue(test.equals(test));
		assertTrue(test.equals(tmp1));
		assertFalse(test.equals(tmp2));
	}

}
