package com.sicpa.standard.sasscl.view.selection.select.barcode;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton;
import com.sicpa.standard.gui.components.buttons.toggleButtons.ToggleImageAndTextButton;
import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.components.scroll.SmoothScrolling;
import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;
import com.sicpa.standard.gui.screen.machine.component.IdInput.DefaultIdInputView;
import com.sicpa.standard.gui.screen.machine.component.IdInput.IdInputEvent;
import com.sicpa.standard.gui.screen.machine.component.IdInput.IdInputmodel;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import com.sicpa.standard.sasscl.productionParameterSelection.selectionmodel.DefaultSelectionModel;
import com.sicpa.standard.sasscl.view.MainFrame;
import com.sicpa.standard.sasscl.view.selection.select.ISelectProductionParametersViewListener;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.jxlayer.JXLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class BarcodeInputView extends DefaultIdInputView {

	protected MainFrame mainFrame;
	protected JButton buttonMaintenance;
	protected JPanel panelSelect;
	protected JPanel panelCenter;
	protected DefaultSelectionModel dataSelectionModel;
	protected ProductionParameterRootNode rootNode;
	protected ISelectProductionParametersViewListener callback;
	protected JXLayer<JScrollPane> scrollSelectPanel;
	protected JLabel labelCorrespondingSKU;
	protected JLabel invalidBarcode;

	private JButton buttonBack;

	private static final Logger logger = LoggerFactory.getLogger(BarcodeInputView.class);

	public BarcodeInputView(ISelectProductionParametersViewListener callback) {
		this.callback = callback;

		getModel().setDescription(Messages.get("view.main.barcodepanel.label"));
		setOpaque(true);
		initGUINew();
	}

	public void reset(ProductionParameterRootNode rootNode) {
		initGUINew();
		this.rootNode = rootNode;
		this.dataSelectionModel = new DefaultSelectionModel(rootNode);
		getModel().reset();
		getLabelCorrespondingSKU().setVisible(false);
		getPanelSelect().removeAll();
	}
	
	protected void initGUINew() {
		ThreadUtils.invokeLater(() -> {
			removeAll();

			setLayout(new MigLayout("fill, inset 0 0 0 0"));
			add(getPanelBarcode(), "span, split 2, pushy, growx, growy");

			add(getTextId(), "growx ,w 50%,h 50 ");
			JPanel buttonPanel = new JPanel();
			add(buttonPanel);
			buttonPanel.add(getPaddedOk(), "h 75!, w 100");
			buttonPanel.add(new PaddedButton(getButtonMaintenance()), "h 75!, w 100");
			buttonPanel.add(getInvalidBarcode());
			getInvalidBarcode().setVisible(false);
			hideCenterPanel();
			getButtonOk().setVisible(true);
			getTextId().setText("");
			getTextId().requestFocusInWindow();

			revalidate();
			repaint();
		});
	}

	protected void initGUISkuSelection() {
		ThreadUtils.invokeLater(() -> {
			removeAll();

			setLayout(new MigLayout("fill, inset 0 0 0 0"));
			add(getPanelCenter(), "grow,push,spanx, spany");

			revalidate();
			repaint();
		});
	}

	public JPanel getPanelCenter() {
		if (panelCenter == null) {
			panelCenter = new JPanel();
			panelCenter.setLayout(new MigLayout("fill,hidemode 3"));
			panelCenter.add(getTextError(), "grow,span");

			panelCenter.add(getLabelCorrespondingSKU(), "");
			panelCenter.add(getScrollSelectPanel(), "grow,push,span");

            panelCenter.add(getButtonBack(),"w 100! , h 40!");
		}
		return panelCenter;
	}

	protected void displaySkuSelectionInCenter() {
		ThreadUtils.invokeLater(() -> {
            getTextError().setVisible(false);
            getLabelCorrespondingSKU().setVisible(true);
            getScrollSelectPanel().setVisible(true);
        });
	}

	protected void displayTextErrorInCenter() {
		ThreadUtils.invokeLater(() -> {
            getTextError().setVisible(true);
            getLabelCorrespondingSKU().setVisible(false);
            getScrollSelectPanel().setVisible(false);
        });
	}

	protected void hideCenterPanel() {
		ThreadUtils.invokeLater(() -> {
            getTextError().setVisible(false);
            getLabelCorrespondingSKU().setVisible(false);
            getScrollSelectPanel().setVisible(false);
        });

	}

	public JLabel getLabelCorrespondingSKU() {
		if (this.labelCorrespondingSKU == null) {
			this.labelCorrespondingSKU = new JLabel("Corresponding SKU:");
			this.labelCorrespondingSKU.setVisible(false);
		}
		return this.labelCorrespondingSKU;
	}

	public JLabel getInvalidBarcode() {
		if (this.invalidBarcode == null) {
			this.invalidBarcode = new JLabel("Invalid Barcode!");
			this.invalidBarcode.setForeground(Color.RED);
		}
		return this.invalidBarcode;
	}

	public JXLayer<JScrollPane> getScrollSelectPanel() {
		if (this.scrollSelectPanel == null) {
			JScrollPane scroll = new JScrollPane(getPanelSelect());
			scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
			SmoothScrolling.enableFullScrolling(scroll);
			this.scrollSelectPanel = SmallScrollBar.createLayerSmallScrollBar(scroll);
		}
		return this.scrollSelectPanel;
	}

	@Override
	protected void modelIdInputComplete(final IdInputEvent evt) {
		super.modelIdInputComplete(evt);
		barcodeEntered(evt);
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
						selectProductionParameters(selectedSKU, selectedMode, barcode);
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

	protected void selectProductionParameters(final SKU sku, final ProductionMode mode, final String barcode) {
		getMainFrame().resetMainPanel();
		getPanelSelect().setVisible(false);
		getLabelCorrespondingSKU().setVisible(false);

		callback.productionParametersSelected(new ProductionParameters(mode, sku, barcode));
	}

	protected MainFrame getMainFrame() {
		if (this.mainFrame == null) {
			Window window = SwingUtilities.getWindowAncestor(this);
			if (window instanceof AbstractMachineFrame) {
				this.mainFrame = (MainFrame) window;
			}
		}
		return this.mainFrame;
	}

	protected static class Entry {
		SKU sku;
		ProductionMode mode;
	}

	protected List<Entry> retrieveSkuFromBarcode(final String barcode) {
		ArrayList<Entry> skus = new ArrayList<>();
		populateMatchingSKU(barcode, rootNode, null, skus);
		return skus;
	}

	protected void populateMatchingSKU(final String barcode, final IProductionParametersNode node,
			final IProductionParametersNode parent, final List<Entry> entries) {
		if (node instanceof SKUNode) {
			SKU sku = (SKU) node.getValue();
			logger.debug("sku=" + sku.getDescription() + ",barcodes=" + sku.getBarCodes());

			if (sku.containsBarcode(barcode)) {
				Entry entry = new Entry();
				entry.sku = sku;

				if (parent instanceof ProductionModeNode) {
					entry.mode = (ProductionMode) parent.getValue();
				}

				permissionFilterAndAdd(entries,entry);
			}
		} else {
			List<? extends IProductionParametersNode> children = node.getChildren();
			if (children != null) {
				for (IProductionParametersNode child : children) {
					populateMatchingSKU(barcode, child, node, entries);
				}
			}
		}
	}

	private void permissionFilterAndAdd(List<Entry> entries, Entry entry) {
		Permission p = dataSelectionModel.getPermissions().get(entry.mode);
		if (p != null && SecurityService.hasPermission(p)) {
			entries.add(entry);
		}
	}

	public JButton getButtonMaintenance() {
		if (this.buttonMaintenance == null) {
			this.buttonMaintenance = new JButton("Maintenance");
			this.buttonMaintenance.addActionListener(e -> buttonMaintenanceActionPerformed());
		}
		return this.buttonMaintenance;
	}

	public void setMaintenanceButtonVisibility(boolean visible) {
		getButtonMaintenance().setVisible(visible);
	}

	protected void buttonMaintenanceActionPerformed() {
		getPanelSelect().setVisible(false);

		ProductionMode mode = ProductionMode.MAINTENANCE;
		callback.productionParametersSelected(new ProductionParameters(mode, null, ""));

	}

	public JPanel getPanelSelect() {
		if (this.panelSelect == null) {
			this.panelSelect = new JPanel(new MigLayout("fill,wrap 1"));
		}
		return this.panelSelect;
	}

	protected void populateSelectPanel(final List<Entry> entries, final String barcode) {
		this.panelSelect = null;
		this.scrollSelectPanel.getView().setViewportView(getPanelSelect());
		getLabelCorrespondingSKU().setVisible(true);

		for (Entry entry : entries) {
			ToggleImageAndTextButton button = new ToggleImageAndTextButton(entry.mode + "\n" + entry.sku);
			button.addActionListener(new ButtonEntryActionListener(entry, barcode));
			getPanelSelect().add(button, "center");
		}
	}

	protected class ButtonEntryActionListener implements ActionListener {
		Entry entry;
		String barcode;

		public ButtonEntryActionListener(final Entry entry, final String barcode) {
			this.entry = entry;
			this.barcode = barcode;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			selectProductionParameters(this.entry.sku, this.entry.mode, this.barcode);
			getPanelSelect().removeAll();
		}
	}

	@Override
	public void setModel(final IdInputmodel model) {
		if (this.model != model) {
			super.setModel(model);
		}
	}

	public JButton getButtonBack() {
		if (this.buttonBack == null) {
			this.buttonBack = new DirectionButton(DirectionButton.Direction.LEFT);
			this.buttonBack.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonBackActionPerformed();
				}
			});

			this.buttonBack.setName("buttonBack");
		}
		return this.buttonBack;
	}

	private void buttonBackActionPerformed() {
		reset(this.rootNode);
	}
}