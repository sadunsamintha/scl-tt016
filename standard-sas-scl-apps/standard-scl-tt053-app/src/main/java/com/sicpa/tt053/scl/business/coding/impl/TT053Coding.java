package com.sicpa.tt053.scl.business.coding.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.coding.ICodeReceiver;
import com.sicpa.standard.sasscl.business.coding.impl.Coding;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;

/**
 * <p>
 * Provide codes to the printer when the application is running in SCL mode.
 * </p>
 * <p>
 * It implements the <code>IPrinterListener</code> interface so it can be notified whenever the printer requests for
 * codes.
 * </p>
 *
 */
public class TT053Coding extends Coding {

	private static final Logger logger = LoggerFactory.getLogger(TT053Coding.class);
	
	public TT053Coding() {
		super();
	}

	public TT053Coding(final IStorage storage) {
		super(storage);
	}
	
	@Override
	public void askCodes(final int number, ICodeReceiver target) {
		synchronized (askCodeLock) {
			if(productionParameters.getProductionMode().equals(ProductionMode.EXPORT)){
				SKU sku = productionParameters.getSku();
				if (sku != null) {
					CodeType codeType = sku.getCodeType();
					// codes to be sent to the printer
					List<String> codes = new ArrayList<String>();
					for(int i=0; i<number; i++){
						codes.add(new String("EXP"));
					}
					
					sendCodeToPrinter(codes, target, null, codeType);
				}
			}else{
			
				IEncoder encoder;
				SKU sku = productionParameters.getSku();
				if (sku != null) {
					CodeType codeType = sku.getCodeType();
	
					encoder = getEncoderToUse(false);
	
					// Storage ran out of encoders.
					if (encoder == null) {
						EventBusService.post(new MessageEvent(MessageEventKey.Coding.ERROR_NO_ENCODERS_IN_STORAGE));
						return;
					}
	
					// codes to be sent to the printer
					List<String> codes = new ArrayList<String>();
	
					encoder = retreiveEnoughCodesToPrint(encoder, codes, number);
	
					sendCodeToPrinter(codes, target, encoder, codeType);
				}
			}
		}
	}
}