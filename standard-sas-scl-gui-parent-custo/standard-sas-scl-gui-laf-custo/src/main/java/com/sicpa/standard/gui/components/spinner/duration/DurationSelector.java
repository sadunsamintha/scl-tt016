package com.sicpa.standard.gui.components.spinner.duration;

import javax.swing.JPanel;

public abstract class DurationSelector extends JPanel {

	private static final long serialVersionUID = 1L;
	protected DurationSelectorModel model;

	public DurationSelector() {
		this(new DurationSelectorModel());
	}

	public DurationSelector(DurationSelectorModel model) {
		setModel(model);
	}

	public void setModel(DurationSelectorModel model) {
		if (this.model != model) {
			model.addDurationChangedListener(new IDurationChangedListener() {
				@Override
				public void durationChanged(DurationChangedEvent evt) {
					modelDurationChanged(evt);
				}
			});
		}
		this.model = model;
	}

	protected abstract void modelDurationChanged(DurationChangedEvent evt);

	public abstract void addDurationUnit(DurationUnit unit);

	public DurationSelectorModel getModel() {
		return model;
	}
}
