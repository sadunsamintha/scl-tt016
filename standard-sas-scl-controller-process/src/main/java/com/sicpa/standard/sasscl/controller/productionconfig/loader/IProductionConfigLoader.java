package com.sicpa.standard.sasscl.controller.productionconfig.loader;

import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.model.ProductionMode;

/**
 * responsible for loading production configurations
 * 
 * @author DIelsch
 * 
 */
public interface IProductionConfigLoader {

    /**
	 * Returns <code>IProductionConfig</code> without validating that all context resources
	 * exist.
	 * @param mode
	 * @return IProductionConfig or null
     */
	IProductionConfig get(ProductionMode mode);

}
