package com.sicpa.tt079.event;


import java.util.Date;

import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.client.common.security.User;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.ProductionParameters;

import static com.sicpa.standard.sasscl.provider.ProductBatchIdExpDtProvider.productionBatchId;
import static com.sicpa.standard.sasscl.provider.ProductBatchIdExpDtProvider.productionExpdt;

/**
 * Event to be fired when a manual intervention flow to the Selection Display View
 *
 *
 */
public class BatchIdExpViewEvent {
    public final User user;
    public final Date date;
    private ProductionParameters productionParameters;

    public BatchIdExpViewEvent(ProductionParameters productionParameters) {
        this.productionParameters = productionParameters;
        this.user = SecurityService.getCurrentUser();
        this.date = new Date();
    }

    public ProductionParameters getProductionParameters() {
        return productionParameters;
    }
}
