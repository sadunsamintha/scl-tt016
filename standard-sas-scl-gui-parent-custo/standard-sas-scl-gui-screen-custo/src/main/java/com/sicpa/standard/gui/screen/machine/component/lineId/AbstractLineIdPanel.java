package com.sicpa.standard.gui.screen.machine.component.lineId;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

public abstract class AbstractLineIdPanel extends JComponent {

	private LineIdModel model;

	public AbstractLineIdPanel(final LineIdModel model) {
		if (model == null) {
			setModel(new LineIdModel());
		} else {
			setModel(model);
		}
	}

	public AbstractLineIdPanel() {
		this(null);
	}

	public LineIdModel getModel() {
		return this.model;
	}

	public void setModel(final LineIdModel model) {
		if (this.model != model) {
			model.addLineIdChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					modelPropertyChanged(evt);
				}
			});
		}
		this.model = model;
	}

	protected abstract void modelPropertyChanged(PropertyChangeEvent evt);
}
