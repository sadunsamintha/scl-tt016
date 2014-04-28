package com.sicpa.standard.sasscl.common.storage.productPackager;

import java.util.List;

import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;

/**
 * Defines a way of grouping products together. one group have to correspond to a single remote server call when sending
 * production
 * 
 * @author DIelsch
 */
public interface IProductsPackager {

	/***
	 * 
	 * group and return the products<br>
	 * one group of product have to correspond to one call to the remote server to keep a correct failover mechanism
	 * 
	 */
	List<PackagedProducts> getPackagedProducts(final List<Product> products);
}
