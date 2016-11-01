package com.sicpa.tt065.view.sku.batchId;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.flow.FlowControl;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.tt065.event.BatchIdViewEvent;
import com.sicpa.tt065.view.flow.TT065DefaultScreensFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.sicpa.tt065.view.TT065ScreenFlowTriggers.BATCH_ID_REGISTERED;
import static com.sicpa.tt065.view.TT065ScreenFlowTriggers.PRODUCTION_PARAMETER_TO_BATCH;

public class BatchIdSkuViewController extends AbstractViewFlowController implements IBatchIdSkuListener {

	private static final Logger logger = LoggerFactory.getLogger(BatchIdSkuViewController.class);

	private TT065DefaultScreensFlow screensFlow;
	private FlowControl flowControl;
	private ProductionParameters pp;

	private volatile HardwareControllerStatus status;

	private AtomicBoolean batchIdButtonPressed = new AtomicBoolean(false);

	public BatchIdSkuViewController(){
		new BatchIdSkuModel();
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		logger.info("refreshing_panel,reason=language switch,lang=" + evt.getLanguage());
		BatchIdSkuView srv = new BatchIdSkuView();
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

	@Subscribe
	public void handleSkuSelectionButtonPressed(BatchIdViewEvent evt){
		batchIdButtonPressed.set(true);
		pp = evt.getPp();
		screensFlow.moveToNext(PRODUCTION_PARAMETER_TO_BATCH);
		logger.info("SkuSelectionButtonPressed,user=" + evt.user.getLogin() + ",date=" + evt.date.toString() + ", batchId=");
	}

	@Override
	protected void displayView() {
		if (!batchIdButtonPressed.get()) {
			super.displayView();
			batchIdButtonPressed.set(true);
		} else {
			moveToNext();
		}
	}

	private void moveToNext() {
		screensFlow.moveToNext(BATCH_ID_REGISTERED);
		//flowControl.moveToNextState(TRG_STOP_REASON_SELECTED);
	}

	public void setScreensFlow(TT065DefaultScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}

	public void setFlowControl(FlowControl flowControl) {
		this.flowControl = flowControl;
	}

	@Override
	public void saveBatchId() {
		logger.info(Messages.format("sku.batch.id.registered", "987654321"));

		//MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventLevel.INFO, PROD_STOP_REASON, Messages.get(stopReason.getKey())));
		moveToNext();
	}
}
