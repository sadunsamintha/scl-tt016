package com.sicpa.standard.sasscl.view.main;

import static com.sicpa.standard.client.common.security.SecurityService.hasPermission;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.security.ILoginListener;
import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.sasscl.security.SasSclPermission;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.main.statistics.StatisticsView;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {

	protected JComponent statsView;
	protected JComponent selectionDisplayView;
	protected JComponent systemInfoView;

	public MainPanel(JComponent statsView, JComponent selectionDisplayView, JComponent systemInfoView) {
		this.statsView = statsView;
		this.selectionDisplayView = selectionDisplayView;
		this.systemInfoView = systemInfoView;
		initGUI();
		addLoginListener();
		fireUserChanged();
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill,gap 0 0 0 0,inset 0 0 0 0"));
		add(systemInfoView, "grow,wrap, pushy");
		add(statsView, "grow,pushy");
		add(selectionDisplayView, "east");
		add(new JSeparator(JSeparator.VERTICAL), "east,growy");
	}
	
	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		userChanged();
	}

	private void addLoginListener() {
		SecurityService.addLoginListener(new ILoginListener() {
			@Override
			public void loginSucceeded(String login) {
				fireUserChanged();
			}

			@Override
			public void logoutCompleted(String login) {
				fireUserChanged();
			}
		});
	}
	
	private void fireUserChanged() {
		SwingUtilities.invokeLater(() -> userChanged());
	}

	protected void userChanged() {
		if (statsView instanceof StatisticsView) {
			((StatisticsView) statsView).getLabelTitle().setVisible(hasPermission(SasSclPermission.PRODUCTION_VIEW_STATISTICS));
			((StatisticsView) statsView).getPanelSeparator().setVisible(hasPermission(SasSclPermission.PRODUCTION_VIEW_STATISTICS));
			((StatisticsView) statsView).getPanelLineStats().setVisible(hasPermission(SasSclPermission.PRODUCTION_VIEW_STATISTICS));
			((StatisticsView) statsView).getPanelTotal().setVisible(hasPermission(SasSclPermission.PRODUCTION_VIEW_STATISTICS));
		}
	}

}
