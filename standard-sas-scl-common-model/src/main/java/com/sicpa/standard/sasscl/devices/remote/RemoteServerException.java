/**
 * Author	: YYang
 * Date		: Jul 21, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.remote;

import java.text.MessageFormat;

import com.sicpa.standard.sasscl.devices.DeviceException;

/**
 * 
 * This exception is thrown when there is exception related to remote server occurs for instance connection.
 * 
 * @author YYang
 * 
 */
public class RemoteServerException extends DeviceException {

	private static final long serialVersionUID = 3530006777708937167L;

	protected boolean business;

	public RemoteServerException() {
	}

	public RemoteServerException(final String message) {
		super(message);
	}

	public RemoteServerException(final Throwable cause) {
		super(cause);
	}

	public RemoteServerException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public RemoteServerException(final Throwable cause, final String msgKey, final Object... msgParam) {
		super(MessageFormat.format(msgKey, msgParam), cause);
	}

	public void setBusiness(final boolean business) {
		this.business = business;
	}

	public boolean isBusiness() {
		return this.business;
	}
}
