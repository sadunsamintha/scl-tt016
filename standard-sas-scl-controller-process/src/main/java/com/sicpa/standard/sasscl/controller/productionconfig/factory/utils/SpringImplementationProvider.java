package com.sicpa.standard.sasscl.controller.productionconfig.factory.utils;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.sasscl.controller.productionconfig.IImplementationProvider;

public class SpringImplementationProvider implements IImplementationProvider {

	@Override
	public Object getImplementation(String beanName) {
		return BeanProvider.getBean(beanName);
	}

}
