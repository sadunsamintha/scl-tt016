package com.sicpa.standard.sasscl.controller.productionconfig.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.mapping.IProductionConfigMapping;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class ProductionConfigFileLoader implements IProductionConfigLoader {

	private static final Logger logger = LoggerFactory.getLogger(ProductionConfigFileLoader.class);

	private IProductionConfigMapping mapping;
	private String folder;

	/**
	 * This method gets a production Configuration given a production mode.
	 */
	@Override
	public IProductionConfig get(ProductionMode mode) {
		String file = folder + "/" + mapping.getProductionConfigId(mode) + ".xml";
		logger.debug("loading production config :{}", file);
		try {
			return (IProductionConfig) ConfigUtils.load(file);
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	public void setMapping(IProductionConfigMapping mapping) {
		this.mapping = mapping;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}
}
