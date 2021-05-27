package com.sicpa.tt085.view.selection.select;

import com.sicpa.standard.sasscl.view.selection.select.SelectProductionParametersBarcodeView;
import com.sicpa.standard.sasscl.view.selection.select.barcode.BarcodeInputView;
import com.sicpa.tt085.view.selection.select.barcode.TT085BarcodeInputView;

public class TT085SelectProductionParametersBarcodeView extends SelectProductionParametersBarcodeView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public BarcodeInputView getDelegate() {
		if (delegate == null) {
			delegate = new TT085BarcodeInputView(callback);
			delegate.reset(skuListProvider.get());
		}
		return delegate;
	}

}
