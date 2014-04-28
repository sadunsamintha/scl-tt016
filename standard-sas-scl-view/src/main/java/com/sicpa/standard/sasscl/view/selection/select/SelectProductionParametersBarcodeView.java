package com.sicpa.standard.sasscl.view.selection.select;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.controller.view.event.BarcodeReadEvent;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.view.selection.select.barcode.BarcodeInputView;

@SuppressWarnings("serial")
public class SelectProductionParametersBarcodeView extends JPanel implements ISelectProductionParametersView {

	protected ISelectProductionParametersViewListener callback;
	protected BarcodeInputView delegate;

	public SelectProductionParametersBarcodeView() {
		initGUI();
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill, inset 0 0 0 0"));
		add(getDelegate(), "growx,spanx,push");
	}

	public void setCallback(ISelectProductionParametersViewListener callback) {
		this.callback = callback;
		getDelegate().setCallback(callback);
	}

	@Override
	public void displaySelectionScreen(ProductionParameterRootNode root) {
		getDelegate().reset(root);

	}

	public BarcodeInputView getDelegate() {
		if (delegate == null) {
			delegate = new BarcodeInputView();
		}
		return delegate;
	}

	@Subscribe
	public void barcodeRead(final BarcodeReadEvent evt) {
		if (isShowing()) {
			getDelegate().getModel().setId(evt.getBarcode());
			getDelegate().getModel().selectionComplete();
		}
	}
}
