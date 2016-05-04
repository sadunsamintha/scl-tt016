package com.sicpa.standard.sasscl.view.monitoring.advanced;

import static com.sicpa.standard.client.common.utils.TaskExecutor.scheduleWithFixedDelay;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.isLineVariable;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class PlcMonitoringController implements IMonitoringViewListener {

	private static Logger logger = LoggerFactory.getLogger(PlcMonitoringController.class);

	private final List<IPlcVariable<?>> plcVars = new ArrayList<>();
	private PlcMonitoringModel model;
	private PlcProvider plcProvider;
	private int inputsReadDelay = 1000;
	private ScheduledFuture<?> scheduledTask;
	private int lineCount;

	public PlcMonitoringController() {
		model = new PlcMonitoringModel();
	}

	private void readPlcInput() {
		for (IPlcVariable<?> var : plcVars) {
			try {
				if (isLineVariable(var.getVariableName())) {
					readAllLinesVar(var);
				} else {
					readVar(var);
				}
			} catch (PlcAdaptorException e) {
				logger.error("failed to read " + var.getVariableName(), e);
			}
		}
		model.notifyModelChanged();
	}

	private void readAllLinesVar(IPlcVariable<?> var) throws PlcAdaptorException {
		for (int line = 1; line <= lineCount; line++) {
			IPlcVariable<?> lineVar = PlcLineHelper.clone(var, line);
			readVar(lineVar);
		}
	}

	private void readVar(IPlcVariable<?> var) throws PlcAdaptorException {
		try {
			String value = plcProvider.get().read(var) + "";
			model.put(var.getVariableName(), value);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private void activatePolling(boolean enabled) {
		model.setMonitoringActive(enabled);
		if (!enabled && scheduledTask != null) {
			scheduledTask.cancel(false);
		} else if (enabled) {
			scheduledTask = scheduleWithFixedDelay(() -> readPlcInput(), inputsReadDelay, MILLISECONDS, "readPlcInput");
		}
		model.notifyModelChanged();
	}

	@Override
	public void monitoringStateChanged(boolean enabled) {
		model.setMonitoringActive(enabled);
		activatePolling(enabled);
		model.notifyModelChanged();
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}

	public void setPlcVars(List<IPlcVariable<?>> plcVars) {
		this.plcVars.addAll(plcVars);
	}

	public void setInputsReadDelay(int inputsReadDelay) {
		this.inputsReadDelay = inputsReadDelay;
	}

	public PlcMonitoringModel getModel() {
		return model;
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}
}
