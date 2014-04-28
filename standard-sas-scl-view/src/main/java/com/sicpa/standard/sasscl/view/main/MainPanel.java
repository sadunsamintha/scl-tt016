package com.sicpa.standard.sasscl.view.main;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;

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
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill,gap 0 0 0 0,inset 0 0 0 0"));
		add(systemInfoView, "grow,wrap, pushy");
		add(statsView, "grow,pushy");
		add(selectionDisplayView, "east");
		add(new JSeparator(JSeparator.VERTICAL), "east,growy");
	}

}
