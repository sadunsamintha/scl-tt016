package com.sicpa.standard.sasscl.devices.brs;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_CONNECTED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_EXIT;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STOPPING;
import static com.sicpa.standard.sasscl.devices.brs.BrsPlcRequest.LINE_DISABLE;
import static com.sicpa.standard.sasscl.devices.brs.BrsPlcRequest.LINE_ENABLE;
import static com.sicpa.standard.sasscl.devices.brs.BrsPlcRequest.RESET_EXPECTED_SKU;
import static com.sicpa.standard.sasscl.devices.brs.BrsPlcRequest.SET_EXPECTED_SKU;
import static com.sicpa.standard.sasscl.devices.brs.BrsPlcRequest.SET_SKU_CHECK_MODE;
import static com.sicpa.standard.sasscl.devices.plc.PlcRequest.RELOAD_PLC_PARAM;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.common.util.ThreadUtils;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class BrsStateListener {

	private static final Logger logger = LoggerFactory.getLogger(BrsStateListener.class);

	protected Map<BrsPlcRequest, IPlcVariable<?>> brsRequestMap;

	protected IPlcAdaptor plcAdaptor;

	protected final AtomicBoolean started = new AtomicBoolean(false);

	protected String versionVarName;

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

			logger.error(MessageFormat.format("[BRS] Problem with BRS PLC communication in state {} : {}", evt
					.getCurrentState().getName(), e.getMessage()), e);
			EventBusService.post(new MessageEvent(plcAdaptor, MessageEventKey.BRS.BRS_PLC_COMMUNICATION_PROBLEM, e
					.getMessage()));
		}
	}

	protected void onStateStopping() throws PlcAdaptorException {
		logger.debug("[BRS] Enter onStateStopping.");

		if (plcAdaptor.isConnected()) {
			plcAdaptor.write(brsRequestMap.get(RESET_EXPECTED_SKU));
			plcAdaptor.write(brsRequestMap.get(LINE_DISABLE));
		}

		setStarted(false);
	}

	protected void setStarted(boolean started) {

		this.started.compareAndSet(!started, started);
	}

	protected void onStateStarted() throws PlcAdaptorException {

		logger.debug("[BRS] Enter onStateStarted.");

		plcAdaptor.write(brsRequestMap.get(SET_EXPECTED_SKU));
		plcAdaptor.write(brsRequestMap.get(SET_SKU_CHECK_MODE));
		plcAdaptor.executeRequest(RELOAD_PLC_PARAM);

		// TODO: workaround with PLC problem. PLC still has not received all the
		// parameters when line_enable is sent.
		ThreadUtils.sleepQuietly(2000);

		plcAdaptor.write(brsRequestMap.get(LINE_ENABLE));

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

	public void setBrsRequestMap(Map<BrsPlcRequest, IPlcVariable<?>> brsRequestMap) {
		this.brsRequestMap = brsRequestMap;
	}

	public void setVersionVarName(String versionVarName) {
		this.versionVarName = versionVarName;
	}

	
}
