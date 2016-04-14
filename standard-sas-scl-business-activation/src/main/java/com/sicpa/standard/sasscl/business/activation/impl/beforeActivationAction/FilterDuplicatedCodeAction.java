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

import static com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction.BeforeActivationResult.createBeforeActivationResultFiltered;
import static com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction.BeforeActivationResult.createBeforeActivationResultNotFiltered;

public class FilterDuplicatedCodeAction extends AbstractBeforeActivationAction {

    private static final Logger logger = LoggerFactory.getLogger(FilterDuplicatedCodeAction.class);

    /**
     * map< cameraName , previousCode >
     */
    private Map<String, String> previousCodeMap = new HashMap<String, String>();

    private ProductionParameters productionParameters;


    @Override
    public BeforeActivationResult internalReceivedCode(final Code code, final boolean good, String cameraName) {
        if (!isEnabled()) {
            return createBeforeActivationResultNotFiltered(code, good);
        }

        logger.debug("Code received at {} = {} , Is good code = {}", new Object[]{cameraName, code.getStringCode(), good});
        if (good && isSameAsPreviousCode(code, cameraName)) {
            return  createBeforeActivationResultFiltered(code, good);
        } else {
            previousCodeMap.put(cameraName, code.getStringCode());
            return  createBeforeActivationResultNotFiltered(code, good);
        }
    }

    private boolean isSameAsPreviousCode(Code code, String cameraName) {
        String previousCode = previousCodeMap.get(cameraName);
        return previousCode != null && previousCode.equals(code.getStringCode());
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
