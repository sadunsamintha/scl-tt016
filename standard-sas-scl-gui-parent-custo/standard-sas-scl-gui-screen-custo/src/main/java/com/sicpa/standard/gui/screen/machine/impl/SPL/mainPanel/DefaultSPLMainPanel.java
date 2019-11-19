package com.sicpa.standard.gui.screen.machine.impl.SPL.mainPanel;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.AbstractStatisticsPanel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.DefaultStatisticsPanel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.StatisticsModel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.systemInfo.AbstractSystemInfoPanel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.systemInfo.DefaultSystemInfoPanel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.systemInfo.SystemInfoModel;

public class DefaultSPLMainPanel extends AbstractSPLMainPanel {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				DefaultSPLMainPanel p = new DefaultSPLMainPanel(DefaultSystemInfoPanel.class,
						DefaultStatisticsPanel.class);
				f.getContentPane().add(p);
				f.pack();
				f.setVisible(true);
			}
		});
	}

	protected AbstractSystemInfoPanel systemPanel;
	protected AbstractStatisticsPanel statsPanel;

	private Class<? extends AbstractSystemInfoPanel> systemClass;
	private Class<? extends AbstractStatisticsPanel> statsClass;

	public DefaultSPLMainPanel(final Class<? extends AbstractSystemInfoPanel> systemClass,
			final Class<? extends AbstractStatisticsPanel> statsClass) {
		this.systemClass = systemClass;
		this.statsClass = statsClass;
		this.initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("inset 0 0 0 0"));

		add(getSystemPanel(), "grow,spanx,pushx");
		add(Box.createGlue(), "pushy,grow,wrap");
		add(getStatsPanel(), "grow,spanx");
		add(Box.createGlue(), "pushy,grow");
	}

	public AbstractSystemInfoPanel getSystemPanel() {

		if (this.systemPanel == null) {
			try {
				this.systemPanel = this.systemClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.systemPanel;
	}

	public AbstractStatisticsPanel getStatsPanel() {
		if (this.statsPanel == null) {
			try {
				this.statsPanel = this.statsClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.statsPanel;
	}

	@Override
	public StatisticsModel getStatsModel() {
		return getStatsPanel().getModel();
	}

	@Override
	public SystemInfoModel getSystemInfoModel() {
		return getSystemPanel().getModel();
	}
}
