package com.sicpa.standard.sasscl.view.selection.select.barcode;

import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.view.selection.select.ISelectProductionParametersViewListener;
import com.sicpa.tt085.model.provider.CountryProvider;


public class TT085BarcodeInputView extends BarcodeInputView implements CountryProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public TT085BarcodeInputView(ISelectProductionParametersViewListener callback) {
		super(callback);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void selectProductionParameters(final SKU sku, final ProductionMode mode, final String barcode) {
		getMainFrame().resetMainPanel();
		getPanelSelect().setVisible(false);
		getLabelCorrespondingSKU().setVisible(false);
		ProductionParameters pp = new ProductionParameters(mode, sku, barcode);
		CustoBuilder.addPropertyToClass(ProductionParameters.class, country);
		pp.setProperty(country, null); // TO DO: Need to be implement
		callback.productionParametersSelected(pp);
	}

}
