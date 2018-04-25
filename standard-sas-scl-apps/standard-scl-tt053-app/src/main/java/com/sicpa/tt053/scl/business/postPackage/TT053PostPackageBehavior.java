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
	
	@Override
	public List<Product> handleBadCode(Code code) {
		synchronized (codes) {
			if (!codes.isEmpty()) {
				List<Code> badCode = new ArrayList<Code>(1);
				logger.debug("handling the current bad code {}. The code removes is {} " , code, codes.get(0));

				badCode.add(codes.remove(0));
				ProductStatus productStatus = blobDetectionUtils.isBlobDetected(code) ? ProductStatus.INK_DETECTED :
						ProductStatus.SENT_TO_PRINTER_UNREAD;
				return generateBadProducts(badCode, productStatus);
			}
		}
		return Collections.emptyList();
	}

	@Override
	public List<Product> handleGoodCode(Code code) {
		synchronized (codes) {
			if (!codes.isEmpty()) {
				int index = indexOfCode(code.getStringCode());
				if (index != -1) {
					logger.debug("handeling the current good code {}. The code removes is {} " , code, codes.get(index));
					codes.remove(index);
					if (index != 0) {
						List<Code> subList = codes.subList(0, index);
						logger.warn("removing the following codes from the list {}. printer wasted " , codes.toString
								());

						List<Code> badCodes = new ArrayList<Code>(subList);
						subList.clear();
						return generateBadProducts(badCodes, ProductStatus.SENT_TO_PRINTER_WASTED);
					}
				} else {
					logger.warn("Code not found in buffer {}", code.getStringCode());
				}
			}
		}
		return Collections.emptyList();
	}
	
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
