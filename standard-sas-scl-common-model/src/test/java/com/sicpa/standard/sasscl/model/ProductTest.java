package com.sicpa.standard.sasscl.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class ProductTest {

	Product product;
	Code code;
	ProductStatus status;
	SKU sku;
	String batchId;
	Date activationDate;

	@Before
	public void setUp() throws Exception {

		product = new Product();

		code = new Code("test");
		code.setEncoderId(1);
		code.setSequence(2);

		status = new ProductStatus(3, "testStatus");

		sku = new SKU(4, "SKUdescription");

		batchId = "1";

		activationDate = new Date();

		product = new Product(code, status, sku, batchId, activationDate, 1234);
	}

	@Test
	public final void testHashCode() {
		assertNotNull(product.hashCode());
	}

	@Test
	public final void testSetCode() {
		Code tmp = new Code("test");
		product.setCode(tmp);
		assertEquals(tmp, product.getCode());
	}

	@Test
	public final void testSetStatus() {
		ProductStatus tmp = new ProductStatus(1, "testStatus1");
		product.setStatus(tmp);
		assertEquals(tmp, product.getStatus());
	}

	@Test
	public final void testSetSku() {
		SKU tmp = new SKU(1);
		product.setSku(tmp);
		assertEquals(tmp, product.getSku());
	}

	@Test
	public final void testSetBatchId() {
		String tmp = "2";
		product.setProductionBatchId(tmp);
		assertEquals(tmp, product.getProductionBatchId());
	}

	@Test
	public final void testEqualsObject() {
		Product tmp = new Product(code, status, sku, batchId,  activationDate, 1234);
		Product tmp2 = new Product(code, status, sku, "123", activationDate, 1234);
		assertTrue(product.equals(tmp));
		assertFalse(product.equals(tmp2));
	}

	@Test
	public final void testSetActivationDate() {
		Date tmp = new Date();
		product.setActivationDate(tmp);
		assertEquals(tmp, product.getActivationDate());
	}

	@Test
	public final void testToString() {
		System.out.println(product.toString());
		assertTrue(product.toString().contains("test"));
		assertTrue(product.toString().contains("testStatus"));
		assertTrue(product.toString().contains("SKUdescription"));
		assertTrue(product.toString().contains("1"));
	}

}
