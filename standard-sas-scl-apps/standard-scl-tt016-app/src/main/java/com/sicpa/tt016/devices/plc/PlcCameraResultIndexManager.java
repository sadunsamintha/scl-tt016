package com.sicpa.tt016.devices.plc;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTING;

public class PlcCameraResultIndexManager {

	protected final static Logger logger = LoggerFactory.getLogger(PlcCameraResultIndexManager.class);

	private static int MIN_INDEX_VALUE = 0;
	private static int MAX_INDEX_VALUE = 255;
	protected int previousIndex = 0;

	private PlcProvider plcProvider;

	private String plcCameraProductStatusNtfVarName;

	@Subscribe
	public void syncPlcCameraResultIndex(ApplicationFlowStateChangedEvent event) {
		if (event.getCurrentState().equals(STT_STARTING)) {
			String var = PlcLineHelper.getLinesVariableName(plcCameraProductStatusNtfVarName).get(0);

			try {
				previousIndex = PlcCameraResultParser.getPlcCameraResultIndex(readPlcVar(var));
			} catch (Exception e) {
				previousIndex = 0;
			}
		}
	}

	/**
	 * Method that returns the difference between the current supplied index and the previous one. Each call to this
	 * method will update the previous index by one.
	 *
	 * @param index
	 * @return
	 */
	public int getIndexDifference(int index) {
		checkIndexWithinRange(index);

		int indexDifference;

		if (index > previousIndex) {
			indexDifference = index - previousIndex;
			updatePreviousIndex(index);
		} else if (index < previousIndex) {
			indexDifference = (MAX_INDEX_VALUE - ((previousIndex - index) - 1));
			updatePreviousIndex(index);
		} else {
			indexDifference = 0;
		}

		return indexDifference;
	}

	private void updatePreviousIndex(int index) {
		previousIndex = (previousIndex == MAX_INDEX_VALUE) ? MIN_INDEX_VALUE : index;
	}

	private void checkIndexWithinRange(int index) {
		if (index < MIN_INDEX_VALUE || index > MAX_INDEX_VALUE) {
			throw new IllegalArgumentException("Index received is out of bounds. Index: " + index);
		}
	}

	private int readPlcVar(String varName) {
		try {
			return plcProvider.get().read(PlcVariable.createInt32Var(varName));
		} catch (PlcAdaptorException e) {
			logger.error("Couldn't read PLC var: {}", varName);
		}

		return 0;
	}

	public void setPreviousIndex(int previousIndex) {
		this.previousIndex = previousIndex;
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}

	public void setPlcCameraProductStatusNtfVarName(String plcCameraProductStatusNtfVarName) {
		this.plcCameraProductStatusNtfVarName = plcCameraProductStatusNtfVarName;
	}

	public int getPreviousIndex() {
		return previousIndex;
	}
}
