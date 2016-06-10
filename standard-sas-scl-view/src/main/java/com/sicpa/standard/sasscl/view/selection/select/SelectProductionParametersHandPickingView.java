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

	private ISelectProductionParametersViewListener callback;
	private DefaultSelectionFlowView delegate;
	private ISelectionModelFactory selectionModelFactory;

	public SelectProductionParametersHandPickingView() {
		initGUI();
	}

	private void initGUI() {
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
		addSelectionCompleteCallback(model);

		getDelegate().setBackButtonVisibleForFirstScreen(false);
		getDelegate().init();
	}

	private void addSelectionCompleteCallback(AbstractSelectionFlowModel model) {
		model.addSelectionFlowListener(new SelectionFlowAdapter() {
			@Override
			public void selectionComplete(SelectionFlowEvent evt) {
				if (getDelegate().isShowing()) {
					selectionCompleteCallBack(evt);
				}
			}
		});
	}

	private void selectionCompleteCallBack(SelectionFlowEvent evt) {
		ProductionMode mode = getProductionMode(evt);
		SKU sku = getSkuMode(evt);

		if (mode != null) {
			OperatorLogger.log("Product Mode: {}", mode.getDescription());
		}
		if (sku != null) {
			OperatorLogger.log("Product Param: {}", sku.getDescription());
		}

		ProductionParameters pp = new ProductionParameters(mode, sku, "");
		callback.productionParametersSelected(pp);
	}

	private SKU getSkuMode(SelectionFlowEvent evt) {
		for (SelectableItem item : evt.getItems()) {
			if (item instanceof IProductionParametersNode) {
				IProductionParametersNode sc = (IProductionParametersNode) item;
				if (sc.getValue() instanceof SKU) {
					return (SKU) sc.getValue();
				}
			}
		}
		return null;
	}

	private ProductionMode getProductionMode(SelectionFlowEvent evt) {
		for (SelectableItem item : evt.getItems()) {
			if (item instanceof IProductionParametersNode) {
				IProductionParametersNode sc = (IProductionParametersNode) item;
				if (sc.getValue() instanceof ProductionMode) {
					return (ProductionMode) sc.getValue();
				}
			}
		}
		return null;
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
