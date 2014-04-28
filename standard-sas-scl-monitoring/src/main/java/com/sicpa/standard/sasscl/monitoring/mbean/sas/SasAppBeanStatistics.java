package com.sicpa.standard.sasscl.monitoring.mbean.sas;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.monitoring.statistics.MonitoringStatistics;
import com.sicpa.standard.sasscl.monitoring.system.AbstractSystemEvent;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.OfflineCountingSystemEvent;
import com.sicpa.standard.sasscl.monitoring.system.event.ProductionParametersSystemEvent;

public class SasAppBeanStatistics extends MonitoringStatistics {

	private static final long serialVersionUID = 1L;

	protected String state;
	protected Set<String> devicesDisconnected = new HashSet<String>();
	protected IStorage storage;
	protected List<PropertyChangeListener> listeners;

	protected Integer offlineCountingQuantity = 0;
	protected Date offlineCountingFromDate = null;
	protected Date offlineCountingToDate = null;

	public SasAppBeanStatistics() {
		this.listeners = new ArrayList<PropertyChangeListener>();
	}

	public Integer getOfflineCountingQuantity() {
		return offlineCountingQuantity;
	}

	public Date getOfflineCountingFromDate() {
		return offlineCountingFromDate;
	}

	public Date getOfflineCountingToDate() {
		return offlineCountingToDate;
	}

	@Override
	public void handleSystemEvent(final AbstractSystemEvent event) {
		super.handleSystemEvent(event);
		if (event.getType().equals(SystemEventType.STATE_CHANGED)) {
			setState(event.getMessage() + "");
		} else if (event.getType().equals(SystemEventType.DEVICE_PRODUCTION_DISCONNECTED)) {
			if (isRunning()) {
				addDisconnectedDevices(event.getMessage() + "");
			}
		} else if (event.getType().equals(SystemEventType.DEVICE_REMOTE_SERVER_DISCONNECTED)) {
			addDisconnectedDevices("remoteServer");
		} else if (event.getType().equals(SystemEventType.DEVICE_PRODUCTION_CONNECTED)) {
			removeDisconnectedDevices(event.getMessage() + "");
		} else if (event.getType().equals(SystemEventType.DEVICE_REMOTE_SERVER_CONNECTED)) {
			removeDisconnectedDevices("remoteServer");
		} else if (event instanceof ProductionParametersSystemEvent) {
			if (event.getType().equals(SystemEventType.SELECT_PROD_PARAMETERS)) {
				resetOfflineCounting();
			}
		} else if (event.getType().equals(SystemEventType.OFFLINE_COUNTING)) {
			OfflineCountingSystemEvent offLineCountEvent = (OfflineCountingSystemEvent) event;
			offlineCountingQuantity = offLineCountEvent.getQuantity();
			offlineCountingFromDate = offLineCountEvent.getFrom();
			offlineCountingToDate = offLineCountEvent.getTo();
		}
	}

	protected void resetOfflineCounting() {
		offlineCountingQuantity = 0;
		offlineCountingFromDate = null;
		offlineCountingToDate = null;
	}

	public void addDisconnectedDevices(final String device) {
		devicesDisconnected.add(device);
	}

	public void removeDisconnectedDevices(final String device) {
		devicesDisconnected.remove(device);
	}

	public void setState(final String state) {
		this.state = state;
		firePropertyChanged("state", state);
	}

	public String getState() {
		return state == null ? "" : state;
	}

	public boolean isRunning() {
		return ApplicationFlowState.STT_STARTED.getName().equals(state);
	}

	public String getDevicesDisconnected() {
		String res = "";
		synchronized (devicesDisconnected) {
			for (String dev : devicesDisconnected) {
				res += dev + " - ";
			}
			if (!res.isEmpty()) {
				res = res.substring(0, res.length() - 2);
			}
		}
		return res;
	}

	public String getStorageInfo() {
		return this.storage.getStorageInfo();
	}

	public void setStorage(final IStorage storage) {
		this.storage = storage;
	}

	public void addPropertyChangeListener(final PropertyChangeListener l) {
		this.listeners.add(l);
	}

	public void removePropertyChangeListener(final PropertyChangeListener l) {
		this.listeners.remove(l);
	}

	protected void firePropertyChanged(final String property, final Object value) {
		if (!this.listeners.isEmpty()) {
			PropertyChangeEvent evt = new PropertyChangeEvent(this, property, null, value);
			for (PropertyChangeListener l : listeners) {
				l.propertyChange(evt);
			}
		}
	}

	@Override
	public void addError(final String error) {
		super.addError(error);
		firePropertyChanged("error", error);
	}

	@Override
	public void addEncoderUsedId(final String id) {
		super.addEncoderUsedId(id);
		firePropertyChanged("encodersUsedId", id);
	}

	@Override
	public void setProductionParameters(final ProductionParameters productionParameters) {
		super.setProductionParameters(productionParameters);
		firePropertyChanged("productionParameters", productionParameters);
	}

	@Override
	public void setStartTime(final Date startTime) {
		super.setStartTime(startTime);
		firePropertyChanged("startTime", startTime);
	}

	@Override
	public void setStopTime(final Date stopTime) {
		super.setStopTime(stopTime);
		firePropertyChanged("stopTime", stopTime);
	}

	@Override
	public void setProductsStatisticsValues(final StatisticsValues statisticsValues) {
		super.setProductsStatisticsValues(statisticsValues);
		firePropertyChanged("statisticsValues", statisticsValues);
	}

	@Override
	public void setLastProductScannedTime(final Date lastProductScannedTime) {
		super.setLastProductScannedTime(lastProductScannedTime);
		firePropertyChanged("lastProductScannedTime", lastProductScannedTime);
	}

	@Override
	public void setLastSentToRemoteServerDate(Date lastSentToRemoteServerDate) {
		super.setLastSentToRemoteServerDate(lastSentToRemoteServerDate);
		firePropertyChanged("lastSentToRemoteServerDate", lastSentToRemoteServerDate);
	}

	@Override
	public void setLastSentToRemoteServerStatus(String lastSentToRemoteServerStatus) {
		super.setLastSentToRemoteServerStatus(lastSentToRemoteServerStatus);
		firePropertyChanged("lastSentToRemoteServerStatus", lastSentToRemoteServerStatus);
	}

	@Override
	public void setLastSucessfulToRemoteServerDate(Date lastSucessfulToRemoteServerDate) {
		super.setLastSucessfulToRemoteServerDate(lastSucessfulToRemoteServerDate);
		firePropertyChanged("lastSucessfulToRemoteServerDate", lastSucessfulToRemoteServerDate);
	}

	@Override
	public void setLastSucessfulToRemoteServerNumberOfProduct(String lastSucessfulToRemoteServerNumberOfProduct) {
		super.setLastSucessfulToRemoteServerNumberOfProduct(lastSucessfulToRemoteServerNumberOfProduct);
		firePropertyChanged("lastSucessfulToRemoteServerNumberOfProduct", lastSucessfulToRemoteServerNumberOfProduct);
	}
}
