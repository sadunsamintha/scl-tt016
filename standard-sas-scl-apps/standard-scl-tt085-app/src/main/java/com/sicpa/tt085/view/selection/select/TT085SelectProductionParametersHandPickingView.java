package com.sicpa.tt085.view.selection.select;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.SelectionFlowEvent;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.view.selection.select.SelectProductionParametersHandPickingView;
import com.sicpa.tt085.model.TT085Country;
import com.sicpa.tt085.model.provider.CountryProvider;


public class TT085SelectProductionParametersHandPickingView extends SelectProductionParametersHandPickingView implements CountryProvider{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void selectionCompleteCallBack(SelectionFlowEvent evt) {
		ProductionMode mode = getProductionMode(evt);
		SKU sku = getSkuMode(evt);
		TT085Country countryObj = getCountry(evt);

		if (mode != null) {
			OperatorLogger.log("Product Mode: {}", mode.getDescription());
		}
		if (sku != null) {
			OperatorLogger.log("Product Param: {}", sku.getDescription());
		}

		ProductionParameters pp = new ProductionParameters(mode, sku, "");
		CustoBuilder.addPropertyToClass(ProductionParameters.class, country);
		pp.setProperty(country, countryObj);
		callback.productionParametersSelected(pp);
	}
	
	private TT085Country getCountry(SelectionFlowEvent evt) {
		for (SelectableItem item : evt.getItems()) {
			if (item instanceof IProductionParametersNode) {
				IProductionParametersNode sc = (IProductionParametersNode) item;
				if (sc.getValue() instanceof TT085Country) {
					return (TT085Country) sc.getValue();
				}
			}
		}
		return null;
	}

}
