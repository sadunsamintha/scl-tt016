package com.sicpa.tt016.view.plc;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.IPlcParamSender;
import com.sicpa.standard.sasscl.devices.plc.IPlcValuesLoader;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcRequest;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroup;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroupEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.view.config.plc.MultiEditablePlcVariablesSet;
import com.sicpa.tt016.model.TT016ProductionMode;

import lombok.Setter;

/**
 * Specialization of Standard Set. This set will work with a controller to
 * handle field value changes. Also save and discard buttons were added to
 * control when to send updated values to PLC
 */
public class TT016MultiEditablePlcVariablesSet extends MultiEditablePlcVariablesSet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(TT016MultiEditablePlcVariablesSet.class);

	protected TT016MultiEditablePlcVariablesController editablePlcVariablesController;
	private String productionBehaviorVar;
	private PlcProvider plcProvider;
	private IPlcParamSender plcParamSender;
	private IPlcValuesLoader plcValuesLoader;
	protected static HashMap<String, Boolean> lineGroupVisibilityMap = new HashMap<String, Boolean>();

	protected JButton saveModificationsButton;
	protected JButton cancelModificationsButton;
	protected Map<String, TT016PlcVariablePanel> tt016PanelsLines = new HashMap<>();

	private static final String SAS_MODE = "PRODUCTIONCONFIG-SAS";
	
	@Setter
	private int lineCount = 3;

	public void setEditablePlcVariablesController(
			final TT016MultiEditablePlcVariablesController editablePlcVariablesController) {
		this.editablePlcVariablesController = editablePlcVariablesController;
	}

	@Override
	public void newPlcGroupEditable(PlcVariableGroupEvent evt) {
		ThreadUtils.invokeLater(() -> {
			TT016PlcVariablePanel p = tt016PanelsLines.get(evt.getLineId());
			if (p == null) {
				for (PlcVariableGroup plcVarGroup : evt.getGroups()) {
					if (!lineGroupVisibilityMap.containsKey(plcVarGroup.getDescription())) {
						lineGroupVisibilityMap.put(plcVarGroup.getDescription(), true);
					}
				}

				p = new TT016PlcVariablePanel(evt.getGroups(), lineGroupVisibilityMap);
				tt016PanelsLines.put(evt.getLineId(), p);
				addLinePanel(p, evt.getLineId());
			}
		});
	}

	@Subscribe
	public void productionParametersChanged(final ProductionParametersEvent evt) {
		if (productionBehaviorVar.toUpperCase().equals(SAS_MODE)
				&& evt != null & evt.getProductionParameters() != null) {
			Boolean isVisible;

			if (evt.getProductionParameters().getProductionMode().equals(ProductionMode.EXPORT)
					|| evt.getProductionParameters().getProductionMode().equals(TT016ProductionMode.AGING)
					|| evt.getProductionParameters().getProductionMode().equals(ProductionMode.REFEED_NORMAL)) {
				isVisible = false;
				saveAndSendValueToPlc("PARAM_LINE_INHIBIT_LBL_APP_OR_AIR_DRYER", String.valueOf(true));
			} else {
				isVisible = true;
				saveAndSendValueToPlc("PARAM_LINE_INHIBIT_LBL_APP_OR_AIR_DRYER", String.valueOf(false));
			}
		}else {
			saveAndSendValueToPlc("PARAM_LINE_INHIBIT_LBL_APP_OR_AIR_DRYER", String.valueOf(true));
		}

			// To uncomment if needed to hide the PLC Label Applicator variable settings
			// from GUI
//			lineGroupVisibilityMap.put("plc.config.line.group.lblapp", isVisible);

		ThreadUtils.invokeLater(() -> {
			tt016PanelsLines.clear();
			handleLanguageSwitch(null);
			this.editablePlcVariablesController.reload();
		});
		
	}

	private void saveAndSendValueToPlc(String varName, String value) {
		for (int lineIndex = 1; lineIndex < lineCount+1; lineIndex++) {
			saveValue(varName, value, lineIndex);
			if (shouldBeSentToPlc()) {
				try {
					plcParamSender.sendToPlc(varName, value, lineIndex);
					plcProvider.get().executeRequest(PlcRequest.RELOAD_PLC_PARAM);
				} catch (PlcAdaptorException e) {
					LOGGER.error("", e);
					EventBusService.post(new MessageEvent(this, MessageEventKey.PLC.ERROR_SENDING_PARAM, e, varName));
				}
			} else {
				LOGGER.debug("PLC is not presented or disconnect. All received updates will be ignored");
			}
		}
	}

	private void saveValue(String varName, String value, int lineIndex) {
		if (lineIndex > 0) {
			plcValuesLoader.saveLineNewValue(varName, value, lineIndex);
		} else {
			plcValuesLoader.saveCabinetNewValue(varName, value);
		}
	}

	private boolean shouldBeSentToPlc() {
		IPlcAdaptor plc = plcProvider != null ? plcProvider.get() : null;
		if (plc != null) {
			if (plc.isConnected()) {
				return true;
			}
		}
		return false;
	}

	@Subscribe
	public void handleNewPlcGroupEditable(PlcVariableGroupEvent evt) {
		ThreadUtils.invokeLater(() -> newPlcGroupEditable(evt));
	}

	public Map<String, TT016PlcVariablePanel> getTt016PanelsLines() {
		return tt016PanelsLines;
	}

	public void setProductionBehaviorVar(String productionBehaviorVar) {
		this.productionBehaviorVar = productionBehaviorVar;
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}

	public void setPlcParamSender(IPlcParamSender plcParamSender) {
		this.plcParamSender = plcParamSender;
	}

	public void setPlcValuesLoader(IPlcValuesLoader plcValuesLoader) {
		this.plcValuesLoader = plcValuesLoader;
	}
}
