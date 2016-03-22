package com.sicpa.standard.sasscl.devices.plc.impl;

import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.getLineIndex;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.getLinesVariableName;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.sasscl.controller.view.event.LineSpeedEvent;
import com.sicpa.standard.sasscl.devices.plc.IPlcListener;
import com.sicpa.standard.sasscl.devices.plc.IPlcValuesLoader;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class PlcSpeedHandler {

	private static final Logger logger = LoggerFactory.getLogger(PlcSpeedHandler.class);

	private static final short SYSTEM_TYPE_TOBACCO = 3;

	private PlcProvider plcProvider;
	private String lineSpeedVarName;
	private String productFreqVarName;
	private String systemTypeVarName;
	private IPlcValuesLoader loader;

	private final Map<Integer, Short> systemTypes = new HashMap<>();

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
		plcProvider.addChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				addLineSpeedListener();
				addProductFreqListener();
			}
		});
	}

	private void addLineSpeedListener() {
		plcProvider.get().addPlcListener(new IPlcListener() {
			@Override
			public void onPlcEvent(PlcEvent event) {
				handleLineSpeedEvent(event);
			}

			@Override
			public List<String> getListeningVariables() {
				return new ArrayList<>(getLinesVariableName(lineSpeedVarName));
			}
		});
	}

	private void addProductFreqListener() {
		plcProvider.get().addPlcListener(new IPlcListener() {
			@Override
			public void onPlcEvent(PlcEvent event) {
				handleProductFreqEvent(event);
			}

			@Override
			public List<String> getListeningVariables() {
				return new ArrayList<>(getLinesVariableName(productFreqVarName));
			}
		});
	}

	private void handleLineSpeedEvent(PlcEvent event) {
		int lineIndex = getLineIndex(event.getVarName());
		if (!isLineActive(lineIndex)) {
			return;
		}
		if (!isTobacco(event)) {
			handleLineSpeedEvent(lineIndex, event);
		}
	}

	private void handleProductFreqEvent(PlcEvent event) {
		int lineIndex = getLineIndex(event.getVarName());
		if (!isLineActive(lineIndex)) {
			return;
		}
		if (isTobacco(event)) {
			handleProductFreqEvent(lineIndex, event);
		}
	}

	private boolean isLineActive(int lineIndex) {
		return plcProvider.get().isLineActive(lineIndex);
	}

	private void handleProductFreqEvent(int lineIndex, PlcEvent event) {
		String value = String.valueOf(event.getValue()) + " PACKS/MIN";
		logger.debug("speed:" + value);
		EventBusService.post(new LineSpeedEvent(lineIndex, value));
	}

	private void handleLineSpeedEvent(int lineIndex, PlcEvent event) {
		String value = String.valueOf(event.getValue()) + " M/MIN";
		logger.debug("speed:" + value);
		EventBusService.post(new LineSpeedEvent(lineIndex, value));
	}

	private boolean isTobacco(PlcEvent event) {
		Short systemType = getSystemTypesByLine().get(getLineIndex(event.getVarName()));
		return (systemType != null) && systemType.equals(SYSTEM_TYPE_TOBACCO);
	}

	private Map<Integer, Short> getSystemTypesByLine() {
		if (systemTypes.isEmpty()) {
			for (Entry<Integer, StringMap> entry : loader.getValues().entrySet()) {
				int lineIndex = entry.getKey();
				for (Entry<String, String> e : entry.getValue().entrySet()) {
					if (e.getKey().equals(systemTypeVarName)) {
						systemTypes.put(lineIndex, Short.parseShort(e.getValue()));
					}
				}
			}
		}
		return systemTypes;
	}

	public void setSystemTypeVarName(String systemTypeVarName) {
		this.systemTypeVarName = systemTypeVarName;
	}

	public void setLineSpeedVarName(String lineSpeedVarName) {
		this.lineSpeedVarName = lineSpeedVarName;
	}

	public void setProductFreqVarName(String productFreqVarName) {
		this.productFreqVarName = productFreqVarName;
	}

	public void setLoader(IPlcValuesLoader loader) {
		this.loader = loader;
	}
}
