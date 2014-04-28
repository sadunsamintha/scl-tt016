package com.sicpa.standard.sasscl.wizard.view.actionPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.buttons.toggleButtons.ToggleImageAndTextButton;
import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.configaction.SetApplicationType;
import com.sicpa.standard.sasscl.wizard.context.ProjectContext;
import com.sicpa.standard.sasscl.wizard.view.IActionProvider;

public class ApplicationTypePanel extends JPanel implements IActionProvider {

	private static final long serialVersionUID = 1L;

	public ApplicationTypePanel() {
		initGUI();
	}

	private ApplicationType type;

	private void initGUI() {
		setLayout(new MigLayout("fill"));
		JLabel title = new JLabel("Select the type of the application you wish to create");
		add(title, "north");
		ButtonGroup grp = new ButtonGroup();
		for (final ApplicationType at : ApplicationType.values()) {
			AbstractButton button = new ToggleImageAndTextButton(at.name());
			grp.add(button);
			add(button);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					type = at;
					ProjectContext.setApplicationType(at);
				}
			});
		}
		// select the first eleement
		grp.getElements().nextElement().setSelected(true);
		type = ApplicationType.valueOf(grp.getElements().nextElement().getText());
	}

	@Override
	public IConfigAction provide() {
		return new SetApplicationType(type);
	}
	
	@Override
	public boolean isAvailable(ApplicationType type) {
		return true;
	}
}
