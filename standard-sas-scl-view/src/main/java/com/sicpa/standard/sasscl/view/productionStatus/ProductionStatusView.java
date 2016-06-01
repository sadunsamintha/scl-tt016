package com.sicpa.standard.sasscl.view.productionStatus;


import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.gui.plaf.SicpaColor;
import net.miginfocom.swing.MigLayout;

public class ProductionStatusView extends AbstractView<IProductionStatusViewListener, ProductionStatusViewModel> {

    private ProductStatusBar bar;

    public ProductionStatusView() {
        this(new ProductionStatusViewModel());
    }

    public ProductionStatusView(ProductionStatusViewModel model) {
        setModel(model);
        initGui();
        setBackground(SicpaColor.BLUE_DARK);
    }

    protected void initGui() {
        setLayout(new MigLayout("fill"));
        add(getBar(), "grow x");
    }

    @Override
    public void modelChanged() {
        startProgressBarStatus();
    }

    public ProductStatusBar getBar() {
        if (bar == null) {
            bar = new ProductStatusBar();
        }
        return bar;
    }

    private void startProgressBarStatus() {
        getBar().startProgressBarAnimation();
    }


}
