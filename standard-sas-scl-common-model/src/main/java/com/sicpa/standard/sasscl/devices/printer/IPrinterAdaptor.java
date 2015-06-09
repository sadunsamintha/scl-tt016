package com.sicpa.standard.sasscl.devices.printer;

import com.sicpa.standard.sasscl.business.coding.ICodeReceiver;
import com.sicpa.standard.sasscl.devices.IStartableDevice;

import java.util.List;

public interface IPrinterAdaptor extends IStartableDevice, ICodeReceiver {

	void sendCodesToPrint(List<String> codes) throws PrinterAdaptorException;

	/**
	 * start printing
	 */
	@Override
	public void start() throws PrinterAdaptorException;

	/**
	 * stop printing
	 */
	@Override
	public void stop() throws PrinterAdaptorException;

	/**
	 * reset code buffer
	 */
	public void resetCodes() throws PrinterAdaptorException;

	/**
	 * shutdown printer
	 */
	public void switchOff() throws PrinterAdaptorException;

	/**
	 * switch on printer
	 */
	public void switchOn() throws PrinterAdaptorException;

}
