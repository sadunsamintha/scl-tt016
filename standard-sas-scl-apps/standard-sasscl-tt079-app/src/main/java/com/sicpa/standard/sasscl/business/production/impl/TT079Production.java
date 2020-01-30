package com.sicpa.standard.sasscl.business.production.impl;

import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.ProductBatchIdExpDtProvider;

public class TT079Production extends Production implements ProductBatchIdExpDtProvider{
	
	private ProductionParameters pp;
	
	@Override
	protected void prepareProduct(Product product) {
		super.prepareProduct(product);
		CustoBuilder.addPropertyToClass(Product.class, productionBatchId );
		product.setProperty(productionBatchId,pp.getProperty(productionBatchId));
		CustoBuilder.addPropertyToClass(Product.class, productionExpdt );
		product.setProperty(productionExpdt,pp.getProperty(productionExpdt));
	}

	public void setPp(ProductionParameters pp) {
		this.pp = pp;
	}
	
}
