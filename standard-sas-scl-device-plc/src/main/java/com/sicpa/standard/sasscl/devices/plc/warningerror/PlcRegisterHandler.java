package com.sicpa.standard.sasscl.devices.plc.warningerror;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.devices.plc.IPlcListener;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.messages.IssueSolvedMessage;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class PlcRegisterHandler implements IPlcListener {

	private static final Logger logger = LoggerFactory.getLogger(PlcRegisterHandler.class);

	private PlcProvider plcProvider;
	private String lineRegisterVarName;
	private String cabRegisterVarName;
	private String lineSecondaryRegisterVarName;

	private final List<String> cabinetErrorsList = new ArrayList<>();
	private final List<String> lineErrorsList = new ArrayList<>();
	private final List<String> lineErrorsSecondaryList = new ArrayList<>();
	

	// previous register value, in order to fire issue solved event
	private Integer previousCabinetRegisterValue = 0;
	private final Map<String, Integer> mapPreviousRegister = new HashMap<>();

	public PlcRegisterHandler() {
	}

	protected List<String> getCabinetErrorsInRegister(int registerValue) {
		return getErrorsInRegister(cabinetErrorsList, registerValue);
	}

	protected List<String> getLineErrorsInRegister(int registerValue) {
		return getErrorsInRegister(lineErrorsList, registerValue);
	}
	
	protected List<String> getLineErrorsInSecondaryRegister(int registerValue) {
		return getErrorsInRegister(lineErrorsSecondaryList, registerValue);
	}

	protected List<String> getErrorsInRegister(List<String> errorAvailable, int registerValue) {

		if (errorAvailable == null || errorAvailable.isEmpty() || registerValue == 0) {
			return Collections.emptyList();
		}

		List<String> res = new ArrayList<String>();
		String binaryString = Integer.toBinaryString(registerValue);
		binaryString = StringUtils.leftPad(binaryString, 32, "0");
		binaryString = StringUtils.reverse(binaryString);
		for (int i = 0; i < binaryString.length(); i++) {
			if (binaryString.charAt(i) == '1') {
				String descriptor = errorAvailable.get(i);
				if (descriptor != null) {
					res.add(descriptor);
				}
			}
		}
		return res;
	}

	protected void fireMessage(final String key, final Object... params) {
		logger.debug("plc error event handling", Messages.get(key));
		MessageEvent evt = new MessageEvent(this, key, params);
		EventBusService.post(evt);
	}

	protected void fireIssueSolved(String key) {
		IssueSolvedMessage msg = new IssueSolvedMessage(key, plcProvider.get());
		EventBusService.post(msg);
	}

	protected void onCabinetErrorRegister(Integer registerValue) {
		synchronized (mapPreviousRegister) {
			handleCabinetRegister(previousCabinetRegisterValue, registerValue);
			previousCabinetRegisterValue = registerValue;
		}
	}

	protected void handleCabinetRegister(Integer previousRegister, Integer currentRegister) {
		List<String> previouses = getCabinetErrorsInRegister(previousRegister);
		List<String> currents = getCabinetErrorsInRegister(currentRegister);

		for (String previousDesc : previouses) {
			if (!currents.contains(previousDesc)) {
				fireIssueSolved(previousDesc);
			}
		}

		for (String msgDescriptor : currents) {
			fireMessage(msgDescriptor);
		}
	}

	protected void handleLineRegister(Integer previousRegister, Integer currentRegister, int lineIndex) {
		List<String> previouses = getLineErrorsInRegister(previousRegister);
		List<String> currents = getLineErrorsInRegister(currentRegister);

		for (String previousDesc : previouses) {
			if (!currents.contains(previousDesc)) {
				fireIssueSolved(previousDesc);
			}
		}

		for (String msgDescriptor : currents) {
			fireMessage(msgDescriptor, lineIndex);
		}
	}
	
	protected void handleSecondaryLineRegister(Integer previousRegister, Integer currentRegister, int lineIndex) {
		List<String> previouses = getLineErrorsInSecondaryRegister(previousRegister);
		List<String> currents = getLineErrorsInSecondaryRegister(currentRegister);

		for (String previousDesc : previouses) {
			if (!currents.contains(previousDesc)) {
				fireIssueSolved(previousDesc);
			}
		}

		for (String msgDescriptor : currents) {
			fireMessage(msgDescriptor, lineIndex);
		}
	}

	protected void onLineErrorRegister(Integer registerValue, String varName) {
		
		synchronized (mapPreviousRegister) {
			List<String> lineWarningErrorVariables = PlcLineHelper.getLinesVariableName(lineRegisterVarName);
			Integer previousRegister = mapPreviousRegister.get(varName);
			if (previousRegister == null) {
				previousRegister = 0;
			}
			
			if (lineWarningErrorVariables != null && lineWarningErrorVariables.contains(varName)){
				handleLineRegister(previousRegister, registerValue, PlcLineHelper.getLineIndex(varName));
			}else {
				handleSecondaryLineRegister(previousRegister, registerValue, PlcLineHelper.getLineIndex(varName));
			}
			mapPreviousRegister.put(varName, registerValue);
		}
	}
	

	@Override
	public void onPlcEvent(PlcEvent event) {

		Integer registerValue = (Integer) event.getValue();

		if (cabRegisterVarName.equals(event.getVarName())) {
			onCabinetErrorRegister(registerValue);
		} else { 
			onLineErrorRegister(registerValue, event.getVarName());
		}
	}

	// ================== getter and setter ======================

	public List<String> getListeningVariables() {
		List<String> vars = new ArrayList<String>();
		vars.add(cabRegisterVarName);
		vars.addAll(PlcLineHelper.getLinesVariableName(lineRegisterVarName));
		vars.addAll(PlcLineHelper.getLinesVariableName(lineSecondaryRegisterVarName));
		return vars;
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
		this.plcProvider.addChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				plcProvider.get().addPlcListener(PlcRegisterHandler.this);
			}
		});
	}

	public List<String> getCabinetErrorsList() {
		return cabinetErrorsList;
	}

	public void setCabinetErrorsList(List<String> cabinetErrorMsgDescriptorList) {
		this.cabinetErrorsList.addAll(cabinetErrorMsgDescriptorList);
	}

	public List<String> getLineErrorsList() {
		return lineErrorsList;
	}

	public void setLineErrorsList(List<String> linePlcWarningErrorMsgDescriptorList) {
		this.lineErrorsList.addAll(linePlcWarningErrorMsgDescriptorList);
	}

	public List<String> getLineErrorsSecondaryList() {
		return lineErrorsSecondaryList;
	}
	
	public void setLineErrorsSecondaryList(List<String> lineSecondaryPlcWarningErrorMsgDescriptorList) {
		this.lineErrorsSecondaryList.addAll(lineSecondaryPlcWarningErrorMsgDescriptorList);
	}
	

	public void setCabRegisterVarName(String cabRegisterVarName) {
		this.cabRegisterVarName = cabRegisterVarName;
	}

	public void setLineRegisterVarName(String lineRegisterVarName) {
		this.lineRegisterVarName = lineRegisterVarName;
	}

	public void setLineSecondaryRegisterVarName(String lineSecondaryRegisterVarName) {
		this.lineSecondaryRegisterVarName = lineSecondaryRegisterVarName;
	}
	
}
