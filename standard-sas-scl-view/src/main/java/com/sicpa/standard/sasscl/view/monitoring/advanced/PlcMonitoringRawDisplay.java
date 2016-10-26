package com.sicpa.standard.sasscl.view.monitoring.advanced;

import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.isLineVariable;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.sicpa.standard.client.common.device.plc.IPLCVariableMappping;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.unit.PlcUnit;
import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;

@SuppressWarnings("serial")
public class PlcMonitoringRawDisplay extends JPanel implements IMonitoringViewComponent {

	private static final String TAB_LINE_PREFIX = "line:";
	private static final String TAB_CABINET = "cabinet";

	private JTabbedPane tabbedPane;
	private final Map<String, JLabel> labelByVarName = new TreeMap<>();
	private final Map<String, JPanel> tabByLineKey = new HashMap<>();

	private IPLCVariableMappping plcVarMap;

	public PlcMonitoringRawDisplay() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill, inset 0 0 0 0, gap 0 0 0 0"));
		add(getTabPane(), "grow, h 430!");
	}

	@Override
	public void update(PlcMonitoringModel model) {
		synchronized (model.getValueByVar()) {
			for (Entry<String, String> entry : model.getValueByVar().entrySet()) {
				String varName = entry.getKey();
				String tabIndexKey = getTabKey(varName);
				JPanel panel = getOrCreateTabPanel(tabIndexKey);

				JLabel labelValue = getOrCreateLabelDisplay(varName, panel);

				if (PlcLineHelper.isLineTypeVariable(varName)) {
					labelValue.setText(getLineVarTypeUnit(entry.getValue()));
				} else {
					labelValue.setText(entry.getValue());
				}
			}
		}
	}

	private JLabel getOrCreateLabelDisplay(String varName, JPanel container) {
		JLabel labelValue = labelByVarName.get(varName);
		if (labelValue == null) {
			labelValue = createLabel(varName, container);
			labelByVarName.put(varName, labelValue);
		}
		return labelValue;
	}

	private JPanel getOrCreateTabPanel(String tabIndexKey) {
		JPanel panel = tabByLineKey.get(tabIndexKey);
		if (panel == null) {
			panel = new JPanel(new MigLayout(""));
			addTab(tabIndexKey, panel);
			tabByLineKey.put(tabIndexKey, panel);
		}
		return panel;
	}

	private String getTabKey(String varName) {
		if (isLineVariable(varName)) {
			int line = PlcLineHelper.getLineIndex(varName);
			return TAB_LINE_PREFIX + line;
		} else {
			return TAB_CABINET;
		}
	}

	private void addTab(String key, JPanel panel) {
		getTabPane().addTab(key, new JScrollPane(panel));
	}

	private JLabel createLabel(String varName, JPanel container) {
		JLabel labelValue = new JLabel();

		JLabel label = new JLabel(getVarLogicalName(varName) + " : ");
		label.setFont(new Font(SicpaFont.getFont(12).getName(), Font.BOLD, 12));
		labelValue.setFont(new Font(SicpaFont.getFont(12).getName(), Font.BOLD, 12));
		container.add(label, "grow");

		container.add(labelValue, "grow,wrap");

		return labelValue;
	}

	private JTabbedPane getTabPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
		}
		return tabbedPane;
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public String getConstraints() {
		return "newline,grow,pushx";
	}

	public void setPlcVarMap(IPLCVariableMappping plcVarMap) {
		this.plcVarMap = plcVarMap;
	}

	private String getVarLogicalName(String varPhysicalName) {
		return PlcLineHelper.isLineVariable(varPhysicalName)
				? plcVarMap.getLogicalName(PlcLineHelper.replaceLineIndexWithPlaceHolder(varPhysicalName))
				: plcVarMap.getLogicalName(varPhysicalName);
	}

	private String getLineVarTypeUnit(String value) {
		return Boolean.parseBoolean(value) ? PlcUnit.PULSES.toString() : PlcUnit.MS.toString();
	}
}
