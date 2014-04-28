package com.sicpa.standard.sasscl.common.storage.productPackager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.utils.DateUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;

public class DefaultProductsPackager implements IProductsPackager {

	private Logger logger = LoggerFactory.getLogger(DefaultProductsPackager.class);

	protected String timeStampFormat = "yyyy-MM-dd--HH-mm-ss-SSS";

	/**
	 * return PackagedProducts<br>
	 * file name format of those PacakgeProducts is timestamp-status-batchid
	 */
	@Override
	public List<PackagedProducts> getPackagedProducts(final List<Product> products) {

		String dateForFileName = DateUtils.format(timeStampFormat, new Date()) + "_";

		Map<String, PackagedProducts> mapPackagedProducts = new HashMap<String, PackagedProducts>();
		long uid = System.currentTimeMillis();

		for (Product product : products) {
			if (product != null) {

				if (product.getStatus() != null) {
					String filenameKey = product.getStatus().toString();
					String fileName = dateForFileName + filenameKey + "-" + product.getProductionBatchId() + "-"
							+ (product.isPrinted() ? "Y" : "N");
					PackagedProducts packaged = mapPackagedProducts.get(fileName);
					if (packaged == null) {
						packaged = new PackagedProducts(uid++, product.getStatus(), product.getProductionBatchId(),
								product.getSubsystem(), product.isPrinted());
						packaged.setFileName(fileName);
						mapPackagedProducts.put(fileName, packaged);
					}
					packaged.getProducts().add(product);
				} else {
					logger.error("The product status is null. Product: " + product.toString());
				}
			}
		}

		List<PackagedProducts> res = new ArrayList<PackagedProducts>();
		res.addAll(mapPackagedProducts.values());

		ThreadUtils.waitForNextTimeStamp();

		return res;
	}
}
