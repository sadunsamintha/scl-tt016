package com.sicpa.standard.sasscl.view.forceCall;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanCall implements IBeanCall {

	private static Logger logger= LoggerFactory.getLogger(BeanCall.class);

	private Object bean;
	private String methodName;
	private String description;

	public BeanCall(Object bean, String methodName, String description) {
		this.bean = bean;
		this.methodName = methodName;
		this.description = description;
	}

	public BeanCall() {
	}

	public void run() {
		try {
			Method m = bean.getClass().getMethod(methodName);
			m.invoke(bean);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
