package com.sicpa.tt079.view.sku.batch_exp;

import static com.sicpa.tt079.view.flow.TT079ScreenFlowTriggers.BATCH_ID_EXP_DT_REGISTERED;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.ProductBatchIdExpDtProvider;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;
import com.sicpa.tt079.event.BatchIdExpViewEvent;
import com.sicpa.tt079.view.flow.TT079DefaultScreensFlow;

/**
 * Controller of the new BatchId and Expiration date view
 *
 */
public class BatchIdExpDtSkuViewController extends AbstractViewFlowController implements IBatchIdExpDtSkuListener, ProductBatchIdExpDtProvider {

	private static final Logger logger = LoggerFactory.getLogger(BatchIdExpDtSkuViewController.class);

	private TT079DefaultScreensFlow screensFlow;
	private ProductionParameters pp;
	private BatchIdExpDtSkuModel model;
	private int batchIdSize;

	public BatchIdExpDtSkuViewController(){
		this(new BatchIdExpDtSkuModel());
	}

	public BatchIdExpDtSkuViewController(BatchIdExpDtSkuModel model){
		this.model = model;
		CustoBuilder.addPropertyToClass(ProductionParameters.class, productionBatchId);
		CustoBuilder.addPropertyToClass(ProductionParameters.class, productionExpdt );
	}

	public void setScreensFlow(TT079DefaultScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}
	

	@Override
	public void saveBatchIdAndExpDt(String strBatchId,Date expDt) {
		
		Pattern patt = Pattern.compile("[%@!()*~^!#$%&+/ ]");//restrictions all symbols
		Matcher matcher = patt.matcher(strBatchId);
		if (matcher.find()){
			JOptionPane.showMessageDialog(null,Messages.get("sku.batch.id.validation.format"));
			return;
		}
		
		if (strBatchId.length() > this.getBatchIdSize()){
			JOptionPane.showMessageDialog(null, Messages.format("sku.batch.id.validation.size",this.getBatchIdSize()));
			return;
		}

		if (expDt == null){
			JOptionPane.showMessageDialog(null, Messages.get("sku.expdt.validation.blank"));
			return;
		}
		
        pp.setProperty(productionBatchId, strBatchId);
        pp.setProperty(productionExpdt, expDt);
        
        logger.info("The Batch Id {} and expdt {} was registered",strBatchId, expDt);

		viewController.setProductionParameters(pp);
		viewController.productionParametersChanged();
		screensFlow.moveToNext(BATCH_ID_EXP_DT_REGISTERED);
	}

	@Override
	protected void displayView() {
		super.displayView();
		BatchIdExpDtSkuView view = (BatchIdExpDtSkuView) this.view;
		view.refresh();
	}

	public void setPp(ProductionParameters pp) {
		this.pp = pp;
	}

	public BatchIdExpDtSkuModel getModel() {
		return model;
	}

	public int getBatchIdSize() {
		return batchIdSize;
	}

	public void setBatchIdSize(int batchIdSize) {
		this.batchIdSize = batchIdSize;
	}
	
}
