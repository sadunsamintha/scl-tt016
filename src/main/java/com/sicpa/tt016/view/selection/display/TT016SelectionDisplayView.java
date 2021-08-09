package com.sicpa.tt016.view.selection.display;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.text.MultiLineLabel;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.view.selection.display.SelectionDisplayView;
import com.sicpa.tt016.scl.model.MoroccoSKU;

public class TT016SelectionDisplayView extends SelectionDisplayView {

    private static final Logger logger = LoggerFactory.getLogger(TT016SelectionDisplayView.class);

    private boolean isBeamEnabled;

    @Override
    protected void buildSelectionPanel(ProductionParameters pp) {
        if (!isBeamEnabled) {
            super.buildSelectionPanel(pp);
            return;
        }

        getMainPanel().removeAll();

        MoroccoSKU sku = (MoroccoSKU) pp.getSku();
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
            getMainPanel().add(new MultiLineLabel(sku.getDescription() + "\n" + sku.getProductHeight() + "mm"), "grow, w 200, h 175 , spanx");
        }

        if (barcode != null) {
            getMainPanel().add(new MultiLineLabel(barcode), "grow, w 200, h 45 , spanx");
        }

        getMainPanel().revalidate();
        getMainPanel().repaint();
    }

    public void setBeamEnabled(boolean beamEnabled) {
        isBeamEnabled = beamEnabled;
    }

}
