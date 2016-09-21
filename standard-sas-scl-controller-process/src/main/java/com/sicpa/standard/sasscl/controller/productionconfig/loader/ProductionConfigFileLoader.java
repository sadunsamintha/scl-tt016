package com.sicpa.standard.sasscl.controller.productionconfig.loader;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.mapping.IProductionConfigMapping;
import com.sicpa.standard.sasscl.controller.view.event.WarningViewEvent;
import com.sicpa.standard.sasscl.model.ProductionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ProductionConfigFileLoader implements IProductionConfigLoader {

	private static final Logger logger = LoggerFactory.getLogger(ProductionConfigFileLoader.class);

	private IProductionConfigMapping mapping;
	private String folder;


	@Override
	public IProductionConfig get(ProductionMode mode) {
		String path = folder + "/" + mapping.getProductionConfigId(mode) + ".xml";
		logger.debug("loading production config :{}", path);
		IProductionConfig ret = null;
		try {
			ret =  (IProductionConfig) ConfigUtils.load(path);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return ret;

	}



	public void setMapping(IProductionConfigMapping mapping) {
		this.mapping = mapping;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}
}
