package com.sicpa.standard.gui.screen.machine.component.selectionSummary;

import javax.swing.JPanel;

public abstract class AbstractSummaryPanel extends JPanel {

	private SummaryModel model;

	public AbstractSummaryPanel(final SummaryModel model) {
		if (model == null) {
			setModel(new SummaryModel());
		} else {
			setModel(model);
		}
	}

	public SummaryModel getModel() {
		return this.model;
	}

	public void setModel(final SummaryModel model) {
		if (this.model != model) {
			this.model = model;
			model.addSummaryListener(new SummaryListener() {
				@Override
				public void SummaryChanged(final SummaryEvent event) {
					modelSummaryChanged(event);
				}
			});
		}
	}

	protected abstract void modelSummaryChanged(SummaryEvent event);

}
