package com.sicpa.standard.sasscl.view.selection.select;

import javax.swing.JPanel;

import com.sicpa.standard.sasscl.controller.productionconfig.validator.ProductionParametersValidator;
import com.sicpa.standard.sasscl.view.selection.select.productionparameters.ProductionParametersSelectionFlowView;
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
	protected ProductionParametersSelectionFlowView delegate;
	private ISelectionModelFactory selectionModelFactory;
	private ProductionParametersValidator productionParametersValidator;

	public SelectProductionParametersHandPickingView() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill, inset 0 0 0 0"));
		add(getDelegate(), "grow,push,spanx");
	}



	@Override
	public void displaySelectionScreen(ProductionParameterRootNode root) {

		AbstractSelectionFlowModel model = selectionModelFactory.create(root);
		if (model == null) {
			return;
		}
		getDelegate().setModel(model);
		getDelegate().setProductionParametersValidator(productionParametersValidator);
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

	protected void selectionCompleteCallBack(SelectionFlowEvent evt) {
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

	protected SKU getSkuMode(SelectionFlowEvent evt) {
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

	protected ProductionMode getProductionMode(SelectionFlowEvent evt) {
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

	public ProductionParametersSelectionFlowView getDelegate() {
		if (delegate == null) {
			delegate = new ProductionParametersSelectionFlowView();
		}
		return delegate;
	}

	public void setDelegate(ProductionParametersSelectionFlowView delegate) {
		this.delegate = delegate;
	}

	public void setSelectionModelFactory(ISelectionModelFactory selectionModelFactory) {
		this.selectionModelFactory = selectionModelFactory;
	}

	public void setCallback(ISelectProductionParametersViewListener callback) {
		this.callback = callback;
	}

	public void setProductionParametersValidator(ProductionParametersValidator productionParametersValidator) {
		this.productionParametersValidator = productionParametersValidator;
	}
}
