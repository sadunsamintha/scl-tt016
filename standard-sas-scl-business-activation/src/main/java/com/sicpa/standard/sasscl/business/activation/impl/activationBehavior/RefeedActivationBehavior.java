package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior;

import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.StandardActivationBehavior;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;

public class RefeedActivationBehavior extends StandardActivationBehavior {


  private String productionBehaviorVar;

  @Override
  protected void checkProduct(DecodedCameraCode result, Product product) {
    super.checkProduct(result, product);
    if (ProductStatus.AUTHENTICATED.equals(product.getStatus())) {
      product.setStatus(ProductStatus.REFEED);
    }
  }

  @Override
  public Product receiveCode(Code code, boolean valid) {

    if ("PRODUCTIONCONFIG-SCL".equalsIgnoreCase(productionBehaviorVar) && null != code.getStringCode()
        && ProductStatus.INK_DETECTED.getId() == Long.parseLong(code.getStringCode())) {
      return super.receiveCode(code, true);
    } else {
      return super.receiveCode(code, valid);
    }
  }

  public void setProductionBehaviorVar(String productionBehaviorVar) {
    this.productionBehaviorVar = productionBehaviorVar;
  }
}
