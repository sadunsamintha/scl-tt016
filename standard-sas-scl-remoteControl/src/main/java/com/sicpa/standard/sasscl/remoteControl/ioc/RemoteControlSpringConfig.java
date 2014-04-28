package com.sicpa.standard.sasscl.remoteControl.ioc;

import com.sicpa.standard.client.common.ioc.AbstractSpringConfig;

public class RemoteControlSpringConfig extends AbstractSpringConfig {

	public static final String BEANS = "beans";
	public static final String PROPERTY_PLACE_HOLDER_CONFIGURER = "propertyPlaceholderConfigurer";

	public RemoteControlSpringConfig() {
		this.config.put(BEANS, "spring/beans.xml");
		this.config.put(PROPERTY_PLACE_HOLDER_CONFIGURER, "spring/propertyPlaceholderConfigurer.xml");
	}

}
