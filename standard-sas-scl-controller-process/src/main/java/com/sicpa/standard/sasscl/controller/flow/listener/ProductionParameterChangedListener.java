package com.sicpa.standard.sasscl.controller.flow.listener;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTED;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.event.ProductionParametersSystemEvent;

public class ProductionParameterChangedListener {

	private IStorage storage;
	private IFlowControl flowControl;

	@Subscribe
	public void setProductionParameters(ProductionParametersEvent evt) {
		if (!isChangedDuringProduction()) {
			if (evt.getProductionParameters() != null) {
				storage.saveSelectedProductionParamters(evt.getProductionParameters());
				MonitoringService.addSystemEvent(new ProductionParametersSystemEvent(evt.getProductionParameters()));
				flowControl.notifyProductionParameterSelected();
			}
		}
	}

	private boolean isChangedDuringProduction() {
		return flowControl.getCurrentState().equals(STT_STARTED);
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}

	public void setFlowControl(IFlowControl flowControl) {
		this.flowControl = flowControl;
	}

}
