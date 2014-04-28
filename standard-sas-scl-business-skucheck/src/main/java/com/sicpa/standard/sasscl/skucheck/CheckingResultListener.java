package com.sicpa.standard.sasscl.skucheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.skucheck.checking.CheckingResultEvent;
import com.sicpa.standard.sasscl.skucheck.checking.ICheckingResultListener;

public class CheckingResultListener implements ICheckingResultListener {

	private static final Logger logger = LoggerFactory.getLogger(CheckingResultListener.class);

	public CheckingResultListener() {
	}

	@Override
	public void checkComplete(CheckingResultEvent evt) {
		logger.debug("sku checking result {}", evt);
		switch (evt.getResultType()) {
		case UNKNOWN_SKU:
			fireMessage(MessageEventKey.SkuCheck.UNKOWN_SKU, evt.getCodes().get(0).getCode(), evt.getSelectedSku());
			break;
		case WRONG_SKU_CRITICAL:
			fireMessage(MessageEventKey.SkuCheck.WRONG_SKU_CRITICAL, evt.getCodes().get(0).getCode(), evt.getSelectedSku());
			break;
		case WRONG_SKU_SMALL:
			fireMessage(MessageEventKey.SkuCheck.WRONG_SKU_SMALL, evt.getCodes().get(0).getCode(), evt.getSelectedSku());
			break;
        case OK:
            fireMessage(MessageEventKey.SkuCheck.OK, evt.getCodes().get(0).getCode(), evt.getSelectedSku());
            break;
        case UNREAD:
            fireMessage(MessageEventKey.SkuCheck.UNREAD, evt.getCodes(), evt.getSelectedSku());
            break;
        default:
            throw new IllegalStateException("Match type not supported");
		}
	}

	public void fireMessage(String key, Object... params) {
		EventBusService.post(new MessageEvent(this, key, params));
	}
}
