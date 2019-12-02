package com.sicpa.tt080.sas.business.activation.impl.activationBehavior.validator;

import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator.AbstractProductValidator;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;

import static com.sicpa.tt080.sasscl.model.TT080ProductStatus.DECLARED;
import static com.sicpa.tt080.sasscl.model.TT080ProductStatus.TYPE_MISMATCH;

/**
 * Product Validator for SAS Production releated to FreeZone Behavior. Code was based on
 * Core's Validator {@link com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator.CodeTypeValidator}
 * This will render a similar result by with different Authentication status.
 */
public class FreeZoneTypeValidator extends AbstractProductValidator {

  /**
   * if the code type from the selected SKU match the code type from the cameraCode set the status to
   * <code>TT080ProductStatus.DECLARED</code> else to <code>TT080ProductStatus.TYPE_MISMATCH</code>
   */
  @Override
  public void validate(final Product product, final DecodedCameraCode cameraCode) {
    if (isAuthentic(cameraCode)) {
      product.setStatus(DECLARED);
    } else {
      product.setStatus(TYPE_MISMATCH);
    }
  }

  private boolean isAuthentic(final DecodedCameraCode cameraCode) {
    return cameraCode.getCodeType() == null
        || cameraCode.getCodeType().equals(productionParameters.getSku().getCodeType());
  }
}
