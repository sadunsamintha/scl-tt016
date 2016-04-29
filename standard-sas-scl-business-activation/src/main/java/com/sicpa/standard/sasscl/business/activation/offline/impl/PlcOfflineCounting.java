package com.sicpa.standard.sasscl.business.activation.offline.impl;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.business.activation.offline.AbstractOfflineCounting;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlcOfflineCounting extends AbstractOfflineCounting {

	private static final Logger logger = LoggerFactory.getLogger(PlcOfflineCounting.class);

	private PlcProvider plcProvider;

	protected final Object lock = new Object();

	@Override
	public void processOfflineCounting(IPlcVariable<Integer> quantityVar, IPlcVariable<Integer> lastStopTimeVar,
	                                   IPlcVariable<Integer> lastProductTimeVar) {
		synchronized (lock) {
			try {
				IPlcAdaptor plc = plcProvider.get();

				if (plc == null) {
					return;
				}

				Integer quantity = plc.read(quantityVar);
				Integer lastStop = plc.read(lastStopTimeVar);
				Integer lastProduct = plc.read(lastProductTimeVar);

				if (quantity != null && lastProduct != null && lastStop != null && quantity > 0 && lastStop > 0
						&& lastProduct > 0) {
					process(1000L * lastStop, 1000L * lastProduct, quantity);
				}

				reset(lastProductTimeVar);
				reset(lastStopTimeVar);
				reset(quantityVar);

			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	protected void reset(IPlcVariable<Integer> var) throws PlcAdaptorException {
		var.setValue(0);
		plcProvider.get().write(var);
	}

	public PlcProvider getPlcProvider() {
		return plcProvider;
	}

	public void setPlcProvider(final PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}
}
