package com.sicpa.tt065.view.selection.display;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.text.MultiLineLabel;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.ProductBatchIdProvider;
import com.sicpa.standard.sasscl.view.selection.display.SelectionDisplayView;

/**
 * Overwritting class of the SelectionDisplayView to manage
 * the productionParameterSelected with the new BatchId View
 *
 * @author mjimenez
 *
 */
@SuppressWarnings("serial")
public class TT065SelectionDisplayView extends SelectionDisplayView implements ProductBatchIdProvider {

	@Override
	public void modelChanged() {
		if (model != null && model.getProductionParameters() != null) {
			buildSelectionPanel(model.getProductionParameters());
		}
	}

	protected void buildSelectionPanel(ProductionParameters pp) {
		getMainPanel().removeAll();

		SKU sku = pp.getSku();
		String barcode = pp.getBarcode();
		CodeType codeType = sku != null ? sku.getCodeType() : null;
		ProductionMode mode = pp.getProductionMode();
		String strBatchId = null;
		try {
			strBatchId = pp.getProperty(productionBatchId);
		}catch (IllegalArgumentException e){
		}


		if (mode != null) {
			getMainPanel().add(new MultiLineLabel(Messages.get(mode.getDescription())), "grow, w 200, h 45 , spanx");
		}

		if (codeType != null) {
			getMainPanel().add(new MultiLineLabel(codeType.getDescription()), "grow, w 200, h 45 , spanx");
		}
		if (sku != null) {
			if (sku.getImage() != null) {
				getMainPanel().add(toScaledImage(sku.getImage().getImage()), "grow,wrap");
			}
			getMainPanel().add(new MultiLineLabel(sku.getDescription()), "grow, w 200, h 175 , spanx");
		}
		if (strBatchId != null && !strBatchId.equals("0")){
			getMainPanel().add(new MultiLineLabel(Messages.get("sku.batch.id.label")+" "+strBatchId), "grow, w 200, h 45 , spanx");
		}

		if (barcode != null) {
			getMainPanel().add(new MultiLineLabel(barcode), "grow, w 200, h 45 , spanx");
		}

		getMainPanel().revalidate();
		getMainPanel().repaint();
	}

}
