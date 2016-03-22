package com.sicpa.standard.sasscl.devices.plc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sicpa.standard.sasscl.devices.AbstractStartableDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;

public abstract class AbstractPlcAdaptor extends AbstractStartableDevice implements IPlcAdaptor {

	protected final Set<IPlcListener> allPlcVariableListeners = new HashSet<>();
	protected final Map<String, Set<IPlcListener>> varListenersMap = new HashMap<>();

	public AbstractPlcAdaptor() {
		setName("PLC");
	}

	@Override
	protected abstract void doConnect() throws PlcAdaptorException;

	@Override
	protected abstract void doDisconnect() throws PlcAdaptorException;

	@Override
	public void start() throws PlcAdaptorException {
		try {
			super.start();
		} catch (DeviceException e) {
			throw new PlcAdaptorException(e);
		}
	}

	@Override
	public void stop() throws PlcAdaptorException {
		try {
			super.stop();
		} catch (DeviceException e) {
			throw new PlcAdaptorException(e);
		}
	}

	/**
	 * notify all listener about a plc var changed
	 * 
	 * @param event
	 */
	public void firePlcEvent(PlcEvent event) {

		synchronized (allPlcVariableListeners) {
			// notify listeners that subscribe for all the variables
			for (IPlcListener listener : allPlcVariableListeners) {
				listener.onPlcEvent(event);
			}
		}

		// notify listeners that only subscribe for this particular event
		Set<IPlcListener> listeners = varListenersMap.get(event.getVarName());

		if (listeners != null) {
			for (IPlcListener listener : listeners) {
				listener.onPlcEvent(event);
			}
		}
	}

	/**
	 * 
	 * to listen to all the variables of the PLC
	 * 
	 * @param listener
	 */
	public void addPlcListener(IPlcListener listener) {

		if (listener.getListeningVariables() != null && listener.getListeningVariables().size() != 0) {
			addPlcListener(listener, listener.getListeningVariables());
			return;
		}

		synchronized (allPlcVariableListeners) {
			allPlcVariableListeners.add(listener);
		}

		// make sure the varListenerMap do not have the passed in listener
		for (Set<IPlcListener> plcListeners : varListenersMap.values()) {
			plcListeners.remove(listener);
		}
	}

	/**
	 * 
	 * @see com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor#addPlcListener(com.sicpa.standard.sasscl.devices.plc.IPlcListener,
	 *      java.util.List)
	 */
	public void addPlcListener(IPlcListener listener, List<String> plcVariableNames) {

		if (plcVariableNames == null) {
			addPlcListener(listener);
		}

		synchronized (allPlcVariableListeners) {

			// add variables to varListenersMap
			if (allPlcVariableListeners.contains(listener)) {
				for (String varName : plcVariableNames) {
					if (varListenersMap.get(varName) == null) {
						varListenersMap.put(varName, new HashSet<>());
					}
				}
				return;
			}

			// ignore if the listener is registered to listen to all the
			// variables
			for (String varName : plcVariableNames) {
				Set<IPlcListener> plcListeners = varListenersMap.get(varName);
				if (plcListeners == null) {
					plcListeners = new HashSet<>();
					varListenersMap.put(varName, plcListeners);
				}
				if (!plcListeners.contains(listener)) {
					plcListeners.add(listener);
				}
			}
		}

	}

	/**
	 * 
	 * to remove the listener from all the variables
	 * 
	 * @param listener
	 */
	public void removePlcListener(IPlcListener listener) {

		synchronized (allPlcVariableListeners) {
			allPlcVariableListeners.remove(listener);
		}

		// remove the listener from the varListenerMap too
		for (Set<IPlcListener> plcListeners : varListenersMap.values()) {
			plcListeners.remove(listener);
		}
	}

	/**
	 * 
	 * @see com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor#removePlcListener(com.sicpa.standard.sasscl.devices.plc.IPlcListener,
	 *      java.util.List)
	 */
	public void removePlcListener(IPlcListener listener, List<String> plcVariableNames) {

		if (plcVariableNames == null) {
			return;
		}

		synchronized (allPlcVariableListeners) {
			if (allPlcVariableListeners.contains(listener)) {
				allPlcVariableListeners.remove(listener);

				// add listeners to the rest of the variables
				for (String var : varListenersMap.keySet()) {
					if (!plcVariableNames.contains(var)) {
						Set<IPlcListener> plcListeners = varListenersMap.get(var);
						if (plcListeners != null) {
							plcListeners.add(listener);
						}
					}
				}
				return;
			}
		}

		for (String varName : plcVariableNames) {
			Set<IPlcListener> plcListeners = varListenersMap.get(varName);
			if (plcListeners != null) {
				plcListeners.remove(listener);
			}
		}
	}
}
