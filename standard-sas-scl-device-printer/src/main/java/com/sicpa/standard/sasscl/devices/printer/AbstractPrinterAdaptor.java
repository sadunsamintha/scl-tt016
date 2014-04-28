/**
 * Author	: CDeAlmeida
 * Date		: 20 Jul 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.printer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.business.coding.RequestCodesEvent;
import com.sicpa.standard.sasscl.devices.AbstractStartableDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;

/**
 * <p>
 * This class provides a default implementation of <code>PrinterController</code>. Standard behaviors such as add and
 * remove listeners are handled here.
 * </p>
 * <p>
 * The developer only needs to subclass this abstract class and define the additional specific behaviors.
 * </p>
 * 
 * 
 * @author CDeAlmeida
 * 
 */
public abstract class AbstractPrinterAdaptor extends AbstractStartableDevice implements IPrinterAdaptor {

	protected final static Logger logger = LoggerFactory.getLogger(AbstractPrinterAdaptor.class);

	public AbstractPrinterAdaptor() {
		super();
		setName("Printer");
	}

	protected void notifyRequestCodesToPrint(final long numberCodes) {
		EventBusService.post(new RequestCodesEvent(numberCodes, this));
	}

	@Override
	public void start() throws PrinterAdaptorException {
		try {
			super.start();
		} catch (DeviceException e) {
			throw new PrinterAdaptorException(e);
		}
	}

	@Override
	public void stop() throws PrinterAdaptorException {
		try {
			super.stop();
		} catch (DeviceException e) {
			throw new PrinterAdaptorException(e);
		}
	}

	@Override
	protected abstract void doConnect() throws PrinterAdaptorException;

	@Override
	protected abstract void doDisconnect() throws PrinterAdaptorException;

}