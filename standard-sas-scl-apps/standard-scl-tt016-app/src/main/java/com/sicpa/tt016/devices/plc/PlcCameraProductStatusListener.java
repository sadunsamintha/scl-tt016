package com.sicpa.tt016.devices.plc;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.plc.IPlcListener;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTING;

public class PlcCameraProductStatusListener implements IPlcListener {

	private final static Logger logger = LoggerFactory.getLogger(PlcCameraProductStatusListener.class);

	private PlcProvider plcProvider;
	private String plcCameraProductStatusNtfVarName;
	private AtomicBoolean isProductionStartingOrStarted = new AtomicBoolean(false);

	public void init() {
		plcProvider.addChangeListener(evt -> plcProvider.get().addPlcListener(this));
	}

	@Override
	public void onPlcEvent(PlcEvent event) {
		if (isProductionStartingOrStarted.get()) {
			logger.debug("PLC camera result received: {}", Integer.toHexString((Integer) event.getValue()));

			EventBusService.post(PlcCameraResultParser.getPlcCameraResultEvent((Integer) event.getValue()));
		}
	}

	@Subscribe
	public void onProductionStarted(ApplicationFlowStateChangedEvent event) {
		isProductionStartingOrStarted.set(event.getCurrentState().equals(STT_STARTING)
				|| event.getCurrentState().equals(STT_STARTED));
	}

	@Override
	public List<String> getListeningVariables() {
		return PlcLineHelper.getLinesVariableName(plcCameraProductStatusNtfVarName);
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}

	public void setPlcCameraProductStatusNtfVarName(String plcCameraProductStatusNtfVarName) {
		this.plcCameraProductStatusNtfVarName = plcCameraProductStatusNtfVarName;
	}
}
