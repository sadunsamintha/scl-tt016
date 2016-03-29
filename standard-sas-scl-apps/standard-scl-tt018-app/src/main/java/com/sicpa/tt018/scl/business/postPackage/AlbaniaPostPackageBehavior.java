package com.sicpa.tt018.scl.business.postPackage;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.business.postPackage.PostPackageBehavior;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.tt018.scl.model.AlbaniaProduct;
import com.sicpa.tt018.scl.model.AlbaniaProductStatus;
import com.sicpa.tt018.scl.utils.AlbaniaBlobUtils;

public class AlbaniaPostPackageBehavior extends PostPackageBehavior {

	private AlbaniaBlobUtils blobUtils;

	private static final Logger logger = LoggerFactory.getLogger(AlbaniaPostPackageBehavior.class);

	@Override
	public List<Product> handleBadCode(Code code) {
		synchronized (codes) {
			if (codes.isEmpty()) {
				return Collections.emptyList();
			}

			Code removedCode = codes.remove(0);

			ProductStatus productStatus = blobUtils.isBlobEnable() && blobUtils.isBlobDetected(code) ? AlbaniaProductStatus.SENT_TO_PRINTER_BLOB : ProductStatus.SENT_TO_PRINTER_UNREAD;
			logger.debug("Handling bad code {} with product status  {}", code, productStatus);
			return generateBadProducts(Arrays.asList(removedCode), productStatus);
		}
	}

	public void setBlobUtils(AlbaniaBlobUtils blobUtils) {
		this.blobUtils = blobUtils;
	}

	@Override
	protected Product createProduct() {
		Product p = getNewAlbaniaProduct();
		configureProduct(p);
		return p;
	}

	public Product getNewAlbaniaProduct() {
		return new AlbaniaProduct(productionParameters.getProductionMode());

	}

	private void configureProduct(Product product) {
		product.setActivationDate(new Date());
		product.setProductionBatchId(batchIdProvider.get());
		product.setSubsystem(subsystemIdProvider.get());
		product.setSku(productionParameters.getSku());
		product.setPrinted(true);
	}

}
