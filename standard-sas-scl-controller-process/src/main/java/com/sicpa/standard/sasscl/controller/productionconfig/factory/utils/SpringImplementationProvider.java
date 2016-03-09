package com.sicpa.standard.sasscl.controller.productionconfig.factory.utils;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.sasscl.controller.productionconfig.IImplementationProvider;

public class SpringImplementationProvider implements IImplementationProvider {

	private Map<String, Object> localImplementations = new HashMap<>();

	@Override
	public Object getImplementation(String beanName) {

		Object res = localImplementations.get(beanName);
		if (res == null) {
			res = BeanProvider.getBean(beanName);
		}
		return res;
	}

	public void addLocalImplementation(String beanName, Object implementation) {
		localImplementations.put(beanName, implementation);
	}

}
