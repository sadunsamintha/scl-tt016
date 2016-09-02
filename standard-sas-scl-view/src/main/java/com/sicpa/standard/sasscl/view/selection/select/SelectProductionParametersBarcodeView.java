package com.sicpa.standard.sasscl.view.selection.select;

import javax.swing.JPanel;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.selection.select.barcode.BarcodeScreenModel;
import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.controller.view.event.BarcodeReadEvent;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.view.selection.select.barcode.BarcodeInputView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class SelectProductionParametersBarcodeView extends JPanel implements ISelectProductionParametersView {

	protected ISelectProductionParametersViewListener callback;
	protected BarcodeInputView delegate;

	private static final Logger logger = LoggerFactory.getLogger(SelectProductionParametersBarcodeView.class);

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

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		logger.info("refreshing_view,reason=language switch,lang=" + evt.getLanguage());
		removeAll();
		this.delegate = null;
		initGUI();
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
