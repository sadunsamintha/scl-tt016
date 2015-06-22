package com.sicpa.standard.sasscl.devices.remote.impl.productionmodemapping;

import com.sicpa.standard.sasscl.devices.remote.mapping.IProductionModeMapping;
import com.sicpa.standard.sasscl.model.ProductionMode;

import java.util.HashMap;

public abstract class ProductionModeMapping implements IProductionModeMapping {

	protected final HashMap<Integer, ProductionMode> mapping = new HashMap<Integer, ProductionMode>();

	@Override
	public ProductionMode getProductionModeFromRemoteId(int remoteid) {
		return mapping.get(remoteid);
	}

	public void add(ProductionMode mode, int idOnRemote) {
		mapping.put(idOnRemote, mode);
	}

}
