package com.sicpa.standard.gui.screen.machine.component.emergency;

import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;

@Deprecated
public abstract class AbstractEmergencyPanel extends JPanel {

	private EmergencyModel model;

	public AbstractEmergencyPanel() {
		this(new EmergencyModel());
	}

	public AbstractEmergencyPanel(final EmergencyModel model) {
		if (model == null) {
			setModel(new EmergencyModel());
		} else {
			setModel(model);
		}
	}

	public void setModel(final EmergencyModel model) {
		if (this.model == model) {
			return;
		}

		this.model = model;

		model.addEmergencyChangeListener(new EmergencyChangeListener() {

			@Override
			public void emergencyVisibilityChanged(final PropertyChangeEvent evt) {
				modelEmergencyVisibilityChanged(evt);
			}

			@Override
			public void emergencyTextChanged(final PropertyChangeEvent evt) {
				modelEmergencyTextChanged(evt);
			}
		});
	}

	protected abstract void modelEmergencyTextChanged(PropertyChangeEvent evt);

	protected abstract void modelEmergencyVisibilityChanged(PropertyChangeEvent evt);

	public EmergencyModel getModel() {
		return this.model;
	}

}
