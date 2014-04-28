package com.sicpa.standard.sasscl.monitoring.system.event;

import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.monitoring.system.AbstractSystemEvent;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;

public class ProductionParametersSystemEvent extends AbstractSystemEvent {

	private static final long serialVersionUID = 1L;

	protected ProductionParameters message;

	public ProductionParametersSystemEvent(ProductionParameters message) {
		super(SystemEventLevel.INFO, SystemEventType.SELECT_PROD_PARAMETERS);
		this.message = message;
	}

	public ProductionParameters getMessage() {
		return message;
	}

}
