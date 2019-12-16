package com.sicpa.standard.gui.screen.machine.impl.SPL.systemInfo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

public abstract class AbstractSystemInfoPanel extends JPanel {

	private SystemInfoModel model;

	public AbstractSystemInfoPanel() {
		this(new SystemInfoModel());
	}

	public AbstractSystemInfoPanel(final SystemInfoModel model) {
		if (model == null) {
			setModel(new SystemInfoModel());
		} else {
			setModel(model);
		}
	}

	protected abstract void modelPropertyChange(PropertyChangeEvent evt);

	public SystemInfoModel getModel() {
		return this.model;
	}

	public void setModel(final SystemInfoModel model) {
		if (this.model == model) {
			return;
		}
		this.model = model;
		model.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				modelPropertyChange(evt);
			}
		});
	}
}
