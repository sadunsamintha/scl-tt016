package com.sicpa.standard.sasscl.business.statistics.impl;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.utils.listener.CoalescentPeriodicListener;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.statistics.IStatistics;
import com.sicpa.standard.sasscl.business.statistics.StatisticsResetEvent;
import com.sicpa.standard.sasscl.business.statistics.mapper.IProductStatusToStatisticKeyMapper;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraConfig;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.event.StatisticsSystemEvent;
import com.sicpa.standard.sasscl.provider.impl.ProductionConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.*;
import static com.sicpa.standard.sasscl.model.ProductStatus.OFFLINE;
import static com.sicpa.standard.sasscl.model.ProductStatus.SENT_TO_PRINTER_WASTED;

/**
 * Statistics based on products created
 */
public class Statistics implements IStatistics {

	private static final Logger logger = LoggerFactory.getLogger(Statistics.class);

	private StatisticsValues stats = new StatisticsValues();
	private IStorage storage;
	private IProductStatusToStatisticKeyMapper statusMapper;
	private final UptimeCounter uptimeCounter = new UptimeCounter();
	private ProductionConfigProvider productionConfigProvider;

	public Statistics(IStorage storage) {
		this.storage = storage;
	}

	public void setStatusMapper(IProductStatusToStatisticKeyMapper statusMapper) {
		this.statusMapper = statusMapper;
	}

	/**
	 * increase by one the total and the statistics key corresponding to the product status<br>
	 * and notified the listeners that the statistics values have changed
	 */
	@Subscribe
	public synchronized void notifyNewProduct(NewProductEvent evt) {
		Product product = evt.getProduct();
		if (product != null) {
			if (isProductStatusHandled(product.getStatus())) {
				handleNewProduct(product);
			}
		}
	}

	private boolean isProductStatusHandled(ProductStatus status) {
		return !status.equals(SENT_TO_PRINTER_WASTED) && !status.equals(OFFLINE);
	}

	private void initStats() {
		if (productionConfigProvider.get().getCameraConfigs() != null) {
			for (CameraConfig cc : productionConfigProvider.get().getCameraConfigs()) {
				for (StatisticsKey key : statusMapper.getAllKeys()) {
					key.setLine(cc.getId());
					stats.set(key, 0);
				}
			}
		}
	}

	private void handleNewProduct(Product product) {
		stats.increase(StatisticsKey.TOTAL);
		Collection<StatisticsKey> keys = statusMapper.getKey(product.getStatus());
		if (keys != null) {
			for (StatisticsKey key : keys) {
				key.setLine(product.getQc());
				stats.increase(key);
			}
		}
		statsChangedEventConcentrator.eventReceived();
	}

	private final CoalescentPeriodicListener statsChangedEventConcentrator = new CoalescentPeriodicListener(250,
			() -> fireStatisticsChanged());

	private void fireStatisticsChanged() {
		MonitoringService.addSystemEvent(new StatisticsSystemEvent(stats));
	}

	public void saveStatistics() {
		if (stats == null || stats.isEmpty())
			return;

		// do not change the stats while it is being save
		synchronized (stats) {
			logger.debug("Saving statistics {}", stats.getMapValues());
			storage.saveStatistics(stats);
		}
	}

	public void setValues(StatisticsValues values) {
		this.stats = values;
	}

	@Override
	public void reset() {
		logger.debug("Reset statistics");
		stats.reset();
		saveStatistics();
		initStats();
		EventBusService.post(new StatisticsResetEvent());
	}

	public StatisticsValues getValues() {
		return stats;
	}

	public int get(final StatisticsKey key) {
		return stats.get(key);
	}

	@Subscribe
	public void handleApplicationStatusChanged(ApplicationFlowStateChangedEvent evt) {
		if (shouldStartUptimeCounter(evt)) {
			uptimeCounter.start();
		} else if (shouldStopUptimeCounter(evt)) {
			uptimeCounter.stop();
		}
	}

	private boolean shouldStartUptimeCounter(ApplicationFlowStateChangedEvent evt) {
		return evt.getCurrentState().equals(STT_STARTING);
	}

	private boolean shouldStopUptimeCounter(ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState().equals(STT_STOPPING)) {
			return true;
		} else if (evt.getCurrentState().equals(STT_RECOVERING)) {
			return true;
		} else if (evt.getCurrentState().equals(STT_CONNECTED) && evt.getPreviousState().equals(STT_STARTING)) {
			return true;
		}
		return false;
	}

	@Override
	public int getUptime() {
		return uptimeCounter.getUptime();
	}

	@Override
	public void resetUptimeCounter() {
		uptimeCounter.reset();
	}

	public void setProductionConfigProvider(ProductionConfigProvider productionConfigProvider) {
		this.productionConfigProvider = productionConfigProvider;
	}

}
