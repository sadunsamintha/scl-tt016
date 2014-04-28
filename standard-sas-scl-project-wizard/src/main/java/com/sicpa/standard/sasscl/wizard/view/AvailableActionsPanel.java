package com.sicpa.standard.sasscl.wizard.view;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.jidesoft.swing.MultilineLabel;
import com.sicpa.standard.gui.components.dnd.DnDGhostManager;
import com.sicpa.standard.gui.components.dnd.DnDStartStopCallback;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.wizard.context.IContextChangeListener;
import com.sicpa.standard.sasscl.wizard.context.ProjectContext;
import com.sicpa.standard.sasscl.wizard.view.SelectedComponentsPanel.FeedbackInfoDisplay;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.BarcodeScanningSystemPanel;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.CameraCognexPanel;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.CameraDrsPanel;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.CustomMessageMappingPanel;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.OfflineCountingPanel;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.PlcBeckHoffPanel;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.PlcJBeckPanel;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.PostPackagePanel;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.ProductStatusMappingPanel;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.ResetStatisticsOnStartPanel;
import com.sicpa.standard.sasscl.wizard.view.dnd.TransferableAction;

public class AvailableActionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JPanel panel;

	private SelectedComponentsPanel selectedComponentPanel;

	public AvailableActionsPanel() {
		initGUI();
		ProjectContext.addListener(new IContextChangeListener() {
			@Override
			public void contextChanged(PropertyChangeEvent evt) {
				ThreadUtils.invokeLater(new Runnable() {
					@Override
					public void run() {
						fillAvailableComponents();
					}
				});
			}
		});
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,inset 0 0 0 0"));
		setBorder(BorderFactory.createTitledBorder("Available Components:"));
		JScrollPane scroll = new JScrollPane(getPanel());
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(scroll, "grow");
	}

	private void fillAvailableComponents() {

		panel.removeAll();

		MultilineLabel label = new MultilineLabel(
				"to add a component to your project, drag it to the selected components panel");
		label.setFont(label.getFont().deriveFont(12f));
		panel.add(label, ",grow,wrap");

		// business
		addComponent(new OfflineCountingPanel(), "spanx, split 2");
		addComponent(new ResetStatisticsOnStartPanel(), "");
		addComponent(new PostPackagePanel(), "wrap");

		// device
		addComponent(new PlcBeckHoffPanel(), "spanx, split 2");
		addComponent(new PlcJBeckPanel(), "");

		addComponent(new CameraCognexPanel(), "spanx, split 2");
		addComponent(new CameraDrsPanel(), "");
		
		addComponent(new BarcodeScanningSystemPanel(),"spanx");

		// mapping
		addComponent(new CustomMessageMappingPanel(), "spanx, split 2");
		addComponent(new ProductStatusMappingPanel(), "");

		panel.add(new JLabel(" "), "push");
	}

	public JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new MigLayout("fill"));

			fillAvailableComponents();

		}
		return panel;
	}

	public void addComponent(JComponent comp, String constraints) {

		if (comp instanceof IActionProvider) {
			if (!((IActionProvider) comp).isAvailable(ProjectContext.getApplicationType())) {
				return;
			}
		}

		DnDGhostManager.enableDrag(comp, new TransferableAction(comp.getClass()), new DnDStartStopCallback() {

			@Override
			public void dragStop(Transferable transferable, Point location) {
				selectedComponentPanel.setInfo(FeedbackInfoDisplay.none);
			}

			@Override
			public void dragStart(Transferable transferable, Point location) {
				selectedComponentPanel.setInfo(FeedbackInfoDisplay.dropin);
			}
		}, null);
		getPanel().add(comp, constraints);
	}

	public void setSelectedComponentPanel(SelectedComponentsPanel selectedComponentPanel) {
		this.selectedComponentPanel = selectedComponentPanel;
	}
}
