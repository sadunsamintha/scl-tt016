package com.sicpa.tt065.scl;

import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.view.main.MainPanelGetter;
import com.sicpa.standard.sasscl.view.selection.select.SelectProductionParametersViewController;
import com.sicpa.tt065.view.sku.batchId.BatchIdSkuViewController;

/**
 * Created by wvieira on 15/09/2016.
 */
public class TT065Bootstrap extends Bootstrap {

    private MainPanelGetter mainPanelGetter;
    private SelectProductionParametersViewController selectProductionParametersViewController;
    private BatchIdSkuViewController batchIdSkuViewController;

    @Override
    public void executeSpringInitTasks() {
        super.executeSpringInitTasks();
    }


    public void setBatchIdSkuViewController(BatchIdSkuViewController batchIdSkuViewController) {
        this.batchIdSkuViewController = batchIdSkuViewController;
    }

    public void setMainPanelGetter(MainPanelGetter mainPanelGetter) {
        this.mainPanelGetter = mainPanelGetter;
    }

    public void setSelectProductionParametersViewController(SelectProductionParametersViewController selectProductionParametersViewController) {
        this.selectProductionParametersViewController = selectProductionParametersViewController;
    }

}
