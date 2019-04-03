package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import java.util.ArrayList;

import com.sicpa.gssd.tt079.common.api.activation.dto.production.authenticated.TT079AuthenticatedProductDto;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.provider.ProductBatchIdExpDtProvider;
import com.sicpa.std.common.api.activation.dto.ProcessedProductsResultDto;
import com.sicpa.std.common.api.activation.dto.productionData.ProcessedProductsStatusDto;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductDto;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductsResultDto;

public class TT079DtoConverter extends DtoConverter implements ProductBatchIdExpDtProvider {
	
	private void populateResultDtoInfo(final ProcessedProductsResultDto dto, final PackagedProducts products) {
		dto.setSubsystemId(products.getSubsystem());
		dto.setIdTransaction(products.getUID());
		dto.setIdProductionBatch(products.getProductionBatchId());
		
	}

	@Override
	public AuthenticatedProductsResultDto convertToActivationDto(PackagedProducts products) {
		ArrayList<AuthenticatedProductDto> authenticatedProductsDto = new ArrayList<>();
		ProcessedProductsStatusDto statusDto = null;
		// create a product dto for each product
		for (Product product : products.getProducts()) {
			if (statusDto == null) {
				int remoteId = productStatusMapping.getRemoteServerProdutcStatus(products.getProductStatus());
				statusDto = new ProcessedProductsStatusDto();
				statusDto.setValue(remoteId);
			}

			// Product Code Type is null for marked products so get it from selected SKU
			long codeTypeId = product.getCode().getCodeType() != null ? product.getCode()
					.getCodeType().getId() : product.getSku().getCodeType().getId();


			authenticatedProductsDto.add(new TT079AuthenticatedProductDto((long) product.getSku().getId(), codeTypeId,
					product.getCode().getEncoderId(), product.getCode().getSequence(), product.getActivationDate(),
					product.getProperty(productionBatchId),product.getProperty(productionExpdt)));
		}

		AuthenticatedProductsResultDto authenticatedProductsResultDto = new AuthenticatedProductsResultDto();
		populateResultDtoInfo(authenticatedProductsResultDto, products);
		authenticatedProductsResultDto.setProcessedProducts(authenticatedProductsDto);
		authenticatedProductsResultDto.setProcessedProductsStatusDto(statusDto);
		authenticatedProductsResultDto.setActivationType(getActivationServiceKey(products));

		return authenticatedProductsResultDto;
	}
}
