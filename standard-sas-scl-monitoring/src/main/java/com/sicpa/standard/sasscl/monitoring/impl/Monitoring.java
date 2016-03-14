package com.sicpa.standard.sasscl.monitoring.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.exception.MonitoringException;
import com.sicpa.standard.model.MonitorType;
import com.sicpa.standard.monitor.IMonitorTypesMapping;
import com.sicpa.standard.monitor.MonitorService;
import com.sicpa.standard.sasscl.business.statistics.StatisticsRestoredEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.monitoring.IMonitoring;
import com.sicpa.standard.sasscl.monitoring.statistics.MonitoredProductStatisticsValues;
import com.sicpa.standard.sasscl.monitoring.statistics.MonitoringStatistics;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.monitoring.statistics.production.ProductionStatistics;
import com.sicpa.standard.sasscl.monitoring.system.AbstractSystemEvent;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import com.sicpa.standard.util.FieldToString;
import com.sicpa.standard.util.IConverter;

public class Monitoring implements IMonitoring {

	private static Logger logger = LoggerFactory.getLogger(Monitoring.class);

	protected IncrementalStatistics incrementalStatistics;
	protected ProductionStatistics productionStatistics;

	protected MonitoringStatistics mbeanStatistics;

	protected final List<SystemEventType> doNotSaveEvents = new ArrayList<>();

	protected int saveIncrPeriod;

	protected SubsystemIdProvider subsystemIdProvider;

	protected ProductionParameters productionParameters;

	protected Timer saveIncrTimer = new Timer("saveIncrTimer");

	protected Object lockIncremental = new Object();
	protected Object lockProduction = new Object();

	protected volatile boolean restoreStats;

	@SuppressWarnings("rawtypes")
	public Monitoring(final IMonitorTypesMapping mapping) {
		MonitorService.setMonitorTypesMapping(mapping);

		FieldToString.addConverter(SystemEventLevel.class, new IConverter<SystemEventLevel>() {
			@Override
			public SystemEventLevel convertFromString(final String value) {
				return new SystemEventLevel(value);
			}

			@Override
			public String convertToString(final SystemEventLevel value) {
				return value.toString();
			}
		});

		FieldToString.addConverter(SystemEventType.class, new IConverter<SystemEventType>() {
			@Override
			public SystemEventType convertFromString(final String value) {
				return new SystemEventType(value);
			}

			@Override
			public String convertToString(final SystemEventType value) {
				return value.toString();
			}
		});

		FieldToString.addConverter(List.class, new IConverter<List>() {
			@Override
			public List convertFromString(final String value) {
				List<String> list = new ArrayList<String>();

				if (value != null && !value.isEmpty()) {
					String[] array = value.split("----");
					for (String elem : array) {
						list.add(elem);
					}
				}

				return list;
			}

			@Override
			public String convertToString(final List value) {
				String s = "";
				for (Object o : value) {
					s += o + "----";
				}
				return s;
			}
		});

		FieldToString.addConverter(ProductionParameters.class, new IConverter<ProductionParameters>() {

			@Override
			public ProductionParameters convertFromString(final String value) {

				ProductionParameters res = new ProductionParameters();

				if (value != null && !value.isEmpty()) {

					int indexSku = value.indexOf(", sku=");
					int indexBarcode = value.indexOf(", barcode=");

					String mode = value.substring("[productionMode=".length(), indexSku);
					String sku;
					String barcode = "";
					if (indexBarcode != -1) {
						sku = value.substring(indexSku + ", sku=".length(), indexBarcode);
						barcode = value.substring(indexBarcode + ", barcode=".length(), value.lastIndexOf(']'));
					} else {
						sku = value.substring(indexSku + ", sku=".length(), value.lastIndexOf(']'));
					}

					res.setBarcode(barcode);
					res.setSku(new SKU(-1, sku));
					res.setProductionMode(new ProductionMode(-1, mode, false));
				}

				return res;
			}

			@Override
			public String convertToString(final ProductionParameters value) {
				return value.toString();
			}
		});

		FieldToString.addConverter(MonitoredProductStatisticsValues.class,
				new IConverter<MonitoredProductStatisticsValues>() {
					@Override
					public MonitoredProductStatisticsValues convertFromString(final String value) {
						MonitoredProductStatisticsValues res = new MonitoredProductStatisticsValues();
						// (total:12)(bad:1)(good:11)

						if (value != null && !value.isEmpty()) {
							String s = value.substring(1, value.length() - 1);
							String[] array = s.split("\\)\\(");
							for (String elem : array) {
								int index = elem.indexOf(':');
								String key = elem.substring(0, index);
								String statsValue = elem.substring(index + 1);
								try {
									res.getValues().put(new StatisticsKey(key), Integer.parseInt(statsValue));
								} catch (Exception e) {
									logger.error("", e);
								}
							}
						}

						return res;
					}

					@Override
					public String convertToString(final MonitoredProductStatisticsValues value) {
						return value.toString();
					}
				});

		FieldToString.addConverter(Date.class, new IConverter<Date>() {

			private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			@Override
			public Date convertFromString(String value) {
				if (value == null || value.isEmpty()) {
					return null;
				}
				try {
					return formatter.parse(value);
				} catch (ParseException e) {
					logger.error("", e);
				}
				return null;
			}

			@Override
			public String convertToString(Date value) {
				if (value == null) {
					return "";
				}
				return formatter.format(value).toString();
			}
		});

		populateDoNotSaveEventList();

	}

