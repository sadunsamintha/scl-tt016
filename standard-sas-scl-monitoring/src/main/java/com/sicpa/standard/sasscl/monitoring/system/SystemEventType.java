package com.sicpa.standard.sasscl.monitoring.system;

import java.io.Serializable;

import com.sicpa.standard.printer.controller.model.command.PrinterMessageId;

public class SystemEventType implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final SystemEventType APP_STARTED = new SystemEventType("APP_STARTED");
	public static final SystemEventType APP_EXITING = new SystemEventType("APP_EXITING");

	public static final SystemEventType STATE_CHANGED = new SystemEventType("STATE_CHANGED");

	public static final SystemEventType SELECT_PROD_PARAMETERS = new SystemEventType("SELECT_PROD_PARAMETERS");

	public static final SystemEventType APPLICATION_MESSAGE = new SystemEventType("APPLICATION_MESSAGE");
	public static final SystemEventType DEVICE_REMOTE_SERVER_DISCONNECTED = new SystemEventType(
			"DEVICE_REMOTE_SERVER_DISCONNECTED");
	public static final SystemEventType DEVICE_PRODUCTION_DISCONNECTED = new SystemEventType("DEVICE_DISCONNECTED");
	public static final SystemEventType DEVICE_REMOTE_SERVER_CONNECTED = new SystemEventType(
			"DEVICE_REMOTE_SERVER_CONNECTED");
	public static final SystemEventType DEVICE_PRODUCTION_CONNECTED = new SystemEventType("DEVICE_CONNECTED");

	public static final SystemEventType REMOTE_SERVER_CALL = new SystemEventType("REMOTE_SERVER_CALL");

	public static final SystemEventType UNCAUGHT_EXCEPTION = new SystemEventType("UNCAUGHT_EXCEPTION");

	public static final SystemEventType STATISTICS_CHANGED = new SystemEventType("STATISTICS_CHANGED");

	public static final SystemEventType PRODUCT_SCANNED = new SystemEventType("PRODUCT_SCANNED");
	public static final SystemEventType GET_CODE_FROM_ENCODER = new SystemEventType("GET_CODE_FROM_ENCODER");
	public static final SystemEventType GET_EXTENDED_CODE_FROM_ENCODER = new SystemEventType("GET_EXTENDED_CODE_FROM_ENCODER");

	// total of product last sent to remote server
	public static final SystemEventType LAST_SENT_TO_REMOTE_SERVER = new SystemEventType("LAST_SENT_TO_REMOTE_SERVER");
	public static final SystemEventType SENT_TO_REMOTE_SERVER_OK = new SystemEventType("SENT_TO_REMOTE_MASTER");
	public static final SystemEventType SENT_TO_REMOTE_SERVER_ERROR = new SystemEventType("SENT_TO_REMOTE_MASTER");

	public static final SystemEventType PRINTER_INK_LEVEL = new SystemEventType("PRINTER_INK_LEVEL");
	public static final SystemEventType PRINTER_MAKEUP_LEVEL = new SystemEventType("PRINTER_MAKEUP_LEVEL");

	public static final SystemEventType CAMERA_TRIGGER_TOO_FAST = new SystemEventType("CAMERA_TRIGGER_TOO_FAST");

	public static final SystemEventType OFFLINE_COUNTING = new SystemEventType("OFFLINE_COUNTING");

	protected String name;

	public SystemEventType(final String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SystemEventType other = (SystemEventType) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
