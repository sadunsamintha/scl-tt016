package com.sicpa.standard.sasscl.devices.brs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.skucheck.checking.CheckingResultEvent;
import com.sicpa.standard.sasscl.skucheck.checking.ICheckingResultListener;

/**
 * As there is no other action done besides displaying some warning in the GUI
 * driven by the default SkuCheck implementation. The purpose of this class is
 * to log in info level the results of the SkuCheck events fired by the BRS
 * system.
 * 
 * @author JPerez
 * 
 */
public class SkuCheckResultListenerLogger implements ICheckingResultListener {

	private static final Logger logger = LoggerFactory.getLogger(SkuCheckResultListenerLogger.class);

	@Override
	public void checkComplete(CheckingResultEvent evt) {
		logger.debug("sku checking result {}", evt);
		switch (evt.getResultType()) {
		case UNKNOWN_SKU:
			logger.info("[BRS] SkuCheck result for {}: UNKNOWN; Expected SKU: {}", evt.getCodes().get(0).getCode(),
					evt.getSelectedSku());
			break;
		case WRONG_SKU_CRITICAL:
		case WRONG_SKU_SMALL:
			logger.info("[BRS] SkuCheck result for {}: WRONG_SKU; Expected SKU: {}", evt.getCodes().get(0).getCode(),
					evt.getSelectedSku());
			break;

		case OK:
			logger.info("[BRS] SkuCheck result for {}: OK; Expected SKU: {}", evt.getCodes().get(0).getCode(),
					evt.getSelectedSku());
			break;
		case UNREAD:
			logger.info("[BRS] SkuCheck result for {}: UNREAD; Expected SKU: {}", evt.getCodes().get(0).getCode(),
					evt.getSelectedSku());
			break;
		default:
			throw new IllegalStateException("Match type not supported");
		}
	}
}
