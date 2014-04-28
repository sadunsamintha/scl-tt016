package com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class FilterDuplicatedCodeAction extends AbstractBeforeActivationAction {

	private static final Logger logger = LoggerFactory.getLogger(FilterDuplicatedCodeAction.class);

	/**
	 * map< cameraName , previousCode >
	 */
	protected Map<String, String> previousCodeMap = new HashMap<String, String>();

	protected ProductionParameters productionParameters;

	@Override
	protected BeforeActivationResult internalReceivedCode(final Code code, final boolean good, String cameraName) {

		if (isEnabled()) {

			logger.debug("Code received at {} = {} , Is good code = {}",
					new Object[] { cameraName, code.getStringCode(), good });

			if (good) {

				String previousCode = previousCodeMap.get(cameraName);

				if (previousCode != null) {
					if (previousCode.equals(code.getStringCode())) {
						return new BeforeActivationResult(code, good, true);
					}
				}
				previousCodeMap.put(cameraName, code.getStringCode());
			}
		}
		return new BeforeActivationResult(code, good, false);

	}

	protected boolean isEnabled() {
		if (productionParameters.getProductionMode().equals(ProductionMode.MAINTENANCE)) {
			return false;
		}
		return true;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}
}
