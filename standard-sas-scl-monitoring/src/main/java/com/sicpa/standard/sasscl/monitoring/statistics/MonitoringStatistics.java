package com.sicpa.standard.sasscl.monitoring.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sicpa.standard.monitor.IMonitorObject;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.monitoring.system.AbstractSystemEvent;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.ProductionParametersSystemEvent;
import com.sicpa.standard.sasscl.monitoring.system.event.StatisticsSystemEvent;
import com.sicpa.standard.util.FieldToString;

public abstract class MonitoringStatistics implements IMonitorObject, Serializable {

	private static final long serialVersionUID = 1L;

	protected MonitoredProductStatisticsValues productsStatistics;
	protected Date startTime;
	protected Date stopTime;
	protected final List<String> encodersUsedId = new ArrayList<String>();

	protected final List<String> errors = new ArrayList<String>();
	protected ProductionParameters productionParameters;

	protected long subsystemId;
	protected Date lastProductScannedTime;

	protected Date lastSentToRemoteServerDate;
	protected String lastSentToRemoteServerStatus;
	protected Date lastSucessfulToRemoteServerDate;
	protected String lastSucessfulToRemoteServerNumberOfProduct;

	// regardless fail or success
	protected String lastSendToRemoteServerNumberOfProduct;

	public MonitoringStatistics() {
		this.productsStatistics = new MonitoredProductStatisticsValues();
	}

	@Override
	public String[] getFields() {
		return FieldToString.getFieldsValue(this);
	}

	@Override
	public void setFields(final String[] fields) {
		FieldToString.setFieldsValue(this, fields);
	}

	public void handleSystemEvent(final AbstractSystemEvent event) {
		if (event.getType().equals(SystemEventType.STATE_CHANGED)) {
			if (event.getMessage().equals(ApplicationFlowState.STT_STARTED.getName())) {
				setStartTime(event.getDate());
			} else if (event.getMessage().equals(ApplicationFlowState.STT_CONNECTED.getName())
					|| event.getMessage().equals(ApplicationFlowState.STT_CONNECTING.getName())) {
				setStopTime(event.getDate());
			}
		} else if (event.getType().equals(SystemEventType.PRODUCT_SCANNED)) {
			setLastProductScannedTime(new Date());
		} else if (event.getType().equals(SystemEventType.LAST_SENT_TO_REMOTE_SERVER)) {
			this.setLastSendToRemoteServerNumberOfProduct(event.getMessage() + "");
		} else if (event.getType().equals(SystemEventType.SENT_TO_REMOTE_SERVER_OK)) {
			Date date = new Date();
			setLastSentToRemoteServerDate(date);
			setLastSucessfulToRemoteServerDate(date);
			setLastSucessfulToRemoteServerNumberOfProduct(event.getMessage() + "");
			setLastSentToRemoteServerStatus("OK");
		} else if (event.getType().equals(SystemEventType.SENT_TO_REMOTE_SERVER_ERROR)) {
			setLastSentToRemoteServerDate(new Date());
			setLastSentToRemoteServerStatus("ERROR");
		} else if (event.getType().equals(SystemEventType.GET_CODE_FROM_ENCODER)) {
			synchronized (encodersUsedId) {
				if (!this.encodersUsedId.contains(event.getMessage())) {
					addEncoderUsedId(event.getMessage() + "");
				}
			}
		} else if (event instanceof ProductionParametersSystemEvent) {
//			if (event.getType().equals(SystemEventType.SELECT_PROD_PARAMETERS)) {
//				setProductionParameters(((ProductionParametersSystemEvent) event).getMessage());
//			}
		} else if (event instanceof StatisticsSystemEvent) {
			if (event.getType().equals(SystemEventType.STATISTICS_CHANGED)) {
				setProductsStatisticsValues(((StatisticsSystemEvent) event).getMessage());
			}
		}
		if (event.getLevel().equals(SystemEventLevel.ERROR)) {
			addError(event.getMessage() + "");
		}
	}

	public void addError(final String error) {
		synchronized (errors) {
			this.errors.add(error);
		}
	}

	public void addEncoderUsedId(final String id) {
		synchronized (this.encodersUsedId) {
			this.encodersUsedId.add(id);
		}
	}

	public ProductionParameters getProductionParameters() {
		return this.productionParameters;
	}

	public void setProductionParameters(final ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setStartTime(final Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setStopTime(final Date stopTime) {
		this.stopTime = stopTime;
	}

	public Date getStopTime() {
		return this.stopTime;
	}

	public void setProductsStatisticsValues(final StatisticsValues statisticsValues) {
		this.productsStatistics.setValues(statisticsValues);
	}

	public void setProductsStatisticsOffset(final Map<StatisticsKey, Integer> map) {
		this.productsStatistics.setMapOffset(map);
	}

	public MonitoredProductStatisticsValues getProductsStatistics() {
		return this.productsStatistics;
	}

	public List<String> getErrors() {
		synchronized (errors) {
			return new ArrayList<String>(errors);
		}
	}

	public void setSubsystemId(long subsystemId) {
		this.subsystemId = subsystemId;
	}

	public long getSubsystemId() {
		return subsystemId;
	}

	public Date getLastSucessfulToRemoteServerDate() {
		return this.lastSucessfulToRemoteServerDate;
	}

	public String getLastSucessfulToRemoteServerNumberOfProduct() {
		return this.lastSucessfulToRemoteServerNumberOfProduct == null ? ""
				: this.lastSucessfulToRemoteServerNumberOfProduct;
	}

	public String getLastSendToRemoteServerNumberOfProduct() {
		return this.lastSendToRemoteServerNumberOfProduct == null ? "" : this.lastSendToRemoteServerNumberOfProduct;
	}

	public Date getLastProductScannedTime() {
		return this.lastProductScannedTime;
	}

	public Date getLastSentToRemoteServerDate() {
		return this.lastSentToRemoteServerDate;
	}

	public List<String> getEncodersUsedId() {
		synchronized (encodersUsedId) {
			return new ArrayList<String>(encodersUsedId);
		}
	}

	public void setLastProductScannedTime(final Date lastProductScannedTime) {
		this.lastProductScannedTime = lastProductScannedTime;
	}

	public void setLastSentToRemoteServerDate(final Date lastSentToRemoteServerDate) {
		this.lastSentToRemoteServerDate = lastSentToRemoteServerDate;
	}

	public void setLastSucessfulToRemoteServerDate(final Date lastSucessfulToRemoteServerDate) {
		this.lastSucessfulToRemoteServerDate = lastSucessfulToRemoteServerDate;
	}

	public void setLastSucessfulToRemoteServerNumberOfProduct(final String lastSucessfulToRemoteServerNumberOfProduct) {
		this.lastSucessfulToRemoteServerNumberOfProduct = lastSucessfulToRemoteServerNumberOfProduct;
	}

	public void setLastSendToRemoteServerNumberOfProduct(String lastSendToRemoteServerNumberOfProduct) {
		this.lastSendToRemoteServerNumberOfProduct = lastSendToRemoteServerNumberOfProduct;
	}

	public String getLastSentToRemoteServerStatus() {
		return this.lastSentToRemoteServerStatus == null ? "" : this.lastSentToRemoteServerStatus;
	}

	public void setLastSentToRemoteServerStatus(String lastSentToRemoteServerStatus) {
		this.lastSentToRemoteServerStatus = lastSentToRemoteServerStatus;
	}
}
