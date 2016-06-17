package com.sicpa.standard.sasscl.view.selection.display;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.text.MultiLineLabel;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class SelectionDisplayView extends AbstractView<ISelectionDisplayViewListener, SelectionDisplayModel> {

	private JPanel mainPanel;
	private JLabel labelTitle;

	public SelectionDisplayView() {
		initGUI();
		handleLanguageSwitch(null);
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill"));
		add(getLabelTitle(), "spanx , split 2");
		add(new JSeparator(), "growx");
		add(getMainPanel(), "grow,push");
	}

	public JLabel getLabelTitle() {
		if (labelTitle == null) {
			labelTitle = new JLabel();
		}
		return labelTitle;
	}

	@Override
	public void modelChanged() {
		if (model != null && model.getProductionParameters() != null) {
			buildSelectionPanel(model.getProductionParameters());
		}
	}

	public JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new MigLayout());
		}
		return mainPanel;
	}

	protected void buildSelectionPanel(ProductionParameters pp) {
		getMainPanel().removeAll();

		SKU sku = pp.getSku();
		String barcode = pp.getBarcode();
		CodeType codeType = sku != null ? sku.getCodeType() : null;
		ProductionMode mode = pp.getProductionMode();

		if (mode != null) {
			getMainPanel().add(new MultiLineLabel(Messages.get(mode.getDescription())), "grow, w 200, h 45 , spanx");
		}

		if (codeType != null) {
			getMainPanel().add(new MultiLineLabel(codeType.getDescription()), "grow, w 200, h 45 , spanx");
		}
		if (sku != null) {
			if (sku.getImage() != null) {
				getMainPanel().add(toScaledImage(sku.getImage().getImage()), "grow,wrap");
			}
			getMainPanel().add(new MultiLineLabel(sku.getDescription()), "grow, w 200, h 135 , spanx");
		}

		if (barcode != null) {
			getMainPanel().add(new MultiLineLabel(barcode), "grow, w 200, h 45 , spanx");
		}

		getMainPanel().revalidate();
		getMainPanel().repaint();
	}

	protected JComponent toScaledImage(Image img) {
		Image scaled = ImageUtils.changeSizeKeepRatio(img, Math.min(getMaxImageWidth(), img.getWidth(null)),
				Math.min(getMaxImageHeight(), img.getHeight(null)));
		return new JLabel(new ImageIcon(scaled));
	}

	protected int getMaxImageWidth() {
		return 200;
	}

	protected int getMaxImageHeight() {
		return 200;
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		getLabelTitle().setText(Messages.get("selectiondisplay.title"));
		modelChanged();
	}
}
