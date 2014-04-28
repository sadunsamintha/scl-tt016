package com.sicpa.standard.sasscl.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class DecodedCameraCodeTest {

	DecodedCameraCode decodedCameraCode;
	CodeType codeType;
	int batchId;
	int sequence;
	boolean authenticated;

	@Before
	public void setUp() throws Exception {
		decodedCameraCode = new DecodedCameraCode();
		codeType = new CodeType(1);
		batchId = 2;
		sequence = 3;
		authenticated = true;
		decodedCameraCode = new DecodedCameraCode(codeType, batchId, sequence,
				authenticated);

	}

	@Test
	public final void testHashCode() {
		assertNotNull(decodedCameraCode.hashCode());
	}

	@Test
	public final void testGetSetBatchId() {
		decodedCameraCode.setBatchId(5);
		assertEquals(5, decodedCameraCode.getBatchId());
	}

	@Test
	public final void testGetSetSequence() {
		decodedCameraCode.setSequence(5);
		assertEquals(5, decodedCameraCode.getSequence());
	}

	@Test
	public final void testIsSetAuthenticated() {
		decodedCameraCode.setAuthenticated(false);
		assertFalse(decodedCameraCode.isAuthenticated());
	}

	@Test
	public final void testGetSetCodeType() {
		CodeType tmp = new CodeType(2);
		decodedCameraCode.setCodeType(tmp);
		assertEquals(2, decodedCameraCode.getCodeType().getId());
	}

	@Test
	public final void testEqualsObject() {
		DecodedCameraCode tmp1 = new DecodedCameraCode(codeType, batchId,
				sequence, authenticated);
		DecodedCameraCode tmp2 = new DecodedCameraCode(codeType, 5, sequence,
				authenticated);
		assertTrue(decodedCameraCode.equals(tmp1));
		assertFalse(decodedCameraCode.equals(tmp2));
	}

	@Test
	public final void testToString() {
		System.out.println(decodedCameraCode.toString());
		assertTrue(decodedCameraCode.toString().contains("batchId=2"));
		assertTrue(decodedCameraCode.toString().contains("sequence=3"));
		assertTrue(decodedCameraCode.toString().contains("authenticated=true"));
	}

}
