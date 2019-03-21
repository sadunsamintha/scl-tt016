package com.sicpa.tt079.view.selection.display;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.utils.DateUtils;
import com.sicpa.standard.gui.components.text.MultiLineLabel;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.ProductBatchIdExpDtProvider;
import com.sicpa.standard.sasscl.view.selection.display.SelectionDisplayView;
import com.sicpa.tt079.view.sku.batch_exp.BatchIdExpDtSkuViewController;

/**
 * Overwritting class of the SelectionDisplayView to manage
 * the productionParameterSelected with the new BatchId and Expiration Date View
 *
 *
 */
@SuppressWarnings("serial")
public class TT079SelectionDisplayView extends SelectionDisplayView implements ProductBatchIdExpDtProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(TT079SelectionDisplayView.class);

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
		Date expDt = null;
		try {
			strBatchId = pp.getProperty(productionBatchId);
			expDt = pp.getProperty(productionExpdt);
		}catch (IllegalArgumentException e){
			logger.error(e.getMessage(),e);
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
			getMainPanel().add(new MultiLineLabel(sku.getDescription()), "grow, w 200, h 135 , spanx");
		}
		if (StringUtils.isNotBlank(strBatchId)){
			getMainPanel().add(new MultiLineLabel(Messages.get("sku.batch.id.label")+" "+strBatchId), "grow, w 200, h 45 , spanx");
		}
		if (expDt!=null){
			String sExpDt = DateUtils.format("dd/MM/yyyy", expDt);
			getMainPanel().add(new MultiLineLabel(Messages.get("sku.expdt.id.label")+" "+sExpDt), "grow, w 200, h 50 , spanx");
		}

		if (barcode != null) {
			getMainPanel().add(new MultiLineLabel(barcode), "grow, w 200, h 45 , spanx");
		}

		getMainPanel().revalidate();
		getMainPanel().repaint();
	}

}
