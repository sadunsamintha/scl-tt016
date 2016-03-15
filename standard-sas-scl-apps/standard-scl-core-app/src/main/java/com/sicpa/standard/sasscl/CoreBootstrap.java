package com.sicpa.standard.sasscl;

import static com.sicpa.standard.sasscl.model.ProductionMode.EXPORT;

import com.sicpa.standard.sasscl.business.alert.task.AbstractAlertTask;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.devices.camera.alert.CameraCountAlertTask;

public class CoreBootstrap extends Bootstrap {

	@Override
	public void executeSpringInitTasks() {
		super.executeSpringInitTasks();

		CustoBuilder.setAlertEnabler((task) -> isTaskEnabled(task), "");

	}

	private boolean isTaskEnabled(AbstractAlertTask t) {
		CameraCountAlertTask task = (CameraCountAlertTask) t;
		if (task.getModel().isEnabled()) {
			return productionParameters.getProductionMode() == EXPORT;
		}
		return false;
	}

}
