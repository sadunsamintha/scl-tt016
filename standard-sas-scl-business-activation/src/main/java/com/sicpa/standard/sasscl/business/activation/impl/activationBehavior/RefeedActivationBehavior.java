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
        && String.valueOf(ProductStatus.INK_DETECTED.getId()).equals(code.getStringCode())) {
      return super.receiveCode(code, true);
    } else {
      return super.receiveCode(code, valid);
    }
  }

  public void setProductionBehaviorVar(String productionBehaviorVar) {
    this.productionBehaviorVar = productionBehaviorVar;
  }

  protected DecodedCameraCode getDecodedCameraCode(Code code) {
    if ("PRODUCTIONCONFIG-SCL".equalsIgnoreCase(productionBehaviorVar) && null != code.getStringCode()
        && String.valueOf(ProductStatus.INK_DETECTED.getId()).equals(code.getStringCode())) {

      // INK_DETECTED codes have "100" as their code so the SICPADATA could not be decoded from the code. So, we have to
      // manually set the values for INK_DETECTED as AUTHENTICATED to ensure it will be counted as REFEED instead of INVALID
      DecodedCameraCode res = new DecodedCameraCode();
      res.setCodeType(productionParameters.getSku().getCodeType());
      res.setBatchId(-1);
      res.setAuthenticated(true);
      res.setSequence(Long.parseLong(code.getStringCode()));

      return res;
    } else {
      return super.getDecodedCameraCode(code);
    }
  }
}
