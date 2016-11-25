package com.sicpa.tt065.view.sku.batchId;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.flow.FlowControl;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.ProductBatchIdProvider;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.tt065.event.BatchIdViewEvent;
import com.sicpa.tt065.view.TT065AbstractViewFlowController;
import com.sicpa.tt065.view.flow.TT065DefaultScreensFlow;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sicpa.tt065.view.TT065ScreenFlowTriggers.BATCH_ID_REGISTERED;

/**
 * Controller of the new BatchId view
 *
 * @author mjimenez
 *
 */
public class BatchIdSkuViewController extends TT065AbstractViewFlowController implements IBatchIdSkuListener, ProductBatchIdProvider {

	private static final Logger logger = LoggerFactory.getLogger(BatchIdSkuViewController.class);

	private TT065DefaultScreensFlow screensFlow;
	private FlowControl flowControl;
	private ProductionParameters pp;
	private BatchIdSkuModel model;


	private volatile HardwareControllerStatus status;

	private AtomicBoolean batchIdButtonPressed = new AtomicBoolean(false);

	public BatchIdSkuViewController(){
		this(new BatchIdSkuModel());
	}

	public BatchIdSkuViewController(BatchIdSkuModel model){
		this.model = model;
		CustoBuilder.addPropertyToClass(ProductionParameters.class, productionBatchId);
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		logger.info("refreshing_panel,reason=language switch,lang=" + evt.getLanguage());
		BatchIdSkuView srv = new BatchIdSkuView(this.pp);
		srv.setController(this);
		this.setView(srv);
	}


	/*@Subscribe
	public void handleStopProduction(ApplicationFlowStateChangedEvent event) {
		if (event.getPreviousState().equals(ApplicationFlowState.STT_STARTED) && event.getCurrentState().equals(ApplicationFlowState.STT_STOPPING)) {
			screensFlow.moveToNext(STOP_PRODUCTION);
		}
	}*/

	@Subscribe
	public void handleBatchIdSkuView(ApplicationFlowStateChangedEvent event) {
		logger.info("CurrentState: " + event.getCurrentState().toString() + " PreviousState: " + event.getPreviousState().toString() + " View:" + screensFlow);
		/*if (event.getPreviousState().equals(ApplicationFlowState.STT_RECOVERING) && event.getCurrentState().equals(ApplicationFlowState.STT_CONNECTED) && screensFlow!=null) {
			logger.info("AQUI DEVERIA TROCAR DE PAINEL ANTES DE IR NO PAINEL PRINCIPAL");
			screensFlow.moveToNext(PRODUCTION_PARAMETER_SELECTED); //TODO muda o Panel atual para o BatchIdSkuView desde o Panel de seleçao de SKU
		}*/
	}

	@Override
	protected void displayView() {
		if (!batchIdButtonPressed.get()) {
			this.pp = viewController.getProductionParameters();
			this.model.setProductionParameters(pp);
			BatchIdSkuView srv = new BatchIdSkuView(this.pp);
			srv.setController(this);
			this.setView(srv);

			super.displayView();
			batchIdButtonPressed.set(true);
		}
	}

	public void setScreensFlow(TT065DefaultScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}

	public void setFlowControl(FlowControl flowControl) {
		this.flowControl = flowControl;
	}

	@Override
	public void saveBatchId(String strBatchId) {
		if (StringUtils.isBlank(strBatchId)){
			showMessageDialog(Messages.get("sku.batch.id.validation.blank"));
			return;
		}
		Pattern patt = Pattern.compile("[%@!()*~^!#$%&+/ ]");//restrictions all symbols
		Matcher matcher = patt.matcher(strBatchId);
		if (matcher.find()){
			showMessageDialog(Messages.get("sku.batch.id.validation.format"));
			return;
		}
		if (strBatchId.length() != 15){
			showMessageDialog(Messages.get("sku.batch.id.validation.size"));
			return;
		}

		pp.setProperty(productionBatchId, strBatchId);
		logger.info(Messages.format("sku.batch.id.registered", strBatchId));

		batchIdButtonPressed.set(false);

		viewController.setProductionParameters(pp);
		viewController.productionParametersChanged();
		screensFlow.moveToNext(BATCH_ID_REGISTERED);

		//store the batchId on ProductionBatchProvider to save later on database
		EventBusService.post(new BatchIdViewEvent(pp));
	}

	public BatchIdSkuModel getModel() {
		return model;
	}
}
