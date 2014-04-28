package com.sicpa.standard.sasscl.controller.productionconfig.mapping;

import com.sicpa.standard.sasscl.model.ProductionMode;

/**
 * mapping between Production mode and ProductionConfig IDs
 * 
 * @author DIelsch
 * 
 */
public interface IProductionConfigMapping {

	String getProductionConfigId(ProductionMode mode);

	/**
	 * used for customisation, to associate a code type to a coding config
	 */
	void put(ProductionMode mode, String productionConfigId);

}
