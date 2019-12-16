package com.sicpa.standard.gui.screen.machine.impl.SPL.stats;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

public abstract class AbstractStatisticsPanel extends JPanel {

	private StatisticsModel model;

	public AbstractStatisticsPanel() {
		this(new StatisticsModel());
	}

	public AbstractStatisticsPanel(final StatisticsModel model) {
		if (model == null) {
			setModel(new StatisticsModel());
		} else {
			setModel(model);
		}
	}

	protected abstract void modelPropertyChanged(PropertyChangeEvent evt);

	public StatisticsModel getModel() {
		return this.model;
	}

	public void setModel(final StatisticsModel model) {
		if (this.model == model) {
			return;
		}
		this.model = model;
		model.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				modelPropertyChanged(evt);
			}
		});
	}
}
