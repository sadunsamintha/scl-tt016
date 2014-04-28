package com.sicpa.standard.sasscl.controller.flow.statemachine.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.statistics.IStatistics;
import com.sicpa.standard.sasscl.business.statistics.StatisticsRestoredEvent;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareController;
import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.IProductionInitiator;
import com.sicpa.standard.sasscl.controller.productionconfig.loader.IProductionConfigLoader;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.impl.ProductionConfigProvider;

public class ExecutorConnecting implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ExecutorConnecting.class);

	protected IHardwareController hardwareController;
	protected IProductionInitiator productionInitiator;
	protected ProductionParameters productionParameters;
	protected IProductionConfigLoader loader;
	protected ProductionConfigProvider productionConfigProvider;
	protected IStatistics statistics;
	protected boolean ignoreStatsReset = false;

	@Override
	public void run() {
		try {
			IProductionConfig config = loader.get(productionParameters.getProductionMode());
			productionConfigProvider.set(config);
			productionInitiator.init(config);

			// do not reset the statistics when they are restored from the storage at startup
			if (!ignoreStatsReset) {
				statistics.reset();
			} else {
				ignoreStatsReset = false;
			}

			hardwareController.connect();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			EventBusService.post(new MessageEvent(MessageEventKey.FlowControl.DEVICES_CONNECT_FAILED));
		}
	}

	@Subscribe
	public void handleStatisticsRestored(StatisticsRestoredEvent evt) {
		ignoreStatsReset = true;
	}

	public void setHardwareController(IHardwareController hardwareController) {
		this.hardwareController = hardwareController;
	}

	public void setLoader(IProductionConfigLoader loader) {
		this.loader = loader;
	}

	public void setProductionInitiator(IProductionInitiator productionInitiator) {
		this.productionInitiator = productionInitiator;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setProductionConfigProvider(ProductionConfigProvider productionConfigProvder) {
		this.productionConfigProvider = productionConfigProvder;
	}

	public void setStatistics(IStatistics statistics) {
		this.statistics = statistics;
	}
}
