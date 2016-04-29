package com.sicpa.standard.sasscl.business.activation.offline.impl;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.business.activation.offline.IOfflineCounting;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

import java.util.List;

public class PlcOfflineCountingTrigger implements IDeviceStatusListener {

	private IOfflineCounting offlineCounting;

	private IPlcVariable<Integer> quantityVar;
	private IPlcVariable<Integer> lastStopTimeVar;
	private IPlcVariable<Integer> lastProductTimeVar;

	public PlcOfflineCountingTrigger(IOfflineCounting offlineCounting) {
		this.offlineCounting = offlineCounting;
	}

	@Override
	public void deviceStatusChanged(DeviceStatusEvent evt) {
		if (evt.getStatus().equals(DeviceStatus.CONNECTED)) {

			List<Integer> lineIndexes = PlcLineHelper.getLineIndexes();

			for (Integer lineIndex : lineIndexes) {
				offlineCounting.processOfflineCounting(
						(IPlcVariable<Integer>) PlcLineHelper.clone(quantityVar, lineIndex),
						(IPlcVariable<Integer>)	PlcLineHelper.clone(lastStopTimeVar, lineIndex),
						(IPlcVariable<Integer>) PlcLineHelper.clone(lastProductTimeVar, lineIndex));
			}
		}
	}

	public void setPlcProvider(final PlcProvider plcProvider) {
		plcProvider.addChangeListener(
				arg0 -> plcProvider.get().addDeviceStatusListener(PlcOfflineCountingTrigger.this));
	}

	public void setQuantityVar(IPlcVariable<Integer> quantityVar) {
		this.quantityVar = quantityVar;
	}

	public void setLastStopTimeVar(IPlcVariable<Integer> lastStopTimeVar) {
		this.lastStopTimeVar = lastStopTimeVar;
	}

	public void setLastProductTimeVar(IPlcVariable<Integer> lastProductTimeVar) {
		this.lastProductTimeVar = lastProductTimeVar;
	}
}
