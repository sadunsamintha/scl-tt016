package com.sicpa.tt085.business.production.impl;

import com.sicpa.standard.sasscl.business.production.impl.Production;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.tt085.model.provider.CountryProvider;


public class TT085Production extends Production implements  CountryProvider{
	
	private ProductionParameters pp;
	
	@Override
	protected void prepareProduct(Product product) {
		product.setSubsystem(subsystemIdProvider.get());
		if (product.getSku() != null) {
			SKU sku = product.getSku().copySkuForProductionData();
			CustoBuilder.addPropertyToClass(Product.class, country );
			if(pp.getProperty(country)!=null) {
				product.setProperty(country,pp.getProperty(country));
			}
			product.setSku(sku);
		}
	}
		
	public void setPp(ProductionParameters pp) {
		this.pp = pp;
	}

}
