package com.sicpa.standard.gui.screen.machine.component.applicationStatus;

import java.beans.PropertyChangeEvent;

import javax.swing.JComponent;

public abstract class AbstractApplicationStatusPanel extends JComponent {
	private static final long serialVersionUID = 1L;
	private ApplicationStatusModel model;

	public AbstractApplicationStatusPanel() {
		this(new ApplicationStatusModel());
	}

	public AbstractApplicationStatusPanel(final ApplicationStatusModel model) {
		if (model == null) {
			setModel(new ApplicationStatusModel());
		} else {
			setModel(model);
		}
	}

	protected abstract void modelApplicationStatusChanged(PropertyChangeEvent evt);

	public ApplicationStatusModel getModel() {
		return this.model;
	}

	public void setModel(final ApplicationStatusModel model) {
		if (this.model == model) {
			return;
		}

		this.model = model;
		model.addApplicationStatusListener(new ApplicationStatusListener() {

			@Override
			public void applicationStatusChanged(final PropertyChangeEvent evt) {
				modelApplicationStatusChanged(evt);
			}
		});
	}
}
