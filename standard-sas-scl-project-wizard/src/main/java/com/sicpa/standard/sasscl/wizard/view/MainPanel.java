package com.sicpa.standard.sasscl.wizard.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.divxdede.swing.busy.JBusyComponent;

import com.sicpa.standard.common.log.StdLogger;
import com.sicpa.standard.sasscl.wizard.ProjectConfigurator;
import com.sicpa.standard.sasscl.wizard.configaction.ConfigurationException;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class MainPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static StdLogger logger = new StdLogger(MainPanel.class);

	private MandatoryActionPanel mandatoryPanel;
	private SelectedComponentsPanel selectedPanels;
	private AvailableActionsPanel availablePanels;
	private JButton buttonGenerateProject;
	private JBusyComponent<JComponent> busyComp;
	private JPanel panel;

	public MainPanel() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill "));
		add(getBusyComp(), "grow");
	}

	public MandatoryActionPanel getMandatoryPanel() {
		if (mandatoryPanel == null) {
			mandatoryPanel = new MandatoryActionPanel();
		}
		return mandatoryPanel;
	}

	public SelectedComponentsPanel getSelectedPanels() {
		if (selectedPanels == null) {
			selectedPanels = new SelectedComponentsPanel();
		}
		return selectedPanels;
	}

	public AvailableActionsPanel getAvailablePanels() {
		if (availablePanels == null) {
			availablePanels = new AvailableActionsPanel();
			availablePanels.setSelectedComponentPanel(getSelectedPanels());
		}
		return availablePanels;
	}

	public JButton getButtonGenerateProject() {

		if (buttonGenerateProject == null) {
			buttonGenerateProject = new JButton("Generate project");
			buttonGenerateProject.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonGenerateProjectActionPerformed();
				}
			});
		}
		return buttonGenerateProject;
	}

	private void buttonGenerateProjectActionPerformed() {

		getBusyComp().setBusy(true);
		new Thread(new Runnable() {

			@Override
			public void run() {
				ProjectContext.prepare();
				ProjectConfigurator configurator = new ProjectConfigurator();
				for (IConfigAction action : getMandatoryPanel().getActions()) {
					configurator.addMandatoryConfigAction(action);
				}
				for (IConfigAction action : getSelectedPanels().getActions()) {
					configurator.addConfigAction(action);
				}
				try {
					configurator.configureProject();
				} catch (final ConfigurationException e) {
					logger.error("", e);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JOptionPane.showMessageDialog(MainPanel.this, e.getMessage());
						}
					});
				}

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						getBusyComp().setBusy(false);
						JOptionPane.showMessageDialog(MainPanel.this, "Project generated", "Sucess",
								JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
		}).start();
	}

	public JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new MigLayout("fill "));
			panel.add(getMandatoryPanel(), "grow");
			panel.add(getAvailablePanels(), "grow,spany 2,wrap, w 350 ");
			panel.add(getSelectedPanels(), "grow,wrap,push");
			panel.add(getButtonGenerateProject(), "spanx,right");
		}
		return panel;
	}

	public JBusyComponent<JComponent> getBusyComp() {
		if (busyComp == null) {
			busyComp = new JBusyComponent<JComponent>(getPanel());
		}
		return busyComp;
	}
}
