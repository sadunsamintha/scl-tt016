package com.sicpa.standard.sasscl.wizard.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.ApplicationTypePanel;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.ProjectNamePanel;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.ProjectPathPanel;

public class MandatoryActionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JPanel panel;

	public MandatoryActionPanel() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill"));
		JScrollPane scroll = new JScrollPane(getPanel());
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(scroll, "grow");
	}

	public JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new MigLayout("wrap 1"));
			panel.add(new ApplicationTypePanel(), "grow");
			panel.add(new ProjectNamePanel(), "grow");
			panel.add(new ProjectPathPanel(), "grow");
		}
		return panel;
	}

	public List<IConfigAction> getActions() {
		List<IConfigAction> res = new ArrayList<IConfigAction>();
		for (Component comp : getPanel().getComponents()) {
			if (comp instanceof IActionProvider) {
				res.add(((IActionProvider) comp).provide());
			}
		}
		return res;
	}
}
