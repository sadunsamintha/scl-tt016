package com.sicpa.standard.sasscl.business.sku.selector.buffer;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sicpa.standard.sasscl.model.SKU;

public class SkuIdentifiedBufferTest {

	private final SkuRecognizedBuffer buffer = new SkuRecognizedBuffer();

	@Test
	public void testMostOccuringSku() throws Exception {

		int bufferSize = 10;

		buffer.setBufferSize(bufferSize);

		int skuIdMostOccuring = 1;
		int skuIdMostOccuringCount = 7;

		int skuIdLow = 2;
		int skuIdLowCount = 3;

		addSku(skuIdMostOccuring, skuIdMostOccuringCount);
		addSku(skuIdLow, skuIdLowCount);
		assertEquals(skuIdMostOccuring, buffer.getSku().getId());
	}

	private void addSku(int skuId, int count) {
		SKU s = new SKU(skuId);
		s.setDescription("" + skuId);
		for (int i = 0; i < count; i++) {
			buffer.add(s);
		}
	}

	@Test
	public void testIsReady() throws Exception {
		int bufferSize = 10;
		buffer.setBufferSize(bufferSize);

		int skuId = 1;
		addSku(skuId, bufferSize - 1);
		assertFalse(buffer.isReady());

		addSku(skuId, 1);
		assertTrue(buffer.isReady());

		addSku(skuId, 1);
		assertTrue(buffer.isReady());

		buffer.reset();
		assertFalse(buffer.isReady());
	}

}
