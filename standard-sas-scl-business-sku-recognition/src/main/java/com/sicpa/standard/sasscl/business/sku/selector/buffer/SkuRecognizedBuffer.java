package com.sicpa.standard.sasscl.business.sku.selector.buffer;

import static java.util.stream.Collectors.groupingBy;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sicpa.standard.sasscl.business.sku.selector.ISkuRecognizedBuffer;
import com.sicpa.standard.sasscl.model.SKU;

public class SkuRecognizedBuffer implements ISkuRecognizedBuffer {

	private final LinkedList<SKU> skus = new LinkedList<SKU>();
	private int bufferSize;

	@Override
	public void reset() {
		synchronized (skus) {
			skus.clear();
		}
	}

	@Override
	public void add(SKU sku) {
		synchronized (skus) {
			if (isFull()) {
				skus.removeFirst();
			}
			skus.add(sku);
		}
	}

	private boolean isFull() {
		synchronized (skus) {
			return skus.size() >= bufferSize;
		}
	}

	@Override
	public boolean isReady() {
		return isFull();
	}

	@Override
	public SKU getSku() {
		return getMostOccurringSku();
	}

	private SKU getMostOccurringSku() {
		synchronized (skus) {
			Map<Integer, List<SKU>> skusById = skus.stream().collect(groupingBy(SKU::getId));

			Comparator<Entry<Integer, List<SKU>>> skuListSizeComparator = (e1, e2) -> Integer.valueOf(
					e1.getValue().size()).compareTo(e2.getValue().size());

			SKU mostOccurringSku = skusById.entrySet().stream().max(skuListSizeComparator).get().getValue().get(0);

			return mostOccurringSku;
		}
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
}
