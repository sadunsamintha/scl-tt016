/**
 * Author	: YYang
 * Date		: Oct 12, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.plc;

import java.io.Serializable;

public class PlcRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	public final static PlcRequest START = new PlcRequest("start");
	public final static PlcRequest RUN = new PlcRequest("run");
	public final static PlcRequest STOP = new PlcRequest("stop");
	public final static PlcRequest RELOAD_PLC_PARAM = new PlcRequest("reload PLC parameters");
	public final static PlcRequest SEND_JAVA_WARNING_ERROR = new PlcRequest("send java warning and error");

	protected final String description;

	public PlcRequest(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

}
