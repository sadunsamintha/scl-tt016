package com.sicpa.standard.sasscl.business.sku.selector.buffer;

import static java.util.Comparator.comparingInt;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map.Entry;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sicpa.standard.sasscl.business.sku.selector.ISkuRecognizedBuffer;
import com.sicpa.standard.sasscl.model.SKU;

public class SkuRecognizedBuffer implements ISkuRecognizedBuffer {

	private final CircularFifoBuffer skus;

	public SkuRecognizedBuffer(int bufferSize) {
		skus = new CircularFifoBuffer(bufferSize);
	}

	@Override
	public void reset() {
		synchronized (skus) {
			skus.clear();
		}
	}

	@Override
	public void add(SKU sku) {
		synchronized (skus) {
			skus.add(sku);
		}
	}

	@Override
	public boolean isReady() {
		synchronized (skus) {
			return skus.isFull();
		}
	}

	@Override
	public SKU getSku() {
		return getMostOccurringSku();
	}

	private SKU getMostOccurringSku() {
		synchronized (skus) {
			Multimap<Integer, SKU> skusById = collectSkusById();
			return getMostOccurringSku(skusById);
		}
	}

	private Multimap<Integer, SKU> collectSkusById() {
		Multimap<Integer, SKU> skusById = ArrayListMultimap.create();
		for (Object o : skus) {
			SKU sku = (SKU) o;
			skusById.put(sku.getId(), sku);
		}
		return skusById;
	}

	private SKU getMostOccurringSku(Multimap<Integer, SKU> skusById) {
		Comparator<Entry<Integer, Collection<SKU>>> valueListSizeComparator = comparingInt(e -> e.getValue().size());

		SKU mostOccurringSku = skusById.asMap().entrySet().stream().max(valueListSizeComparator).get().getValue()
				.iterator().next();

		return mostOccurringSku;
	}

}
