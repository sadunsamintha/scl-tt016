package com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.type.impl;

import com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.type.ICameraJobParameter;


public abstract class AliasCameraJobParameter<E> implements ICameraJobParameter {

	private String alias;
	private E parameterValue;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public E getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(E parameterValue) {
		this.parameterValue = parameterValue;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " {alias='" + alias + '\'' + ", parameterValue=" + parameterValue
				+ '}';
	}
}
