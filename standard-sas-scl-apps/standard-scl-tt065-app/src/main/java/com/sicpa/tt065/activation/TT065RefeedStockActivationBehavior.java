package com.sicpa.tt065.activation;


import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.StandardActivationBehavior;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.tt065.model.TT065ProductStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TT065RefeedStockActivationBehavior extends StandardActivationBehavior {

    private static final Logger logger = LoggerFactory.getLogger(TT065RefeedStockActivationBehavior.class);

    @Override
    protected void checkProduct(DecodedCameraCode result, Product product) {

        super.checkProduct(result, product);
        if (ProductStatus.AUTHENTICATED.equals(product.getStatus())) {
            product.setStatus(TT065ProductStatus.REFEED_STOCK);
            logger.debug("product.setStatus:"+TT065ProductStatus.REFEED_STOCK);
        }
    }
}
