package com.sicpa.standard.gui.screen.machine.component.confirmation;

import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;

public abstract class AbstractConfirmationPanel extends JPanel {

	private ConfirmationModel model;

	public AbstractConfirmationPanel() {
		this(new ConfirmationModel());
	}

	public AbstractConfirmationPanel(final ConfirmationModel model) {

		if (model == null) {
			setModel(new ConfirmationModel());
		} else {
			setModel(model);
		}
	}

	protected abstract void modelQuestionTextChanged(PropertyChangeEvent evt);

	protected abstract void modelConfirmButtonTextChanged(PropertyChangeEvent evt);

	protected abstract void modelCancelButtonTextChanged(PropertyChangeEvent evt);

	protected abstract void modelCancelButtonVisibilityChanged(PropertyChangeEvent evt);

	protected abstract void modelAskQuestionChanged(PropertyChangeEvent evt);

	protected abstract void modelAbort();

	protected abstract void modelConfirmButtonenabilityChanged(PropertyChangeEvent evt);

	public void setModel(final ConfirmationModel model) {
		if (this.model == model) {
			return;
		}
		this.model = model;
		model.addConfirmationChangeListener(new ConfirmationChangeListener() {

			@Override
			public void questionTextChanged(final PropertyChangeEvent evt) {
				modelQuestionTextChanged(evt);
			}

			@Override
			public void confirmButtonTextChanged(final PropertyChangeEvent evt) {
				modelConfirmButtonTextChanged(evt);
			}

			@Override
			public void cancelButtonVisibilityChanged(final PropertyChangeEvent evt) {
				modelCancelButtonVisibilityChanged(evt);
			}

			@Override
			public void cancelButtonTextChanged(final PropertyChangeEvent evt) {
				modelCancelButtonTextChanged(evt);
			}

			@Override
			public void askQuestion(final PropertyChangeEvent evt) {
				modelAskQuestionChanged(evt);
			}

			@Override
			public void confirmButtonEnabilityChanged(PropertyChangeEvent evt) {
				modelConfirmButtonenabilityChanged(evt);
			}

			@Override
			public void abort() {
				modelAbort();
			}
		});
	}

	public ConfirmationModel getModel() {
		return this.model;
	}
}
