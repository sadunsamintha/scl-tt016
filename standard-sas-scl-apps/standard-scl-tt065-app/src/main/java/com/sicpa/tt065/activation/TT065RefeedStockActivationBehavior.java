package com.sicpa.tt065.activation;


import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.StandardActivationBehavior;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.tt065.model.TT065ProductStatus;

public class TT065RefeedStockActivationBehavior extends StandardActivationBehavior {

    @Override
    protected void checkProduct(DecodedCameraCode result, Product product) {

        super.checkProduct(result, product);
        if (ProductStatus.AUTHENTICATED.equals(product.getStatus())) {
            product.setStatus(TT065ProductStatus.REFEED_STOCK);
        }
    }
}
