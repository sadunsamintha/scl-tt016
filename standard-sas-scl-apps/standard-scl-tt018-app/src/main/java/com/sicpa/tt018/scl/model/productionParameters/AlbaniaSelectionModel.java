package com.sicpa.tt018.scl.model.productionParameters;

import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.productionParameterSelection.SelectionModel;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class AlbaniaSelectionModel extends SelectionModel {
	public AlbaniaSelectionModel(final IProductionParametersNode root) {
		super(root);
	}

	@Override
	protected void populatePermissions() {
		this.mapPermissions.put(ProductionMode.STANDARD, SasSclPermission.PRODUCTION_MODE_STANDARD);
		this.mapPermissions.put(ProductionMode.MAINTENANCE, SasSclPermission.PRODUCTION_MODE_MAINTENANCE);
		this.mapPermissions.put(ProductionMode.EXPORT, SasSclPermission.PRODUCTION_MODE_EXPORT);
		this.mapPermissions.put(AlbaniaProductionMode.SOFT_DRINK, AlbaniaPermission.PRODUCTION_MODE_SOFT_DRINK);
	}

}
