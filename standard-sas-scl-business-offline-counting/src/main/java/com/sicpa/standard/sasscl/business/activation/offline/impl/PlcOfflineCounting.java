package com.sicpa.standard.sasscl.business.activation.offline.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.business.activation.offline.AbstractOfflineCounting;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class PlcOfflineCounting extends AbstractOfflineCounting {

	private static final Logger logger = LoggerFactory.getLogger(PlcOfflineCounting.class);

	protected IPlcVariable<Integer> quantityVar;
	protected IPlcVariable<Integer> lastStopTimeVar;
	protected IPlcVariable<Integer> lastProductTimeVar;

	protected PlcProvider plcProvider;

	public PlcOfflineCounting() {
	}

	protected final Object lock = new Object();

	@Override
	public void processOfflineCounting() {
		synchronized (lock) {
			try {

				IPlcAdaptor plc = plcProvider.get();

				if (plc == null) {
					return;
				}

				Integer quantity = plc.read(quantityVar);
				Integer last_stop = plc.read(lastStopTimeVar);
				Integer last_product = plc.read(lastProductTimeVar);

				if (quantity != null && last_product != null && last_stop != null && quantity > 0 && last_stop > 0
						&& last_product > 0) {
					process(1000l * last_stop, 1000l * last_product, quantity);
				}

				reset();
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	protected void reset() {
		try {
			reset(lastProductTimeVar);
			reset(lastStopTimeVar);
			reset(quantityVar);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	protected void reset(IPlcVariable<Integer> var) throws PlcAdaptorException {
		var.setValue(0);
		plcProvider.get().write(var);
	}

	public IPlcVariable<Integer> getLastStopTimeVar() {
		return lastStopTimeVar;
	}

	public void setLastStopTimeVar(IPlcVariable<Integer> lastStopTimeVar) {
		this.lastStopTimeVar = lastStopTimeVar;
	}

	public IPlcVariable<Integer> getLastProductTimeVar() {
		return lastProductTimeVar;
	}

	public void setLastProductTimeVar(IPlcVariable<Integer> lastProductTimeVar) {
		this.lastProductTimeVar = lastProductTimeVar;
	}

	public IPlcVariable<Integer> getQuantityVar() {
		return quantityVar;
	}

	public void setQuantityVar(IPlcVariable<Integer> quantityVar) {
		this.quantityVar = quantityVar;
	}

	public PlcProvider getPlcProvider() {
		return plcProvider;
	}

	public void setPlcProvider(final PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
		plcProvider.addChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				IPlcAdaptor plc = plcProvider.get();
				plc.addDeviceStatusListener(new PlcOfflineCountingTrigger(PlcOfflineCounting.this));
			}
		});
	}
}
