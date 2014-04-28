package com.sicpa.standard.sasscl.controller.productionconfig.loader;

import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.model.ProductionMode;

/**
 * responsible to load production configs
 * 
 * @author DIelsch
 * 
 */
public interface IProductionConfigLoader {

	IProductionConfig get(ProductionMode mode);
}
