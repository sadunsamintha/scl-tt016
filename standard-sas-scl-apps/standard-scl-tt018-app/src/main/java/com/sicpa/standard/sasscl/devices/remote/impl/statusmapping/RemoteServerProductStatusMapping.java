package com.sicpa.standard.sasscl.devices.remote.impl.statusmapping;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.sasscl.devices.remote.mapping.IRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.model.ProductStatus;

public class RemoteServerProductStatusMapping implements IRemoteServerProductStatusMapping {

	protected final Map<ProductStatus, Integer> map = new HashMap<ProductStatus, Integer>();

	@Override
	public int getRemoteServerProdutcStatus(ProductStatus ProductStatus) {
		Integer res = map.get(ProductStatus);
		if (res == null) {
			return -1;
		}
		return res;
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
