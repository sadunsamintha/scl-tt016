package com.sicpa.standard.sasscl.utils.mapping;

import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.controller.productionconfig.mapping.ProductionConfigMapping;

public class TestProductionConfigMapping extends ProductionConfigMapping {

	public TestProductionConfigMapping() {
		super();
		put(AbstractFunctionnalTest.SAS_MODE, "standard-SAS");
		put(AbstractFunctionnalTest.SCL_MODE, "standard-SCL");
	}

}
