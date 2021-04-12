package com.sicpa.standard.sasscl.view.selection.select;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.SelectionFlowAdapter;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.SelectionFlowEvent;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.controller.productionconfig.validator.ProductionParametersValidator;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.ISelectionModelFactory;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.view.selection.change.SelectionChangeView;
import com.sicpa.standard.sasscl.view.selection.select.productionparameters.ProductionParametersSelectionFlowView;
import com.sicpa.standard.sasscl.view.startstop.StartStopView;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class SelectProductionParametersHandPickingView extends JPanel implements ISelectProductionParametersView {

	protected ISelectProductionParametersViewListener callback;
	protected ProductionParametersSelectionFlowView delegate;
	private ISelectionModelFactory selectionModelFactory;
	private ProductionParametersValidator productionParametersValidator;
	private final String START_STOP_VIEW_NAME = "startStopView";
	private final String SELECT_CHANGE_VIEW_NAME = "selectionChangeView";

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
		addCancelSelectionCallback(model);

		getDelegate().setBackButtonVisibleForFirstScreen(true);
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

	private void addCancelSelectionCallback(AbstractSelectionFlowModel model) {
		model.addSelectionFlowListener(new SelectionFlowAdapter() {
			@Override
			public void cancelSelection() {
				if (getDelegate().isShowing() && !((SelectProductionParametersViewController)callback).useBarcodeReader) {
					cancelSelectionCallBack();
				} else if(getDelegate().isShowing() && ((SelectProductionParametersViewController)callback).useBarcodeReader){
					displayBarcodeScreen();
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

	protected void cancelSelectionCallBack() {
		StartStopView ssv = (StartStopView) getComponentByBeanName(START_STOP_VIEW_NAME);
		ssv.getButtonStart().setEnabled(true);
		SelectionChangeView scv = (SelectionChangeView) getComponentByBeanName(SELECT_CHANGE_VIEW_NAME);
		scv.getButtonChangeContext().setEnabled(true);
		callback.selectionCanceled();
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

	private Component getComponentByBeanName(String beanName){
		Component component = (Component) BeanProvider.getBean(beanName);
		return component;
	}

	private void displayBarcodeScreen() {
		((SelectProductionParametersViewController)callback).displayView();
	}
}
