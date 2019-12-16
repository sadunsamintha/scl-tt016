package com.sicpa.standard.gui.screen.machine.impl.SPL;

import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;
import com.sicpa.standard.gui.screen.machine.PanelKey;
import com.sicpa.standard.gui.screen.machine.component.devicesStatus.AbstractDevicesStatusPanel;
import com.sicpa.standard.gui.screen.machine.component.devicesStatus.DefaultDevicesStatusPanel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.mainPanel.AbstractSPLMainPanel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.mainPanel.DefaultSPLMainPanel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.AbstractStatisticsPanel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.DefaultStatisticsPanel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.systemInfo.AbstractSystemInfoPanel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.systemInfo.DefaultSystemInfoPanel;

public abstract class AbstractSplFrame extends AbstractMachineFrame {

	public static final PanelKey KEY_STATISTICS_PANEL = new PanelKey( AbstractStatisticsPanel.class);
	public static final PanelKey KEY_SYSTEM_INFO_PANEL = new PanelKey( AbstractSystemInfoPanel.class);
	public static final PanelKey KEY_DEVICE_STATUS_PANEL = new PanelKey(AbstractDevicesStatusPanel.class);

	static {
		mapPanelClasses.put(KEY_STATISTICS_PANEL, DefaultStatisticsPanel.class);
		mapPanelClasses.put(KEY_SYSTEM_INFO_PANEL, DefaultSystemInfoPanel.class);
		mapPanelClasses.put(KEY_DEVICE_STATUS_PANEL, DefaultDevicesStatusPanel.class);
	}

	private AbstractDevicesStatusPanel devicesStatusPanel;
	private AbstractSPLMainPanel mainPanel;

	public AbstractSplFrame(final SplViewController controller) {
		super(controller);
	}

	@Override
	protected void initController() {
		super.initController();
		getController().setSystemModel(getMainPanel().getSystemInfoModel());
		getController().setStatsModel(getMainPanel().getStatsModel());
		getController().setDevicesModel(getDevicesStatusPanel().getModel());
	}

	@Override
	protected void initGUI() {
		super.initGUI();
	}

	@Override
	protected void buildRightPanel() {
		setRightPanel(getDevicesStatusPanel());
		setRightPanelPreferredWidth(250);
	}

	protected AbstractDevicesStatusPanel getDevicesStatusPanel() {
		if (this.devicesStatusPanel == null) {
			this.devicesStatusPanel = (AbstractDevicesStatusPanel) createPanel(KEY_DEVICE_STATUS_PANEL);
			this.devicesStatusPanel.setName("devicesStatusPanel");
		}
		return this.devicesStatusPanel;
	}

	@Override
	protected void buildLayeredLeftPanel() {

		addLayerToLeftPanel(getMainPanel(), 10);
		addLayerToLeftPanel(getScrollingErrorFatal(), 20, true);
		addLayerToLeftPanel(getConfirmationPanel(), 30);
		getMainPanel().setVisible(true);
	}

	@SuppressWarnings("unchecked")
	public AbstractSPLMainPanel getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = new DefaultSPLMainPanel((Class<? extends AbstractSystemInfoPanel>) mapPanelClasses
					.get(KEY_SYSTEM_INFO_PANEL), (Class<? extends AbstractStatisticsPanel>) mapPanelClasses
					.get(KEY_STATISTICS_PANEL));
		}
		return this.mainPanel;
	}

	@Override
	public SplViewController getController() {
		return (SplViewController) super.getController();
	}

	@Override
	protected void buildFooter() {
		addFillerToFooter();
		addToFooter(getSelectProductButton());
	}
}
