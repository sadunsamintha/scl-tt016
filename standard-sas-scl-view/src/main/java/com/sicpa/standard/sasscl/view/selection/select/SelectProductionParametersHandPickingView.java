package com.sicpa.standard.sasscl.view.selection.select;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.DefaultSelectionFlowView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.SelectionFlowAdapter;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.SelectionFlowEvent;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.ISelectionModelFactory;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;

@SuppressWarnings("serial")
public class SelectProductionParametersHandPickingView extends JPanel implements ISelectProductionParametersView {

	protected ISelectProductionParametersViewListener callback;

	protected DefaultSelectionFlowView delegate;

	protected ISelectionModelFactory selectionModelFactory;

	public SelectProductionParametersHandPickingView() {
		initGUI();
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill, inset 0 0 0 0"));
		add(getDelegate(), "grow,push,spanx");
	}

	public void setCallback(ISelectProductionParametersViewListener callback) {
		this.callback = callback;
	}

	@Override
	public void displaySelectionScreen(ProductionParameterRootNode root) {

		AbstractSelectionFlowModel model = selectionModelFactory.create(root);
		if (model == null) {
			return;
		}
		getDelegate().setModel(model);

		model.addSelectionFlowListener(new SelectionFlowAdapter() {
			@Override
			public void selectionComplete(final SelectionFlowEvent evt) {
				if (getDelegate().isShowing()) {
					SelectProductionParametersHandPickingView.this.selectionComplete(evt);
				}
			}

			@Override
			public void cancelSelection() {
			}
		});
		getDelegate().setBackButtonVisibleForFirstScreen(false);
		getDelegate().init();
	}

	protected void selectionComplete(final SelectionFlowEvent evt) {
		ProductionMode mode = null;
		SKU sku = null;

		for (SelectableItem item : evt.getItems()) {
			if (item instanceof IProductionParametersNode) {
				IProductionParametersNode sc = (IProductionParametersNode) item;
				if (sc.getValue() instanceof ProductionMode) {
					mode = (ProductionMode) sc.getValue();
					OperatorLogger.log("Product Mode: {}", mode.getDescription());
				} else if (sc.getValue() instanceof SKU) {
					sku = (SKU) sc.getValue();
					OperatorLogger.log("Product Param: {}", sku.getDescription());
				}
			}
		}
		ProductionParameters pp = new ProductionParameters(mode, sku, "");
		callback.productionParametersSelected(pp);
	}

	public DefaultSelectionFlowView getDelegate() {
		if (delegate == null) {
			delegate = new DefaultSelectionFlowView();
			delegate.setUsePreviousPanel(false);
		}
		return delegate;
	}

	public void setSelectionModelFactory(ISelectionModelFactory selectionModelFactory) {
		this.selectionModelFactory = selectionModelFactory;
	}
}
