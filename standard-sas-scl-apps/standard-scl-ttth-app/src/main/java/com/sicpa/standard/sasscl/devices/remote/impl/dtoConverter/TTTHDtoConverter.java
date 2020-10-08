package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import com.sicpa.gssd.ttth.common.api.activation.dto.production.authenticated.TTTHAuthenticatedProductDto;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyFactory;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.provider.ProductBatchJobIdProvider;
import com.sicpa.std.common.api.activation.dto.AuthorizedProductsDto;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductDto;

public class TTTHDtoConverter extends DtoConverter implements ProductBatchJobIdProvider {

	private DailyBatchRequestRepository dailyBatchRequestRepository;

	@Override
	public ProductionParameterRootNode convert(AuthorizedProductsDto products) {
		//Clear out the existing list.
		dailyBatchRequestRepository.clearDailyBatchRequest();
		return super.convert(products);
	}

	@Override
	protected AuthenticatedProductDto createAuthenticatedProductsDto(Product product, long codeTypeId) {
		CustomizablePropertyFactory.getCustomizablePropertyDefinition().addProperty(Product.class, productionBatchJobId);

		return new TTTHAuthenticatedProductDto((long) product.getSku().getId(), codeTypeId,
			product.getCode().getEncoderId(), product.getCode().getSequence(), product.getActivationDate(),
			product.getProperty(productionBatchJobId));
	}

	public void setDailyBatchRequestRepository(DailyBatchRequestRepository dailyBatchRequestRepository) {
		this.dailyBatchRequestRepository = dailyBatchRequestRepository;
	}
}
