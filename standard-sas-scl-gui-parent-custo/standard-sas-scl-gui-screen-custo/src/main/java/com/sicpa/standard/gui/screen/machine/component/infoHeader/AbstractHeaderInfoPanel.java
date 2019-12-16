package com.sicpa.standard.gui.screen.machine.component.infoHeader;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import com.sicpa.standard.gui.model.BasicMapLikeModel;

public abstract class AbstractHeaderInfoPanel extends JComponent {

	BasicMapLikeModel model;

	public AbstractHeaderInfoPanel() {
		this(null);
	}

	public AbstractHeaderInfoPanel(final BasicMapLikeModel model) {
		if (model == null) {
			setModel(new BasicMapLikeModel());
		} else {
			setModel(model);
		}
	}

	public BasicMapLikeModel getModel() {
		return this.model;
	}

	public void setModel(final BasicMapLikeModel model) {
		if (this.model == model) {
			return;
		}
		model.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				modelPropertyChanged(evt);
			}
		});
		this.model = model;
	}

	protected abstract void modelPropertyChanged(PropertyChangeEvent evt);
}
