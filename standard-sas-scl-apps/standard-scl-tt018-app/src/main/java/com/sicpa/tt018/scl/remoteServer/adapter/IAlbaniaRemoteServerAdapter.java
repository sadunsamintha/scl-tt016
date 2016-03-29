package com.sicpa.tt018.scl.remoteServer.adapter;

import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.tt018.interfaces.scl.master.dto.CountedProductsDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.EjectedActivationsDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.MarketTypeDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.PackagedProductsDTO;

public interface IAlbaniaRemoteServerAdapter {

	ProductionParameterRootNode createSkuSelectionTree(MarketTypeDTO marketTypeDto);

	PackagedProductsDTO convertDomesticProduction(PackagedProducts products, int subsystemId);

	CountedProductsDTO convertCountingProduction(PackagedProducts products, int subsystemId);

	EjectedActivationsDTO convertEjectedProduction(PackagedProducts products, int subsystemId);

}