	protected void populateDoNotSaveEventList() {
		doNotSaveEvents.add(SystemEventType.STATISTICS_CHANGED);
		doNotSaveEvents.add(SystemEventType.PRODUCT_SCANNED);
		doNotSaveEvents.add(SystemEventType.GET_CODE_FROM_ENCODER);
		doNotSaveEvents.add(SystemEventType.PRINTER_INK_LEVEL);
		doNotSaveEvents.add(SystemEventType.PRINTER_MAKEUP_LEVEL);
		doNotSaveEvents.add(SystemEventType.OFFLINE_COUNTING);
	}

	@Override
	public void addSystemEvent(final AbstractSystemEvent event) {

		if (!doNotSaveEvents.contains(event.getType())) {
			if (event instanceof BasicSystemEvent) {
				addSystemEventInternal((BasicSystemEvent) event);
			} else {// as we can only save one type of event convert everything to BasicSystemEvent
				addSystemEventInternal(new BasicSystemEvent(event.getLevel(), event.getType(), event.getMessage() + ""));
			}
		}
		if (incrementalStatistics != null) {
			synchronized (lockIncremental) {
				incrementalStatistics.handleSystemEvent(event);
			}
		}
		if (productionStatistics != null) {
			synchronized (lockProduction) {
				productionStatistics.handleSystemEvent(event);
			}
		}
		if (mbeanStatistics != null) {
			mbeanStatistics.handleSystemEvent(event);
		}

		if (event.getType().equals(SystemEventType.STATE_CHANGED)) {

			if (event.getMessage().equals(ApplicationFlowState.STT_CONNECTED.getName())
					|| event.getMessage().equals(ApplicationFlowState.STT_CONNECTING.getName())) {
				saveIncrementalStatistics();
				saveProductionStatistics();
				saveIncrTimer.cancel();
			}

			else if (event.getMessage().equals(ApplicationFlowState.STT_STARTING.getName())) {
				saveIncrTimer = new Timer("saveIncrTimer");
				saveIncrTimer.scheduleAtFixedRate(createSaveIncrTask(), 1000L * saveIncrPeriod, 1000L * saveIncrPeriod);

				/**
				 * Production can last multiple days without interruption, it is necessary to schedule statistics saving
				 * at midnight for consistency
				 */
				Date firstSavingDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
				firstSavingDate = DateUtils.addDays(firstSavingDate, 1);
				firstSavingDate = DateUtils.addSeconds(firstSavingDate, -1);
				saveIncrTimer.schedule(createSaveIncrTask(), firstSavingDate, DateUtils.MILLIS_PER_DAY);
				saveIncrTimer.schedule(createSaveStatTask(), firstSavingDate, DateUtils.MILLIS_PER_DAY);
			}
		} else if (event.getType().equals(SystemEventType.SELECT_PROD_PARAMETERS)) {
			if (restoreStats) {
				restoreStats = false;
			} else {
				//saveIncrTimer.cancel();
				incrementalStatistics = null;
				productionStatistics = null;
			}
		}
	}

	public void saveProductionStatistics() {
		synchronized (lockProduction) {
			if (productionStatistics != null && !productionStatistics.getProductsStatistics().getValues().isEmpty()) {
				productionStatistics.setStopTime(new Date());
				MonitorService.addEvent(MonitorType.PRODUCTION_STATISTICS, productionStatistics);
			}
			createNewProductionStatistics();
		}
	}

