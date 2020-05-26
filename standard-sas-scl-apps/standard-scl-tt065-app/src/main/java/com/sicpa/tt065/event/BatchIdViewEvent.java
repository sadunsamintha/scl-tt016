package com.sicpa.tt065.event;


import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.client.common.security.User;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.ProductionParameters;

import java.util.Date;

import static com.sicpa.standard.sasscl.provider.ProductBatchIdProvider.productionBatchId;

/**
 * Event to be fired when a manual intervention flow to the Selection Display View
 *
 * @author mjimenez
 *
 */
public class BatchIdViewEvent {
    public final User user;
    public final Date date;
    private ProductionParameters productionParameters;

    public BatchIdViewEvent(ProductionParameters productionParameters) {
        CustoBuilder.addPropertyToClass(ProductionParameters.class, productionBatchId);
        this.productionParameters = productionParameters;
        this.user = SecurityService.getCurrentUser();
        this.date = new Date();
    }

    public ProductionParameters getProductionParameters() {
        return productionParameters;
    }
}
