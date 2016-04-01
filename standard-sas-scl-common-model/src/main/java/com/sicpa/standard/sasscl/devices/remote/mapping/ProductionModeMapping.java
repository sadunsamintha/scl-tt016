package com.sicpa.standard.sasscl.devices.remote.mapping;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.sasscl.devices.remote.mapping.IProductionModeMapping;
import com.sicpa.standard.sasscl.model.ProductionMode;

public abstract class ProductionModeMapping implements IProductionModeMapping {

	private final Map<Integer, ProductionMode> mapping = new HashMap<>();

	@Override
	public ProductionMode getProductionModeFromRemoteId(int remoteid) {
		return mapping.get(remoteid);
	}

	public void add(ProductionMode mode, int idOnRemote) {
		mapping.put(idOnRemote, mode);
	}

}
