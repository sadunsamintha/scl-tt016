package com.sicpa.tt016.business.postpackage;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.business.postPackage.PostPackageBehavior;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.tt016.model.event.TT016NewProductEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TT016PostPackageBehavior extends PostPackageBehavior {

	private static final Logger logger = LoggerFactory.getLogger(TT016PostPackageBehavior.class);

	@Override
	public List<Product> handleBadCode(Code code) {
		List<Product> products = super.handleBadCode(code);

		if (!products.isEmpty()) {
			EventBusService.post(new TT016NewProductEvent(products.get(0)));
		} else {
			logger.warn("PostPackage returned 0 products, codes list size: " + codes.size());
		}

		//We return an empty list because we want to fire a product event to the ProductStatusMerger first and only
		// later to the Statistics, Storage, and other modules. By returning an empty list the
		// ActivationWithPostPackage won't fire the event.
		return Collections.emptyList();
	}

	@Override
	public List<Product> handleGoodCode(Code code) {
		super.handleGoodCode(code);

		//We return an empty list because we don't want the SENT_TO_PRINTER_WASTED products to be sent to the
		// ProductStatusMerger because it would result in the desynchronization of the queues of products
		return Collections.emptyList();
	}
	
//	@Override
//	protected List<Product> generateBadProducts(final List<Code> badCodes, ProductStatus status) {
//
//		List<Product> products = new ArrayList<Product>();
//		for (Code code : badCodes) {
//			Product p = createProduct();
//			p.setCode(code);
//			p.setStatus(status);
//			p.setQc(assosiatedCamera);
//			
//			if(productionParameters.getProductionMode().equals(ProductionMode.EXPORT)){
//				p.getCode().setCodeType(code.getCodeType());
//			}else {
//				DecodedCameraCode decodedCameraCode = decode(code);
//				if (decodedCameraCode != null) {
//					setProductInfoWithCryptoResult(p, decodedCameraCode);
//				}
//				logger.debug("Generating product from post package status = {} , code = {}", status, code);
//			}
//			products.add(p);
//		}
//		return products;
//	}
	
	/**
	 * decode encrypted code
	 *
	 * @param code
	 * @return
	 */
	@Override
	protected DecodedCameraCode decode(Code code) {
		try {
			return (DecodedCameraCode) authenticatorProvider.get().decode(authenticatorModeProvider.get(),
					code.getStringCode(), code.getCodeType());
		} catch (CryptographyException e) {
			logger.error("Failed to decode : " + e.getMessage(), e);
		}
		return null;
	}
}
