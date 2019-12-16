package com.sicpa.standard.gui.screen.machine.component.IdInput;

import javax.swing.JComponent;

public abstract class AbstractIdInputView extends JComponent {

	protected IdInputmodel model;

	public AbstractIdInputView() {
		this(new IdInputmodel());
	}

	public AbstractIdInputView(final IdInputmodel model) {
		if (model == null) {
			setModel(new IdInputmodel());
		} else {
			setModel(model);
		}
	}

	public void setModel(final IdInputmodel model) {
		if (this.model != model) {
			model.addIdListener(new IdInputListener() {
				@Override
				public void complete(final IdInputEvent evt) {
					modelIdInputComplete(evt);
				}

				@Override
				public void descriptionChanged(final IdInputEvent evt) {
					modelDescriptionChanged(evt);
				}

				@Override
				public void idChanged(final IdInputEvent evt) {
					modelIdChanged(evt);
				}

				@Override
				public void error(final IdInputEvent evt) {
					modelError(evt);
				}
			});
		}
		this.model = model;
	}

	protected abstract void modelError(IdInputEvent evt);

	protected abstract void modelIdChanged(IdInputEvent evt);

	protected abstract void modelDescriptionChanged(IdInputEvent evt);

	protected abstract void modelIdInputComplete(IdInputEvent evt);

	public IdInputmodel getModel() {
		return this.model;
	}
}
