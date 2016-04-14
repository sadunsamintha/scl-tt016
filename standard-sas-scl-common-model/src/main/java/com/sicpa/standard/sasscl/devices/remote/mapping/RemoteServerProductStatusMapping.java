package com.sicpa.standard.sasscl.devices.remote.mapping;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.sasscl.devices.remote.mapping.IRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.model.ProductStatus;

public class RemoteServerProductStatusMapping implements IRemoteServerProductStatusMapping {

	private final Map<ProductStatus, Integer> map = new HashMap<>();

	@Override
	public int getRemoteServerProdutcStatus(ProductStatus ProductStatus) {
		return map.getOrDefault(ProductStatus, -1);
	}

	@Override
	public void add(ProductStatus productStatus, int remoteId) {
		map.put(productStatus, remoteId);
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
