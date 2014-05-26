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

	public final static PlcRequest START = new PlcRequest(1, "start");
	public final static PlcRequest RUN = new PlcRequest(2, "run");
	public final static PlcRequest STOP = new PlcRequest(3, "stop");
	public final static PlcRequest RELOAD_PLC_PARAM = new PlcRequest(4, "reload PLC parameters");
	public final static PlcRequest SEND_JAVA_WARNING_ERROR = new PlcRequest(5, "send java warning and error");      
	
	protected final int id;

	protected final String description;

	public PlcRequest(int id, String description) {
		this.id = id;
		this.description = description;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof PlcRequest) {
			return ((PlcRequest) obj).id == this.id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	public int getId() {
		return this.id;
	}

	public String getDescription() {
		return this.description;
	}

}
