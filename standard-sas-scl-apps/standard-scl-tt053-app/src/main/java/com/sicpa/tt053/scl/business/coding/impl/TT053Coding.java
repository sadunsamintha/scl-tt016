package com.sicpa.tt053.scl.business.coding.impl;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.sasscl.business.coding.ICodeReceiver;
import com.sicpa.standard.sasscl.business.coding.impl.Coding;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;

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
				askCodeExport(number, target);
			}else{
				super.askCodes(number, target);
			}
		}
	}
	
	private void askCodeExport(final int number, ICodeReceiver target) {
		SKU sku = productionParameters.getSku();
		if (sku != null) {
			CodeType codeType = sku.getCodeType();
			List<String> codes = new ArrayList<String>();
			for(int i=0; i<number; i++){
				codes.add(new String("EXP"));
			}
			sendCodeToPrinter(codes, target, null, codeType);
		}
	}
}