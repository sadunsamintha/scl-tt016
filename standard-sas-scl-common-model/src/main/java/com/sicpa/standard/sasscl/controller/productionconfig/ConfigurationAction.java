package com.sicpa.standard.sasscl.controller.productionconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.controller.productionconfig.config.AbstractLayoutConfig;

/**
 * wrap a call to a IConfigurator<br>
 * helper class used the codingInitiator to be able to store the call to IConfigurator in order to call all of them
 * during the configuration step
 * 
 * @author DIelsch
 * 
 */
public class ConfigurationAction {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationAction.class);

	protected IConfigurator<AbstractLayoutConfig, Object> configurator;
	protected AbstractLayoutConfig config;
	protected Object configurable;

	public ConfigurationAction(IConfigurator<AbstractLayoutConfig, Object> configurator, AbstractLayoutConfig config,
			Object configurable) {
		this.configurator = configurator;
		this.config = config;
		this.configurable = configurable;
	}

	public void execute() throws ConfigurationFailedException {
		if (configurator != null) {
			configurator.execute(config, configurable);
		} else {
			logger.warn("no configurator for: " + configurable);
		}
	}
}