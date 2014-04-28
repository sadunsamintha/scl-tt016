package com.sicpa.standard.sasscl.controller.productionconfig;

import com.sicpa.standard.sasscl.controller.productionconfig.config.AbstractLayoutConfig;


/**
 * responsible to provide to the CONFIGURABLE the CONFIG it has to load
 * 
 * @author DIelsch
 * 
 * @param <CONFIG>
 *            come from the coding config
 * @param <CONFIGURABLE>
 *            camera/printer/qc
 */
public interface IConfigurator<CONFIG extends AbstractLayoutConfig, CONFIGURABLE> {

	void execute(CONFIG config, CONFIGURABLE configurable) throws ConfigurationFailedException;

}
