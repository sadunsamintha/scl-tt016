package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator;

import static com.sicpa.standard.sasscl.model.ProductStatus.AUTHENTICATED;
import static com.sicpa.standard.sasscl.model.ProductStatus.TYPE_MISMATCH;

import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;

public class CodeTypeValidator extends AbstractProductValidator {

	/**
	 * if the code type from the selected SKU match the code type from the cameraCode set the status to
	 * <code>ProductStatus.AUTHENTICATED</code> else to <code>ProductStatus.TYPE_MISMATCH</code>
	 */
	@Override
	public void validate(Product product, DecodedCameraCode cameraCode) {
		if (isAuthentic(cameraCode)) {
			product.setStatus(AUTHENTICATED);
		} else {
			product.setStatus(TYPE_MISMATCH);
		}
	}

	private boolean isAuthentic(DecodedCameraCode cameraCode) {
		return cameraCode.getCodeType() == null
				|| cameraCode.getCodeType().equals(productionParameters.getSku().getCodeType());
	}
}