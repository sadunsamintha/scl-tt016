package com.sicpa.standard.sasscl.wizard.view.actionPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.l2fprod.common.swing.JDirectoryChooser;
import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.configaction.SetProjectPathAction;
import com.sicpa.standard.sasscl.wizard.view.IActionProvider;

public class ProjectPathPanel extends JPanel implements IActionProvider {

	private static final long serialVersionUID = 1L;
	private JTextField textPath;
	private JButton buttonBrowse;

	public ProjectPathPanel() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill"));
		add(new JLabel("Path where to create the project:"), "spanx");
		add(getTextPath(), "grow,push");
		add(getButtonBrowse());

	}

	@Override
	public IConfigAction provide() {
		return new SetProjectPathAction(getTextPath().getText());
	}

	public JTextField getTextPath() {
		if (textPath == null) {
			textPath = new JTextField("C:/workspace/ttxxx");
		}
		return textPath;
	}

	public JButton getButtonBrowse() {
		if (buttonBrowse == null) {
			buttonBrowse = new JButton("Browse...");
			buttonBrowse.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonBrowseActionPerformed();
				}
			});
		}
		return buttonBrowse;
	}

	private JDirectoryChooser chooser;

	private void buttonBrowseActionPerformed() {
		if (chooser == null) {
			chooser = new JDirectoryChooser();
			chooser.setMultiSelectionEnabled(false);
		}
		if (chooser.showOpenDialog(this) == JDirectoryChooser.APPROVE_OPTION) {
			getTextPath().setText(chooser.getSelectedFile().getAbsolutePath());
		}

	}
	@Override
	public boolean isAvailable(ApplicationType type) {
		return true;
	}
}
