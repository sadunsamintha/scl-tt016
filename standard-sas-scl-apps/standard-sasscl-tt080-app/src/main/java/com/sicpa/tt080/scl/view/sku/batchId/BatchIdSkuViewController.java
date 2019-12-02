package com.sicpa.tt080.scl.view.sku.batchId;

import com.google.common.eventbus.Subscribe;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.flow.FlowControl;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.ProductBatchIdProvider;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.tt080.scl.event.BatchIdViewEvent;
import com.sicpa.tt080.scl.view.TT080ViewFlowController;
import com.sicpa.tt080.scl.view.flow.TT080ScreensFlow;

import static com.sicpa.tt080.scl.view.TT080ScreenFlowTriggers.BATCH_ID_REGISTERED;

/**
 * Controller to handle BatchIds for Rep. Dominican Project
 * batchIds will follow a custom format, where the timestamp will be appended to the final result:
 *
 * TIMESTAMP_SKUID_Batch
 */
public class BatchIdSkuViewController extends TT080ViewFlowController implements IBatchIdSkuListener, ProductBatchIdProvider {

	private static final Logger logger = LoggerFactory.getLogger(BatchIdSkuViewController.class);

	private TT080ScreensFlow screensFlow;
	private FlowControl flowControl;
	private ProductionParameters pp;
	private BatchIdSkuModel model;

	private static final int MAX_FIELD_SIZE = 100;
	private static final Pattern ALL_SYMBOLS_RESTRICTION_PATTERN =  Pattern.compile("[%@!()*~^!#$%&+/ ]");


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

	@Subscribe
	public void handleBatchIdSkuView(ApplicationFlowStateChangedEvent event) {
		logger.info("CurrentState: " + event.getCurrentState().toString() + " PreviousState: " + event.getPreviousState().toString() + " View:" + screensFlow);
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

	public void setScreensFlow(TT080ScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}

	public void setFlowControl(FlowControl flowControl) {
		this.flowControl = flowControl;
	}

	@Override
	public void saveBatchId(final String strBatchId) {
		if (StringUtils.isBlank(strBatchId)){
			showMessageDialog(Messages.get("sku.batch.id.validation.blank"));
			return;
		}

		if (hasInvalidFormat(strBatchId)){
			showMessageDialog(Messages.get("sku.batch.id.validation.format"));
			return;
		}

		if (hasInvalidBatchSize(strBatchId)){
			showMessageDialog(Messages.get("sku.batch.id.validation.size"));
			return;
		}

		final ProductionBatch productionBatch = new ProductionBatch(strBatchId, viewController.getProductionParameters().getSku().getId());
		if (isInvalidProductionBatchSize(productionBatch.getProductionBatchId())){
			showMessageDialog(Messages.get("sku.productionbatch.id.validation.size"));
			return;
		}

		pp.setProperty(ProductBatchIdProvider.productionBatchId, productionBatch);
		logger.info(Messages.format("sku.batch.id.registered", productionBatch.getProductionBatchId()));

		batchIdButtonPressed.set(false);

		viewController.setProductionParameters(pp);
		viewController.productionParametersChanged();
		screensFlow.moveToNext(BATCH_ID_REGISTERED);

		//store the batchId on ProductionBatchProvider to save later on database
		EventBusService.post(new BatchIdViewEvent(pp));
	}

	private static boolean hasInvalidFormat(String strBatchId) {
		final Matcher matcher = ALL_SYMBOLS_RESTRICTION_PATTERN.matcher(strBatchId);
		return matcher.find();
	}

	private static boolean isInvalidProductionBatchSize(String productionBatchId) {
		return productionBatchId.length() > MAX_FIELD_SIZE;
	}

	private static boolean hasInvalidBatchSize(final String strBatchId) {
		return strBatchId.length() == 0 || strBatchId.length() > 15;
	}

	public BatchIdSkuModel getModel() {
		return model;
	}
}
