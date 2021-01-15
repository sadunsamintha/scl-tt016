package com.sicpa.tt085.view.selection.select.barcode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.buttons.toggleButtons.ToggleImageAndTextButton;
import com.sicpa.standard.gui.screen.machine.component.IdInput.IdInputEvent;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import com.sicpa.standard.sasscl.view.selection.select.ISelectProductionParametersViewListener;
import com.sicpa.standard.sasscl.view.selection.select.barcode.BarcodeInputView;
import com.sicpa.tt085.model.TT085Country;
import com.sicpa.tt085.model.provider.CountryProvider;
import com.sicpa.tt085.productionParameterSelection.node.impl.CountryNode;

public class TT085BarcodeInputView extends BarcodeInputView implements CountryProvider {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(TT085BarcodeInputView.class);

	public TT085BarcodeInputView(ISelectProductionParametersViewListener callback) {
		super(callback);
	}
	
	protected void barcodeEntered(final IdInputEvent evt) {
		ThreadUtils.invokeLater(() -> {
			getPanelSelect().removeAll();
			getLabelCorrespondingSKU().setVisible(false);
			getTextError().setText("");
			if (getMainFrame() != null) {
				getMainFrame().getController().executeTaskInBackground(() -> {
					final String barcode = evt.getId().trim();
					final List<Entry> possibleParams = retrieveSkuFromBarcode(barcode);

					if (possibleParams == null || possibleParams.isEmpty()) {
						displayTextErrorInCenter();
						getModel().setError(Messages.get("view.main.barcodepanel.error") + ":\n" + barcode);
						getInvalidBarcode().setVisible(true);
						revalidate();
						repaint();
					} else if (possibleParams.size() == 1) {
						hideCenterPanel();
						SKU selectedSKU = possibleParams.get(0).sku;
						ProductionMode selectedMode = possibleParams.get(0).mode;
						TT085Country country = ((TT085Entry)possibleParams.get(0)).country;
						selectProductionParameters(selectedSKU, selectedMode, barcode, country);
					} else {
						ThreadUtils.invokeLater(() -> {
							displaySkuSelectionInCenter();
							populateSelectPanel(possibleParams, barcode);
							initGUISkuSelection();
						});
					}
				});
			}
		});
	}

	@Override
	protected void populateMatchingSKU(final String barcode, final IProductionParametersNode node,
			final IProductionParametersNode parent, final List<Entry> entries, ProductionMode productionMode) {
		if (node instanceof SKUNode) {
			SKU sku = (SKU) node.getValue();
			logger.debug("sku=" + sku.getDescription() + ",barcodes=" + sku.getBarCodes());
			if (sku.containsBarcode(barcode)) {
				List<TT085Country> countries = getCountriesForSku(node);
				if (countries.size() > 0) {
					TT085Entry entry = null;
					for (TT085Country country : countries) {
						entry = new TT085Entry();
						entry.sku = sku;
						entry.mode = productionMode;
						entry.country = country;
						permissionFilterAndAdd(entries,entry);
					}
				} else {
					TT085Entry entry = new TT085Entry();
					entry.sku = sku;
					entry.mode = productionMode;
					entry.country = null;
					permissionFilterAndAdd(entries,entry);
				}
			}
		} else {
			List<? extends IProductionParametersNode> children = node.getChildren();
			if (children != null) {
				ProductionMode mode = productionMode;
				for (IProductionParametersNode child : children) {
					if (child instanceof ProductionModeNode) {
						mode = (ProductionMode) child.getValue();
					}
					populateMatchingSKU(barcode, child, node, entries, mode);
				}
			}
		}
	}

	protected List<TT085Country> getCountriesForSku(final IProductionParametersNode node) {
		List<TT085Country> countryList = new ArrayList<TT085Country>();
		if (node == null)
			return null;
		for (IProductionParametersNode child : node.getChildren()) {
			if (child instanceof CountryNode) {
				TT085Country country = (TT085Country) child.getValue();
				countryList.add(country);
			}
		}
		return countryList;
	}
	
	protected void populateSelectPanel(final List<Entry> entries, final String barcode) {
		this.panelSelect = null;
		this.scrollSelectPanel.getView().setViewportView(getPanelSelect());
		getLabelCorrespondingSKU().setVisible(true);
		for (Entry entry : entries) {
			TT085Entry tt085Entry = (TT085Entry) entry;
			ToggleImageAndTextButton button = new ToggleImageAndTextButton(entry.mode + "\n" + entry.sku + "\n" + tt085Entry.country.getDisplayDescription());
			button.addActionListener(new TT085ButtonEntryActionListener(tt085Entry, barcode));
			getPanelSelect().add(button, "center");
		}
	}
	
	protected class TT085ButtonEntryActionListener implements ActionListener {
		TT085Entry entry;
		String barcode;

		public TT085ButtonEntryActionListener(final TT085Entry entry, final String barcode) {
			this.entry = entry;
			this.barcode = barcode;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			selectProductionParameters(this.entry.sku, this.entry.mode, this.barcode, this.entry.country);
			getPanelSelect().removeAll();
		}
	}

	protected void selectProductionParameters(final SKU sku, final ProductionMode mode, final String barcode, TT085Country cuntry) {
		getMainFrame().resetMainPanel();
		getPanelSelect().setVisible(false);
		getLabelCorrespondingSKU().setVisible(false);
		ProductionParameters pp = new ProductionParameters(mode, sku, barcode);
		CustoBuilder.addPropertyToClass(ProductionParameters.class, country);
		pp.setProperty(country, cuntry); 
		callback.productionParametersSelected(pp);
	}

	protected static class TT085Entry extends Entry {
		TT085Country country;
	}

}
