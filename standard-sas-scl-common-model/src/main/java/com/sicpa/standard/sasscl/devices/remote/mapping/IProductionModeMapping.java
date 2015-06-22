package com.sicpa.standard.sasscl.devices.remote.mapping;

import com.sicpa.standard.sasscl.model.ProductionMode;

import java.util.List;

public interface IProductionModeMapping {

	ProductionMode getProductionModeFromRemoteId(int remoteid);

	void add(ProductionMode mode, int idOnRemote);

	List<ProductionMode> getAllAvailableProductionModes();
}
