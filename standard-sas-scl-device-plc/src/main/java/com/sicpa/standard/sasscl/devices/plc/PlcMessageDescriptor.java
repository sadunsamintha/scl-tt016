package com.sicpa.standard.sasscl.devices.plc;

import com.sicpa.standard.plc.value.IPlcVariable;

public class PlcMessageDescriptor {

	protected IPlcVariable<?> plcVariable;

	protected String messageKey;

	protected boolean blockable;

	public PlcMessageDescriptor() {
	}

	public PlcMessageDescriptor(IPlcVariable<?> plcVariable, String messageKey, boolean blockable) {
		this.plcVariable = plcVariable;
		this.messageKey = messageKey;
		this.blockable = blockable;
	}

	public IPlcVariable<?> getPlcVariable() {
		return this.plcVariable;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public void setPlcVariable(IPlcVariable<?> plcVariable) {
		this.plcVariable = plcVariable;
	}

	public boolean isBlockable() {
		return blockable;
	}

	public void setBlockable(boolean blockable) {
		this.blockable = blockable;
	}

}
