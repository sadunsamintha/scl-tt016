package com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class FilterDuplicatedCodeAction extends AbstractBeforeActivationAction {

    private static final Logger logger = LoggerFactory.getLogger(FilterDuplicatedCodeAction.class);

    /**
     * map< cameraName , previousCode >
     */
    private Map<String, String> previousCodeMap = new HashMap<String, String>();

    private ProductionParameters productionParameters;

    @Override
    public BeforeActivationResult internalReceivedCode(final Code code, final boolean good, String cameraName) {

        if (isEnabled()) {
            logger.debug("Code received at {} = {} , Is good code = {}", new Object[]{cameraName, code.getStringCode(), good});

            if (good) {
                String previousCode = previousCodeMap.get(cameraName);

                if (previousCode != null && previousCode.equals(code.getStringCode())) {
                    return new BeforeActivationResult(code, good, true);
                }
                previousCodeMap.put(cameraName, code.getStringCode());
            }
        }
        return new BeforeActivationResult(code, good, false);

    }

    @Subscribe
    public void resetCodesOnStart(ApplicationFlowStateChangedEvent evt) {
        ApplicationFlowState currentState = evt.getCurrentState();
        if (currentState.equals(ApplicationFlowState.STT_STARTING)) {
            previousCodeMap.clear();
        }
    }

    private boolean isEnabled() {
        if (productionParameters.getProductionMode().equals(ProductionMode.MAINTENANCE) ||
                productionParameters.getProductionMode().equals(ProductionMode.EXPORT)) {
            return false;
        }
        return true;
    }

    public void setProductionParameters(ProductionParameters productionParameters) {
        this.productionParameters = productionParameters;
    }
}
