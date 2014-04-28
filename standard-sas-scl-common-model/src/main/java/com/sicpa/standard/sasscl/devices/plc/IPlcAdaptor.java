package com.sicpa.standard.sasscl.devices.plc;

import java.util.List;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.devices.IStartableDevice;

public interface IPlcAdaptor extends IStartableDevice {

	/**
	 * send start request
	 */
	@Override
	void start() throws PlcAdaptorException;

	/**
	 * send stop request
	 */
	@Override
	void stop() throws PlcAdaptorException;

	/**
	 * to execute passed in request, e.g. start, stop & maintenance
	 * 
	 * @param request
	 *            to be executed
	 * @throws PlcAdaptorException
	 *             thrown if the executor is null
	 */
	void executeRequest(PlcRequest request) throws PlcAdaptorException;

	/**
	 * send the new value set in the instance of IPlcVariable to PLC again
	 * 
	 * @param var
	 *            to be written to PLC if var is not null
	 * @throws PlcAdaptorException
	 *             wrapping any exception thrown while writing or reloading
	 */
	void reloadPlcParameter(final IPlcVariable<?> var) throws PlcAdaptorException;

	/**
	 * to add listener that listen to all the PLC variables
	 * 
	 * @param listener
	 *            of all PLC variables
	 */
	void addPlcListener(final IPlcListener listener);

	/**
	 * to add listener that only listen to passed in PLC variables
	 * 
	 * @param listener
	 *            only listening to passed PLC variables
	 * @param plcVariableNames
	 *            names of PLC variables to be listened by the listener
	 */
	void addPlcListener(final IPlcListener listener, List<String> plcVariableNames);

	/**
	 * to remove listener from listening to all the PLC variables
	 * 
	 * @param listener
	 *            to be removed
	 */
	void removePlcListener(final IPlcListener listener);

	/**
	 * to remove listener from listening to passed in PLC variables
	 * 
	 * @param listener
	 *            to be removed on passed PLC variable names
	 * @param plcVariableNames
	 *            names of PLC variables that the listener is not going to follow
	 */
	void removePlcListener(final IPlcListener listener, List<String> plcVariableNames);

	/**
	 * read the value from the plc for the given variable
	 * 
	 * @param var
	 *            whose value is read
	 * @return read variable value. null if var is null
	 * @throws PlcAdaptorException
	 *             wrapping exception
	 */
	<T> T read(IPlcVariable<T> var) throws PlcAdaptorException;

	/**
	 * write a value to the plc
	 * 
	 * @param var
	 *            to be written
	 * @throws PlcAdaptorException
	 *             wrapping exception
	 */
	void write(IPlcVariable<?> var) throws PlcAdaptorException;

	/**
	 * register a PLC variable for notification
	 * 
	 * @param var
	 *            for which notification is registered
	 */
	public void registerNotification(IPlcVariable<?> var);

	String getPlcVersion();
}
