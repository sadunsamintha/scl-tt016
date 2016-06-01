package com.sicpa.standard.sasscl.view.monitoring.advanced;

import static com.sicpa.standard.gui.components.scroll.SmallScrollBar.createLayerSmallScrollBar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.view.ISecuredComponentGetter;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.security.SasSclPermission;

@SuppressWarnings("serial")
public class PlcMonitoringView extends AbstractView<IMonitoringViewListener, PlcMonitoringModel> implements
		ISecuredComponentGetter {

	private final List<IMonitoringViewComponent> views = new ArrayList<>();
	private JCheckBox checkEnableMonitoring;
	private JPanel panelViews;

	public PlcMonitoringView() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,inset 0 0 0 0,gap 0 0 0 0"));
		add(getCheckEnableMonitoring(), "wrap");
		add(createLayerSmallScrollBar(new JScrollPane(getPanelViews())), "grow,push");
	}

	private void addPlcComponentToLayout(IMonitoringViewComponent view) {
		getPanelViews().add(view.getComponent(), view.getConstraints());
	}

	public JPanel getPanelViews() {
		if (panelViews == null) {
			panelViews = new JPanel(new MigLayout(""));
		}
		return panelViews;
	}

	public void setViews(Collection<IMonitoringViewComponent> comps) {
		for (IMonitoringViewComponent comp : comps) {
			addView(comp);
		}
	}

	public void addView(IMonitoringViewComponent view) {
		synchronized (views) {
			views.add(view);
		}
		addPlcComponentToLayout(view);
	}

	public JCheckBox getCheckEnableMonitoring() {
		if (checkEnableMonitoring == null) {
			checkEnableMonitoring = new JCheckBox(Messages.get("monitoring.plc.var.read"));
			checkEnableMonitoring.addActionListener(e -> checkEnableInputsActionPerformed());
		}
		return checkEnableMonitoring;
	}

	private void checkEnableInputsActionPerformed() {
		fireInputStateChanged(getCheckEnableMonitoring().isSelected());
	}

	private void fireInputStateChanged(boolean enabled) {
		for (IMonitoringViewListener listener : listeners) {
			listener.monitoringStateChanged(enabled);
		}
	}

	@Override
	public void modelChanged() {
		synchronized (views) {
			for (IMonitoringViewComponent view : views) {
				view.update(model);
			}
		}
	}

	@Override
	public Permission getPermission() {
		return SasSclPermission.ADVANCED_MONITORING;
	}

	@Override
	public String getTitle() {
		return "monitoring.advanced";
	}

	@Override
	public JComponent getComponent() {
		return this;
	}
}
