package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;

public class CodeTypeValidator extends AbstractProductValidator {

	private static final Logger logger = LoggerFactory.getLogger(CodeTypeValidator.class);

	/**
	 * if the code type from the selected SKU match the code type from the cameraCode set the status to
	 * <code>ProductStatus.AUTHENTICATED</code> else to <code>ProductStatus.TYPE_MISMATCH</code>
	 */
	@Override
	public void validate(final Product product, final DecodedCameraCode cameraCode) {
		logger.debug("Product = {} , Decoded camera code = {}", product, cameraCode);

		if (productionParameters.getSku() != null
				&& (cameraCode.getCodeType() == null || cameraCode.getCodeType().equals(
						productionParameters.getSku().getCodeType()))) {
			product.setStatus(ProductStatus.AUTHENTICATED);
		} else {
			product.setStatus(ProductStatus.TYPE_MISMATCH);
		}
	}
}