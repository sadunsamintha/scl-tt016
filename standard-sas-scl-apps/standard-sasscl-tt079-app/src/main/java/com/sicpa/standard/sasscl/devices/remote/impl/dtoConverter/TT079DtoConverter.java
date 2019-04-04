package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import java.util.ArrayList;

import com.sicpa.gssd.tt079.common.api.activation.dto.production.authenticated.TT079AuthenticatedProductDto;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.provider.ProductBatchIdExpDtProvider;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductDto;

public class TT079DtoConverter extends DtoConverter implements ProductBatchIdExpDtProvider {

	@Override
	protected AuthenticatedProductDto createAuthenticatedProductsDto(Product product, long codeTypeId) {
		return new TT079AuthenticatedProductDto((long) product.getSku().getId(), codeTypeId,
				product.getCode().getEncoderId(), product.getCode().getSequence(), product.getActivationDate(),
				product.getProperty(productionBatchId),product.getProperty(productionExpdt));
	}

}
