package com.sicpa.standard.sasscl.devices.plc;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.*;

public class PlcBrsStateListener {

	private static final Logger logger = LoggerFactory.getLogger(PlcBrsStateListener.class);

	protected Map<PlcRequest, IPlcVariable<?>> brsRequestMap;

	protected IPlcAdaptor plcAdaptor;

	protected final AtomicBoolean started = new AtomicBoolean(false);

	protected String versionVarName;

	protected ProductionParameters productionParameters;

	@Subscribe
	public void processStateChanged(ApplicationFlowStateChangedEvent evt) {

		try {
			if (evt.getCurrentState() == STT_CONNECTED) {

				onStateReady();
				return;
			}
			if (evt.getCurrentState() == STT_EXIT) {

				onStateExitApplication();
				return;
			}
			if (evt.getCurrentState() == STT_STARTED) {

				onStateStarted();
				return;
			}
			if (evt.getCurrentState() == STT_STOPPING) {

				onStateStopping();
			}
		} catch (PlcAdaptorException e) {

			logger.error(MessageFormat.format("[BRS] Problem with BRS PLC communication in state {0} : {1}", evt
					.getCurrentState().getName(), e.getMessage()), e);
			EventBusService.post(new MessageEvent(plcAdaptor, MessageEventKey.BRS.BRS_PLC_COMMUNICATION_PROBLEM, e
					.getMessage()));
		}
	}

	protected void onStateStopping() throws PlcAdaptorException {
		logger.debug("[BRS] Enter onStateStopping.");

		if (plcAdaptor.isConnected()) {
			plcAdaptor.write(brsRequestMap.get(BrsPlcRequest.RESET_EXPECTED_SKU));
			plcAdaptor.write(brsRequestMap.get(BrsPlcRequest.LINE_DISABLE));
		}

		setStarted(false);
	}

	protected void setStarted(boolean started) {

		this.started.compareAndSet(!started, started);
	}

	protected void onStateStarted() throws PlcAdaptorException {

		logger.debug("[BRS] Enter onStateStarted.");

		if (productionParameters != null && !productionParameters.getProductionMode().equals(ProductionMode.EXPORT)) {
			plcAdaptor.write(brsRequestMap.get(BrsPlcRequest.LINE_ENABLE));
		}

		setStarted(true);

	}

	protected void onStateExitApplication() throws PlcAdaptorException {
		logger.debug("[BRS] Enter onStateExitApplication.");
	}

	protected void onStateReady() throws PlcAdaptorException {
		logger.debug("[BRS] Enter onStateReady.");
		readBrsPlcVersion();
	}

	protected void readBrsPlcVersion() {
		IPlcVariable<String> var = PlcVariable.createStringVar(versionVarName, 50);
		try {
			String version = plcAdaptor.read(var);
			logger.info("BRS version:" + version);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public boolean isStopped() {
		return !started.get();
	}

	public void setPlcAdaptor(IPlcAdaptor plcAdaptor) {
		this.plcAdaptor = plcAdaptor;
	}

	public void setBrsRequestMap(Map<PlcRequest, IPlcVariable<?>> brsRequestMap) {
		this.brsRequestMap = brsRequestMap;
	}

	public void setVersionVarName(String versionVarName) {
		this.versionVarName = versionVarName;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	@Subscribe
	public void updateParameters(ProductionParametersEvent evt) {
		if (evt.getProductionParameters() != null) {
			setProductionParameters(evt.getProductionParameters());
		}
	}

	public void sendSkuConfig() throws PlcAdaptorException {
		plcAdaptor.write(brsRequestMap.get(BrsPlcRequest.SET_EXPECTED_SKU));
		plcAdaptor.write(brsRequestMap.get(BrsPlcRequest.SET_SKU_CHECK_MODE));
	}

}
