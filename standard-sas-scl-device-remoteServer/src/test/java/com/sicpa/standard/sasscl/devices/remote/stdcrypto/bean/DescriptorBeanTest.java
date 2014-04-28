package com.sicpa.standard.sasscl.devices.remote.stdcrypto.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.sicpa.standard.sasscl.devices.remote.simulator.stdCrypto.bean.DescriptorBean;

/**
 * The class <code>DescriptorBeanTest</code> contains tests for the class <code>{@link DescriptorBean}</code>.
 * @generatedBy CodePro at 12/11/10 15:54
 * @author JAguililla
 * @version $Revision: 1.0 $
 */
public class DescriptorBeanTest {
	/**
	 * Run the DescriptorBean() constructor test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testDescriptorBean () { assertNotNull (new DescriptorBean()); }

	/**
	 * Run the long getBatchId() method test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testGetBatchId () {
		DescriptorBean fixture = new DescriptorBean();
		fixture.setBatchId(1L);
		long result = fixture.getBatchId();
		assertEquals(1L, result);
	}

	/**
	 * Run the long getDate() method test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testGetDate () {
		DescriptorBean fixture = new DescriptorBean();
		fixture.setDate(1L);
		long result = fixture.getDate();
		assertEquals(1L, result);
	}

	/**
	 * Run the long getType() method test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testGetType () {
		DescriptorBean fixture = new DescriptorBean();
		fixture.setType(1L);
		long result = fixture.getType();
		assertEquals(1L, result);
	}

	/**
	 * Run the void setBatchId(long) method test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testSetBatchId () {
		DescriptorBean fixture = new DescriptorBean();
		fixture.setBatchId(1L);
		long batchId = 1L;
		fixture.setBatchId(batchId);
		assertEquals(batchId, fixture.getBatchId());
	}

	/**
	 * Run the void setDate(long) method test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testSetDate () {
		DescriptorBean fixture = new DescriptorBean();
		fixture.setDate(1L);
		long date = 1L;
		fixture.setDate(date);
		assertEquals(date, fixture.getDate());
	}

	/**
	 * Run the void setType(long) method test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testSetType () {
		DescriptorBean fixture = new DescriptorBean();
		fixture.setType(1L);
		long type = 1L;
		fixture.setType(type);
		assertEquals(type, fixture.getType());
	}
}