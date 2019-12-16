package com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow;

import javax.swing.JPanel;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectionFlowViewFactory;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.AbstractSelectionView;

public abstract class AbstractSelectionFlowView extends JPanel {

	private static final long serialVersionUID = 1L;

	private SelectionFlowViewFactory viewFactory;

	private AbstractSelectionFlowModel model;

	public AbstractSelectionFlowView(final AbstractSelectionFlowModel model, final SelectionFlowViewFactory viewFactory) {
		this.viewFactory = viewFactory;
		setModel(model);
		setName("selectionFlow");
	}

	public void setModel(final AbstractSelectionFlowModel model) {
		if (model != null && this.model != model) {
			model.addSelectionFlowListener(new SelectionFlowListener() {

				@Override
				public void selectionComplete(final SelectionFlowEvent evt) {
					modelSelectionComplete(evt);
				}

				@Override
				public void selectionChanged(final SelectionFlowEvent evt) {
					modelSelectionChanged(evt);
				}

				@Override
				public void populateNextComplete(final SelectionFlowEvent evt) {
					modelPopulateNextComplete(evt);
				}

				@Override
				public void cancelSelection() {
					modelCancelSelection();
				}

				@Override
				public void previousSelectionChanged(final SelectionFlowEvent evt) {
					modelOldSelectionChanged(evt);
				}

			});
			model.init();
		}
		this.model = model;
	}

	protected abstract void modelOldSelectionChanged(final SelectionFlowEvent evt);

	protected void modelSelectionComplete(final SelectionFlowEvent evt) {

	}

	protected abstract void modelSelectionChanged(SelectionFlowEvent evt);

	protected void modelCancelSelection() {
	};

	protected abstract void modelPopulateNextComplete(SelectionFlowEvent evt);

	public AbstractSelectionFlowModel getModel() {
		return this.model;
	}

	public void setViewFactory(final SelectionFlowViewFactory viewFactory) {
		this.viewFactory = viewFactory;
	}

	protected AbstractSelectionView createView(final SelectableItem[] items) {
		return this.viewFactory.createSelectionView(items);
	}

	public void init() {
		getModel().init();
	}

	public void resetSelections() {
		getModel().resetSelections();
	}
}
