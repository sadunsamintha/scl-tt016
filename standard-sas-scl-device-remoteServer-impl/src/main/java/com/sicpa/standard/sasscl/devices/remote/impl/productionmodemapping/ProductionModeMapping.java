package com.sicpa.standard.sasscl.devices.remote.impl.productionmodemapping;

import java.util.HashMap;

import com.sicpa.standard.sasscl.devices.remote.mapping.IProductionModeMapping;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class ProductionModeMapping implements IProductionModeMapping {

	protected final HashMap<Integer, ProductionMode> mapping = new HashMap<Integer, ProductionMode>();

	@Override
	public ProductionMode getProductionModeFromRemoteId(int remoteid) {
		return mapping.get(remoteid);
	}

	public void add(ProductionMode mode, int idOnRemote) {
		mapping.put(idOnRemote, mode);
	}

}
