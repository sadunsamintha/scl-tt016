package com.sicpa.standard.sasscl.view.config.plc;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroup;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

@SuppressWarnings("serial")
public class PlcVariablePanel extends JPanel {

	protected List<PlcVariableGroup> groups;

	public PlcVariablePanel(List<PlcVariableGroup> groups) {
		EventBusService.register(this);
		this.groups = groups;
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout());
		for (PlcVariableGroup grp : groups) {
			add(new PanelGroup(grp), "growx,pushx,wrap");
		}
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		removeAll();
		initGUI();
		revalidate();
	}

	public List<PlcVariableGroup> getGroups() {
		return groups;
	}

	public static class PanelGroup extends JPanel {

		private static final long serialVersionUID = 1L;
		protected JButton button;
		protected JPanel panelVar;
		protected PlcVariableGroup group;

		public PanelGroup(PlcVariableGroup grp) {
			this.group = grp;
			initGUI();
		}

		private void initGUI() {
			setLayout(new MigLayout("hidemode 3"));
			add(getButton(), "pushx,spanx , split 3");
			add(new JLabel(Messages.get(group.getDescription())), "");
			add(new JSeparator(), "growx ");
			add(getPanelVar(), "grow,push");
		}

		public JButton getButton() {
			if (button == null) {
				button = new JButton("+");
				button.addActionListener(e -> {
                    getPanelVar().setVisible(!getPanelVar().isVisible());
                    String text = getPanelVar().isVisible() ? "-" : "+";
                    button.setText(text);
                });
			}
			return button;
		}

		public JPanel getPanelVar() {
			if (panelVar == null) {
				panelVar = new JPanel(new MigLayout());
				for (PlcVariableDescriptor var : group.getPlcVars()) {
					JComponent r = var.getRenderer();
					if (r != null) {
						panelVar.add(r, "grow,wrap");
					}
				}
				panelVar.setVisible(false);
			}
			return panelVar;
		}
	}

}
