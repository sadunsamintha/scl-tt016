package com.sicpa.standard.sasscl.business.activation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.alert.task.AbstractAlertTask;
import com.sicpa.standard.sasscl.business.alert.task.model.PlcActivationCounterCheckModel;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.plc.IPlcListener;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class PlcActivationCounterCheck extends AbstractAlertTask implements IPlcListener {

	private static Logger logger = LoggerFactory.getLogger(PlcActivationCounterCheck.class);

	private String productFreqVarName;
	protected PlcActivationCounterCheckModel model;

	protected final Map<String, Integer> plcCounterMap = new HashMap<String, Integer>();
	protected Integer totalCount = 0;
	protected boolean plcCounterReset = false;
	protected final AtomicInteger counterFromActivation = new AtomicInteger();

	public PlcActivationCounterCheck() {
	}

	public void onPlcEvent(PlcEvent event) {

		// THIS IS A BAD IDEA TO DO THAT FOR EACH NOTIFICATION
		// TOO MANY NOTIFICATION BETWEEN PLC AND JAVA FOR A HIGH SPEED LINE
		// THIS MODULE IS NOT ACTIVE BY DEFAULT

		List<String> productCounterVariables = PlcLineHelper.getLinesVariableName(productFreqVarName);

		if (productCounterVariables == null || productCounterVariables.size() == 0)
			return;

		if (!productCounterVariables.contains(event.getVarName()))
			return;

		plcCounterMap.put(event.getVarName(), (Integer) event.getValue());

		try {

			calculateTotalPlcCount();

			if (totalCount == 0 || totalCount == 1) {
				plcCounterReset = true;
			}
			if (plcCounterReset) {
				// only button if the counter has been reset once (only handle notification when the plc has been
				// started)
				checkForMessage();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * this is the handle multiconveyor
	 * 
	 * @return
	 */
	public void calculateTotalPlcCount() {
		totalCount = 0;
		for (Entry<String, Integer> entry : plcCounterMap.entrySet()) {
			totalCount = totalCount + entry.getValue();
		}
	}

	@Subscribe
	public void notifyNewProduct(NewProductEvent evt) {
		counterFromActivation.incrementAndGet();
	}

	@Override
	public void reset() {

	}

	@Override
	protected MessageEvent getAlertMessage() {
		return new MessageEvent(MessageEventKey.Alert.PLC_ACTIVATION_CROSS_CHECK_FAILED);
	}

	@Override
	protected boolean isAlertPresent() {
		if (Math.abs(totalCount - counterFromActivation.get()) > getModel().getMaxDelta()) {
			logger.error("counter from plc={}, counter from the activation={}", totalCount, counterFromActivation.get());
			return true;
		}
		return false;
	}

	@Override
	public String getAlertName() {
		return "plc activation cross button";
	}

	@Override
	protected boolean isEnabled() {
		return model.isEnabled();
	}

	public void setModel(PlcActivationCounterCheckModel model) {
		this.model = model;
	}

	public PlcActivationCounterCheckModel getModel() {
		return model;
	}

	@Override
	public List<String> getListeningVariables() {
		return null;
	}

	public void setPlcProvider(final PlcProvider plcProvider) {
		plcProvider.addChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				plcProvider.get().addPlcListener(PlcActivationCounterCheck.this);
			}
		});
	}

	// Products trigs counter is reset in PLC at each start also
	@Subscribe
	public void handleApplicationStateChange(ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState().equals(ApplicationFlowState.STT_STARTING)) {
			counterFromActivation.set(0);
		}
	}

	public void setProductFreqVarName(String productFreqVarName) {
		this.productFreqVarName = productFreqVarName;
	}
}
