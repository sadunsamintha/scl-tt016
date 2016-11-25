package com.sicpa.standard.sasscl.view.selection.select;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.security.ISecurityController;
import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.barcode.BarcodeReaderEvent;
import com.sicpa.standard.sasscl.event.UserLoginEvent;
import com.sicpa.standard.sasscl.event.UserLogoutEvent;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.security.SasSclPermission;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.selection.select.barcode.BarcodeInputView;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

@SuppressWarnings("serial")
public class SelectProductionParametersBarcodeView extends JPanel implements ISelectProductionParametersView {

	protected ISelectProductionParametersViewListener callback;
	protected BarcodeInputView delegate;
	protected SkuListProvider skuListProvider;
	protected ISecurityController securityController;

	private static final Logger logger = LoggerFactory.getLogger(SelectProductionParametersBarcodeView.class);

	public void initGUI() {
		ThreadUtils.invokeLater(() -> {
			setLayout(new MigLayout("fill, inset 0 0 0 0"));
			add(getDelegate(), "growx,spanx,push");
		});
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

	@Subscribe
	public void handleUserLogin(UserLoginEvent evt) {
		getDelegate().setMaintenanceButtonVisibility(isMaintenanceModeAvailableForCurrentUser());
	}

	@Subscribe
	public void handleUserLogout(UserLogoutEvent evt) {
		getDelegate().setMaintenanceButtonVisibility(isMaintenanceModeAvailableForCurrentUser());
	}

	public BarcodeInputView getDelegate() {
		if (delegate == null) {
			delegate = new BarcodeInputView(callback);
			delegate.reset(skuListProvider.get());
		}
		return delegate;
	}

	@Subscribe
	public void barcodeRead(final BarcodeReaderEvent evt) {
		if (isShowing()) {
			getDelegate().getModel().setId(evt.getBarcode());
			getDelegate().getModel().selectionComplete();
		}
	}

	public void setCallback(ISelectProductionParametersViewListener callback) {
		this.callback = callback;
	}

	public void setSkuListProvider(SkuListProvider skuListProvider) {
		this.skuListProvider = skuListProvider;
	}

	public void setSecurityController(ISecurityController securityController) {
		this.securityController = securityController;
	}

	private boolean isMaintenanceModeAvailableForCurrentUser() {
		return SecurityService.hasPermission(SasSclPermission.PRODUCTION_MODE_MAINTENANCE);
	}
}
