package com.sicpa.standard.sasscl.business.activation;

import static com.sicpa.standard.sasscl.model.ProductStatus.OFFLINE;
import static com.sicpa.standard.sasscl.model.ProductStatus.SENT_TO_PRINTER_WASTED;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.business.alert.task.model.PlcActivationCounterCheckModel;
import com.sicpa.standard.sasscl.business.alert.task.scheduled.AbstractScheduledAlertTask;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class PlcActivationCounterCheck extends AbstractScheduledAlertTask {

	private static Logger logger = LoggerFactory.getLogger(PlcActivationCounterCheck.class);

	private PlcProvider plcProvider;
	private String productFreqVarName;
	private int executionDelay;
	private PlcActivationCounterCheckModel model;

	private final AtomicInteger counterFromActivation = new AtomicInteger();

	private int getProductCountFromPlc() throws PlcAdaptorException {
		List<String> productCounterVariables = PlcLineHelper.getLinesVariableName(productFreqVarName);
		int res = 0;
		for (String var : productCounterVariables) {
			int val = readVar(var);
			res += val;
		}
		return res;
	}

	private int readVar(String var) throws PlcAdaptorException {
		int res = plcProvider.get().read(PlcVariable.createInt32Var(var));
		return res;
	}

	@Subscribe
	public void notifyNewProduct(NewProductEvent evt) {
		if (model.isEnabled()) {
			if (acceptProduct(evt.getProduct())) {
				counterFromActivation.incrementAndGet();
			}
		}
	}

	private boolean acceptProduct(Product p) {
		return !asList(SENT_TO_PRINTER_WASTED, OFFLINE).contains(p.getStatus());
	}

	@Override
	public void reset() {
		counterFromActivation.set(0);
	}

	@Override
	protected MessageEvent getAlertMessage() {
		return new MessageEvent(MessageEventKey.Alert.PLC_ACTIVATION_CROSS_CHECK_FAILED);
	}

	@Override
	protected boolean isAlertPresent() {

		try {
			int totalFromPlc = getProductCountFromPlc();
			if (Math.abs(totalFromPlc - counterFromActivation.get()) > getModel().getMaxDelta()) {
				logger.error("counter from plc={}, counter from the activation={}", totalFromPlc,
						counterFromActivation.get());
				return true;
			}
		} catch (PlcAdaptorException e) {
			logger.error("", e);
		}
		return false;
	}

	@Override
	public String getAlertName() {
		return "plc activation cross button";
	}

	public void setModel(PlcActivationCounterCheckModel model) {
		this.model = model;
	}

	public PlcActivationCounterCheckModel getModel() {
		return model;
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}

	public void setProductFreqVarName(String productFreqVarName) {
		this.productFreqVarName = productFreqVarName;
	}

	@Override
	protected boolean isEnabledDefaultImpl() {
		return model.isEnabled();
	}

	public void setExecutionDelay(int executionDelay) {
		this.executionDelay = executionDelay;
	}

	@Override
	public long getDelay() {
		return executionDelay;
	}
}
