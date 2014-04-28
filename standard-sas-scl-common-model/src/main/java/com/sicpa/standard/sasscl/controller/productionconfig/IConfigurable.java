package com.sicpa.standard.sasscl.controller.productionconfig;

import com.sicpa.standard.sasscl.controller.productionconfig.config.AbstractLayoutConfig;



public interface IConfigurable<CONFIG extends AbstractLayoutConfig, CONFIGURABLE> {

	IConfigurator<CONFIG, CONFIGURABLE> getConfigurator();

}
