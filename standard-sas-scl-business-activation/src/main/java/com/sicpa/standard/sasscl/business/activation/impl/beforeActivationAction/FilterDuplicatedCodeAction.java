package com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction;

import static com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction.BeforeActivationResult.createBeforeActivationResultFiltered;
import static com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction.BeforeActivationResult.createBeforeActivationResultNotFiltered;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTING;
import static com.sicpa.standard.sasscl.model.ProductionMode.EXPORT;
import static com.sicpa.standard.sasscl.model.ProductionMode.MAINTENANCE;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class FilterDuplicatedCodeAction extends AbstractBeforeActivationAction {

	private static final Logger logger = LoggerFactory.getLogger(FilterDuplicatedCodeAction.class);

	private Map<String, String> previousCodeByCamera = new HashMap<>();
	private ProductionParameters productionParameters;

	@Override
	public BeforeActivationResult internalReceivedCode(Code code, boolean good, String cameraName) {
		if (!isEnabled()) {
			return createBeforeActivationResultNotFiltered(code, good);
		}

		logger.debug("Code received at {} = {} , Is good code = {}", new Object[] { cameraName, code.getStringCode(),
				good });
		if (good && isSameAsPreviousCode(code, cameraName)) {
			return createBeforeActivationResultFiltered(code, good);
		} else {
			previousCodeByCamera.put(cameraName, code.getStringCode());
			return createBeforeActivationResultNotFiltered(code, good);
		}
	}

	private boolean isSameAsPreviousCode(Code code, String cameraName) {
		String previousCode = previousCodeByCamera.get(cameraName);
		return previousCode != null && previousCode.equals(code.getStringCode());
	}

	@Subscribe
	public void resetCodesOnStart(ApplicationFlowStateChangedEvent evt) {
		ApplicationFlowState currentState = evt.getCurrentState();
		if (currentState.equals(STT_STARTING)) {
			previousCodeByCamera.clear();
		}
	}

	private boolean isEnabled() {
		if (productionParameters.getProductionMode().equals(MAINTENANCE)
				|| productionParameters.getProductionMode().equals(EXPORT)) {
			return false;
		}
		return true;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}
}
