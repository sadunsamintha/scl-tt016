package com.sicpa.standard.sasscl.view.selection.select.barcode;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton;
import com.sicpa.standard.gui.components.buttons.toggleButtons.ToggleImageAndTextButton;
import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.components.scroll.SmoothScrolling;
import com.sicpa.standard.gui.plaf.SicpaColor;
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
import java.util.Optional;

@SuppressWarnings("serial")
public class BarcodeInputView extends DefaultIdInputView {

	protected MainFrame mainFrame;
	protected DirectionButton backButton;
	protected JButton buttonMaintenance;
	protected JPanel panelSelect;
	protected JLabel labelConnected;
	protected JPanel panelCenter;

	protected ProductionParameterRootNode rootNode;

	protected ISelectProductionParametersViewListener callback;

	private static final Logger logger = LoggerFactory.getLogger(BarcodeInputView.class);

	public BarcodeInputView() {
		setModel(new BarcodeScreenModel());
		getModel().setDescription(Messages.get("view.main.barcodepanel.label"));
		setOpaque(true);
		initGUInew();
	}

	protected JXLayer<JScrollPane> scrollSelectPanel;
	protected JLabel labelCorespondingSKU;

	public void reset(ProductionParameterRootNode rootNode) {
		this.rootNode = rootNode;
		getModel().reset();
		getLabelCorespondingSKU().setVisible(false);
		getPanelSelect().removeAll();
	}

	@Override
	public BarcodeScreenModel getModel() {
		return (BarcodeScreenModel) super.getModel();
	}

	public void setCallback(ISelectProductionParametersViewListener callback) {
		this.callback = callback;
	}

	protected void initGUI() {

	}

	protected void initGUInew() {
		removeAll();

		setLayout(new MigLayout("fill", "", ""));
		add(getPanelBarcode(), "h 100!,pushx, grow,wrap");

		add(getTextId(), "growx ,w 50%,h 50 ");
		add(getPaddedOk(), "h 75!,w 100");
		add(new PaddedButton(getButtonMaintenance()), "h 75!,wrap");

		add(getPanelCenter(), "grow,push,span");

		add(getLabelConnected(), "north");
		hideCenterPanel();

	}

	public JPanel getPanelCenter() {
		if (panelCenter == null) {
			panelCenter = new JPanel();
			panelCenter.setLayout(new MigLayout("fill,hidemode 3"));
			panelCenter.add(getTextError(), "grow,span");

			panelCenter.add(getLabelCorespondingSKU(), "");
			panelCenter.add(getScrollSelectPanel(), "grow,push,span");
		}
		return panelCenter;
	}

	protected void displaySkuSelectionInCenter() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getTextError().setVisible(false);
				getLabelCorespondingSKU().setVisible(true);
				getScrollSelectPanel().setVisible(true);
			}
		});
	}

	protected void displayTextErrorInCenter() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getTextError().setVisible(true);
				getLabelCorespondingSKU().setVisible(false);
				getScrollSelectPanel().setVisible(false);
			}
		});
	}

	protected void hideCenterPanel() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getTextError().setVisible(false);
				getLabelCorespondingSKU().setVisible(false);
				getScrollSelectPanel().setVisible(false);
			}
		});

	}

	public JLabel getLabelCorespondingSKU() {

		if (this.labelCorespondingSKU == null) {
			this.labelCorespondingSKU = new JLabel("Corresponding SKU:");
			this.labelCorespondingSKU.setVisible(false);
		}
		return this.labelCorespondingSKU;
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
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getButtonOk().setEnabled(false);
				getPanelSelect().removeAll();
				getLabelCorespondingSKU().setVisible(false);
				getTextError().setText("");
				if (getMainFrame() != null) {
					getMainFrame().getController().executeTaskInBackground(new Runnable() {
						@Override
						public void run() {
							final String barcode = evt.getId().trim();
							final List<Entry> possibleParams = retreiveSkuFromBarcode(barcode);

							if (possibleParams == null || possibleParams.isEmpty()) {
								displayTextErrorInCenter();
								getModel().setError(Messages.get("view.main.barcodepanel.error") + ":\n" + barcode);
							} else if (possibleParams.size() == 1) {
								hideCenterPanel();
								SKU selectedSKU = possibleParams.get(0).sku;
								ProductionMode selectectMode = possibleParams.get(0).mode;
								selectProductionParameters(selectedSKU, selectectMode, barcode);

							} else {
								ThreadUtils.invokeLater(new Runnable() {
									@Override
									public void run() {
										displaySkuSelectionInCenter();
										populateSelectPanel(possibleParams, barcode);
									}
								});
							}
							ThreadUtils.invokeLater(new Runnable() {
								@Override
								public void run() {
									getButtonOk().setEnabled(true);
								}
							});
						}
					});
				}
			}
		});
	}

	protected void selectProductionParameters(final SKU sku, final ProductionMode mode, final String barcode) {

		getMainFrame().resetMainPanel();
		getPanelSelect().setVisible(false);
		getLabelCorespondingSKU().setVisible(false);

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

	protected List<Entry> retreiveSkuFromBarcode(final String barcode) {
		ArrayList<Entry> skus = new ArrayList<Entry>();
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
				entries.add(entry);
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

	public JButton getButtonMaintenance() {
		if (this.buttonMaintenance == null) {
			this.buttonMaintenance = new JButton("Maintenance");
			this.buttonMaintenance.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonMaintenanceActionPerformed();
				}
			});
		}
		return this.buttonMaintenance;
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
		getLabelCorespondingSKU().setVisible(true);

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

	public void modelBarcodeConnectionStatusChanged(final boolean connected) {
		if (connected) {
			getLabelConnected().setText(" ");
		} else {
			getLabelConnected().setText(Messages.get("barcode.status.notConnected"));
		}
	}

	@Override
	public void setModel(final IdInputmodel model) {
		if (this.model != model) {
			model.addIdListener(new BarcodeViewAdapter() {
				@Override
				public void barcodeConnectionStatusChanged(final BarcodeViewEvent evt) {
					modelBarcodeConnectionStatusChanged(evt.isConnected());
				}
			});
			super.setModel(model);
		}
	}

	public JLabel getLabelConnected() {
		if (this.labelConnected == null) {
			this.labelConnected = new JLabel();
			this.labelConnected.setForeground(SicpaColor.RED);
			modelBarcodeConnectionStatusChanged(getModel().isBarcodeConnected());
		}
		return this.labelConnected;
	}
}
