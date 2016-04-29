package com.sicpa.standard.sasscl.business.activation.offline;

import com.sicpa.standard.plc.value.IPlcVariable;

public interface IOfflineCounting {

	void processOfflineCounting(IPlcVariable<Integer> quantityVar, IPlcVariable<Integer> lastStopTimeVar,
	                            IPlcVariable<Integer> lastProductTimeVar);

}
