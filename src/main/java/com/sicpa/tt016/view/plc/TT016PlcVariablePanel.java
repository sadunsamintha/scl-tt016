package com.sicpa.tt016.view.plc;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroup;
import com.sicpa.standard.sasscl.view.config.plc.PlcVariablePanel;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class TT016PlcVariablePanel extends PlcVariablePanel {
	private static final Logger LOGGER = LoggerFactory.getLogger(TT016PlcVariablePanel.class);

	protected HashMap<String, Boolean> lineGroupVisibilityMap;

	public TT016PlcVariablePanel(List<PlcVariableGroup> groups, HashMap<String, Boolean> lineGroupVisibilityMap) {
		super(groups);
		EventBusService.register(this);
		this.lineGroupVisibilityMap = lineGroupVisibilityMap;
		initGUI();
	}

	protected void initGUI() {
		setLayout(new MigLayout());
		for (PlcVariableGroup grp : groups) {
			if (lineGroupVisibilityMap != null && lineGroupVisibilityMap.containsKey(grp.getDescription()) && 
					lineGroupVisibilityMap.get(grp.getDescription())) {
				add(new PanelGroup(grp), "growx,pushx,wrap");
			}
		}
	}
	
	public HashMap<String, Boolean> getLineGroupVisibilityMap() {
		return lineGroupVisibilityMap;
	}
}
