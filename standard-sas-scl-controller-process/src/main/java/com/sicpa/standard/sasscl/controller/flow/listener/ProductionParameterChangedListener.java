package com.sicpa.standard.sasscl.controller.flow.listener;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.event.ProductionParametersSystemEvent;

public class ProductionParameterChangedListener {

	protected IStorage storage;

	protected IFlowControl flowControl;

	@Subscribe
	public void setProductionParameters(ProductionParametersEvent evt) {
		if (evt.getProductionParameters() != null) {
			storage.saveSelectedProductionParamters(evt.getProductionParameters());
			MonitoringService.addSystemEvent(new ProductionParametersSystemEvent(evt.getProductionParameters()));
			flowControl.notifyProductionParameterSelected();
		}
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}

	public void setFlowControl(IFlowControl flowControl) {
		this.flowControl = flowControl;
	}

}
