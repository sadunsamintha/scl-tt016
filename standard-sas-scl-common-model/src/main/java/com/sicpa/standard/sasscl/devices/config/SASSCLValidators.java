package com.sicpa.standard.sasscl.devices.config;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sicpa.standard.client.common.descriptor.validator.IValidator;
import com.sicpa.standard.client.common.descriptor.validator.Validators;

public class SASSCLValidators extends Validators {

	public SASSCLValidators() {
		super();
	}

	public SASSCLValidators(final Entry<Object, List<IValidator>>... args) {
		super(args);
	}

	public void addValidator(Map<Object, List<IValidator>> mapValidators) {
		for (Entry<Object, List<IValidator>> entry : mapValidators.entrySet()) {
			this.addValidator(entry);
		}
	}

}
