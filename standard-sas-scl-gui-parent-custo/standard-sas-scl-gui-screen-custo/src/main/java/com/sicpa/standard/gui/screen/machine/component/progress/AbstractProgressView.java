package com.sicpa.standard.gui.screen.machine.component.progress;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

public abstract class AbstractProgressView extends JPanel {

	ProgressModel model;

	public AbstractProgressView() {
		this(null);
	}

	public AbstractProgressView(final ProgressModel model) {
		if (model == null) {
			setModel(new ProgressModel());
		} else {
			setModel(model);
		}
	}

	public void setModel(final ProgressModel model) {
		if (this.model == model) {
			return;
		}
		this.model = model;
		model.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				modelProgressChanged((Boolean) evt.getNewValue());
			}
		});
	}

	public ProgressModel getModel() {
		return this.model;
	}

	protected abstract void modelProgressChanged(boolean isRunning);
}
