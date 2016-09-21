package com.sicpa.standard.sasscl.view.selection.select.productionparameters;


import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.DefaultSelectionFlowView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.AbstractSelectionButtonView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.AbstractSelectionView;
import com.sicpa.standard.sasscl.controller.productionconfig.validator.ProductionParametersValidator;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;

import java.awt.*;

public class ProductionParametersSelectionFlowView extends DefaultSelectionFlowView{

    private ProductionParametersValidator productionParametersValidator;

    public ProductionParametersSelectionFlowView(){
        super();
        this.setUsePreviousPanel(false);

    }

    protected AbstractSelectionView createView(SelectableItem[] items) {
        AbstractSelectionView asv =  super.createView(items);
       if (items.length > 0 && items[0] instanceof ProductionModeNode) {
            for (int i = 0; i < items.length; i++) {
                ProductionModeNode pmn = (ProductionModeNode) items[i];
                if (!productionParametersValidator.validate(pmn.getValue())){
                    ((AbstractSelectionButtonView)asv).getPanel().getComponent(i).setEnabled(false);

                }

            }
        }
        return asv;
    }


    public void setProductionParametersValidator(ProductionParametersValidator productionParametersValidator) {
        this.productionParametersValidator = productionParametersValidator;
    }
}
