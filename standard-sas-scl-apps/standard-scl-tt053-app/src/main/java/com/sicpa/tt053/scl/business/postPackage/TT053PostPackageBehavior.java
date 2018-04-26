package com.sicpa.tt053.scl.business.postPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.business.postPackage.PostPackageBehavior;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class TT053PostPackageBehavior extends PostPackageBehavior {
	
	private static final Logger logger = LoggerFactory.getLogger(TT053PostPackageBehavior.class);
	
	public TT053PostPackageBehavior(){
		super();
	}
	
	@Override
	protected List<Product> generateBadProducts(final List<Code> badCodes, ProductStatus status) {

		List<Product> products = new ArrayList<Product>();
		for (Code code : badCodes) {
			Product p = createProduct();
			p.setCode(code);
			p.setStatus(status);
			p.setQc(assosiatedCamera);
			
			if(productionParameters.getProductionMode().equals(ProductionMode.EXPORT)){
				p.getCode().setCodeType(code.getCodeType());
			}else {
				DecodedCameraCode decodedCameraCode = decode(code);
				if (decodedCameraCode != null) {
					setProductInfoWithCryptoResult(p, decodedCameraCode);
				}
				logger.debug("Generating product from post package status = {} , code = {}", status, code);
			}
			products.add(p);
		}
		return products;
	}

}
