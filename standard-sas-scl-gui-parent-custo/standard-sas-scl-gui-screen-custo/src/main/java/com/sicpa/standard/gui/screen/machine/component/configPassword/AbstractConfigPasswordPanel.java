package com.sicpa.standard.gui.screen.machine.component.configPassword;

import javax.swing.JPanel;

@Deprecated
public abstract class AbstractConfigPasswordPanel extends JPanel {

	private ConfigPasswordModel model;

	public AbstractConfigPasswordPanel(final ConfigPasswordModel model) {
		if (model == null) {
			setModel(new ConfigPasswordModel());
		} else {
			setModel(model);
		}
	}

	public AbstractConfigPasswordPanel() {
		this(null);
	}

	public void setModel(final ConfigPasswordModel model) {
		if (this.model == model) {
			return;
		}
		this.model = model;
		model.addConfigPasswordListener(new ConfigPasswordListener() {

			@Override
			public void passwordChecked(final ConfigPasswordEvent evt) {
				modelPasswordChecked();
			}

			@Override
			public void canceled() {
			}
		});
	}

	protected abstract void modelPasswordChecked();

	public ConfigPasswordModel getModel() {
		return this.model;
	}
}
