package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import com.sicpa.gssd.tt079.common.api.activation.dto.production.authenticated.TT079AuthenticatedProductDto;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyFactory;
import com.sicpa.standard.sasscl.provider.ProductBatchIdExpDtProvider;
import com.sicpa.standard.sasscl.provider.ProductShipmentIdProvider;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductDto;

public class TT079DtoConverter extends DtoConverter implements ProductBatchIdExpDtProvider, ProductShipmentIdProvider {

	@Override
	protected AuthenticatedProductDto createAuthenticatedProductsDto(Product product, long codeTypeId) {
		CustomizablePropertyFactory.getCustomizablePropertyDefinition().addProperty(Product.class, productionBatchId);
		CustomizablePropertyFactory.getCustomizablePropertyDefinition().addProperty(Product.class, productionExpdt);
		CustomizablePropertyFactory.getCustomizablePropertyDefinition().addProperty(Product.class, productionShipmentId);
		
		return new TT079AuthenticatedProductDto((long) product.getSku().getId(), codeTypeId,
				product.getCode().getEncoderId(), product.getCode().getSequence(), product.getActivationDate(),
				product.getProperty(productionBatchId),
				product.getProperty(productionExpdt),
				product.getProperty(productionShipmentId));
	}

}
