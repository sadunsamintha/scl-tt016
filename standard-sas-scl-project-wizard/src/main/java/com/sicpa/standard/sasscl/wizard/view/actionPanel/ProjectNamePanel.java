package com.sicpa.standard.sasscl.wizard.view.actionPanel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.configaction.SetProjectNameAction;
import com.sicpa.standard.sasscl.wizard.view.IActionProvider;

public class ProjectNamePanel extends JPanel implements IActionProvider {

	private static final long serialVersionUID = 1L;

	private JTextField textProjectName;

	public ProjectNamePanel() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout(""));
		add(new JLabel("Project name:"),"wrap");
		add(getTextProjectName(), "push,growx");
	}

	@Override
	public IConfigAction provide() {
		return new SetProjectNameAction(getTextProjectName().getText());
	}

	public JTextField getTextProjectName() {
		if (textProjectName == null) {
			textProjectName = new JTextField("ttxxx");
		}
		return textProjectName;
	}
	@Override
	public boolean isAvailable(ApplicationType type) {
		return true;
	}
}
