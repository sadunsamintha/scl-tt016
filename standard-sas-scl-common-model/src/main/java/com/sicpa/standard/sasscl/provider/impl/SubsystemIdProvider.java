package com.sicpa.standard.sasscl.provider.impl;

import com.sicpa.standard.client.common.provider.AbstractProvider;

public class SubsystemIdProvider extends AbstractProvider<Long> {

	public SubsystemIdProvider(long value) {
		super("subsystemId");
		set(value);
	}
}