	public void saveIncrementalStatistics() {
		synchronized (lockIncremental) {
			if (incrementalStatistics != null && !incrementalStatistics.getProductsStatistics().getValues().isEmpty()) {
				incrementalStatistics.setStopTime(new Date());
				MonitorService.addEvent(MonitorType.INCREMENTAL_STATISTICS, incrementalStatistics);
			}
			createNewIncrementalStatistics();
		}
	}

	protected void createNewProductionStatistics() {
		ProductionStatistics previous = productionStatistics;
		productionStatistics = new ProductionStatistics();
		productionStatistics.setSubsystemId(subsystemIdProvider.get());
		productionStatistics.setProductionParameters(productionParameters);
		productionStatistics.setStartTime(new Date());

		if (previous != null) {
			Map<StatisticsKey, Integer> mapValues;
			if (previous.getProductsStatistics().getValues().size() == 0) {
				mapValues = previous.getProductsStatistics().getMapOffset();
			} else {
				mapValues = previous.getProductsStatistics().getValues();
			}
			productionStatistics.getProductsStatistics().setMapOffset(mapValues);
		}
	}

	protected void createNewIncrementalStatistics() {
		IncrementalStatistics previous = incrementalStatistics;
		incrementalStatistics = new IncrementalStatistics();
		incrementalStatistics.setSubsystemId(subsystemIdProvider.get());
		incrementalStatistics.setProductionParameters(productionParameters);
		incrementalStatistics.setStartTime(new Date());

		if (previous != null) {
			Map<StatisticsKey, Integer> mapValues;
			if (previous.getProductsStatistics().getValues().size() == 0) {
				mapValues = previous.getProductsStatistics().getMapOffset();
			} else {
				mapValues = previous.getProductsStatistics().getValues();
			}
			incrementalStatistics.getProductsStatistics().setMapOffset(mapValues);
		}
	}

	protected TimerTask createSaveIncrTask() {
		return new TimerTask() {
			@Override
			public void run() {
				saveIncrementalStatistics();
			}
		};
	}

	protected TimerTask createSaveStatTask() {
		return new TimerTask() {
			@Override
			public void run() {
				saveProductionStatistics();
			}
		};
	}

	protected void addSystemEventInternal(final BasicSystemEvent event) {
		MonitorService.addEvent(MonitorType.SYSTEM_EVENT, event);
	}

	public void setSaveIncrPeriod(final int saveIncrPeriod) {
		this.saveIncrPeriod = saveIncrPeriod;
		if (this.saveIncrPeriod <= 0) {
			this.saveIncrPeriod = 300;
		}
	}

	@Override
	public List<BasicSystemEvent> getSystemEvent(final Date from, final Date to) {
		return read(MonitorType.SYSTEM_EVENT, from, to);
	}

	@Override
	public List<ProductionStatistics> getProductionStatistics(final Date from, final Date to) {
		return read(MonitorType.PRODUCTION_STATISTICS, from, to);
	}

	@Override
	public List<IncrementalStatistics> getIncrementalStatistics(final Date from, final Date to) {
		return read(MonitorType.INCREMENTAL_STATISTICS, from, to);
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> read(final MonitorType type, final Date from, final Date to) {
		List<T> res = null;

		try {
			res = (List<T>) MonitorService.getEventsList(type, from, to);
		} catch (MonitoringException e) {
			logger.error("", e);
		}
		return res;
	}

	public void setMbeanStatistics(final MonitoringStatistics mbeanStatistics) {
		this.mbeanStatistics = mbeanStatistics;
		this.mbeanStatistics.setSubsystemId(subsystemIdProvider.get());
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setSubsystemIdProvider(SubsystemIdProvider subsystemIdProvider) {
		this.subsystemIdProvider = subsystemIdProvider;
	}

	@Subscribe
	public void handleStatisticsRestored(StatisticsRestoredEvent evt) {
		restoreStats = true;

		initStats();

		productionStatistics.setProductsStatisticsOffset(evt.getStatsValues().getMapValues());
		incrementalStatistics.setProductsStatisticsOffset(evt.getStatsValues().getMapValues());
	}

	private void initStats(){
		synchronized(lockProduction){
			createNewProductionStatistics();
		}
		synchronized(lockIncremental){
			createNewIncrementalStatistics();
		}
	}
}
