package com.sicpa.standard.sasscl.controller.skuselection.partial;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STOPPING;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.controller.skuselection.ISkuSelectionBehavior;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public abstract class AbstractSkuSelectionDuringProduction implements ISkuSelectionBehavior {
	private IFlowControl flowControl;
	private ProductionParameters productionParameters;

	@Override
	public boolean isLoadPreviousSelection() {
		return false;
	}

	@Override
	public void stopProduction() {
		flowControl.notifyStopProduction();
	}

	@Subscribe
	public void handleApplicationStateChanged(ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState().equals(STT_STOPPING)) {
			flowControl.notifyEnterSelectionScreen();
		}
	}

	@Override
	public void duringProductionOnProductionParameterChanged(ProductionParametersEvent evt) {
		productionParameters.setSku(evt.getProductionParameters().getSku());
		EventBusService.post(evt);
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

}
