package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import static com.sicpa.std.common.api.activation.business.ActivationServiceHandler.LABELED_PRODUCT_TYPE;
import static com.sicpa.std.common.api.activation.business.ActivationServiceHandler.MARKED_PRODUCT_TYPE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.devices.remote.GlobalMonitoringToolInfo;
import com.sicpa.standard.sasscl.devices.remote.mapping.IRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.StdCryptoAuthenticatorWrapper;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.std.common.api.activation.dto.AuthorizedProductsDto;
import com.sicpa.std.common.api.activation.dto.ProcessedProductsResultDto;
import com.sicpa.std.common.api.activation.dto.SicpadataReaderDto;
import com.sicpa.std.common.api.activation.dto.productionData.ProcessedProductsStatusDto;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductDto;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductsResultDto;
import com.sicpa.std.common.api.activation.dto.productionData.counted.CountedProductsDto;
import com.sicpa.std.common.api.activation.dto.productionData.counted.CountedProductsResultDto;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoDto;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorOrderDto;
import com.sicpa.std.common.api.monitoring.dto.EventDto;
import com.sicpa.std.common.api.monitoring.dto.EventTypeDto;

public class DtoConverter implements IDtoConverter {

	private ISkuConverter skuConverter;
	protected IRemoteServerProductStatusMapping productStatusMapping;
	private ICryptoFieldsConfig cryptoFieldsConfig;

	@Override
	public ProductionParameterRootNode convert(AuthorizedProductsDto products) {
		return skuConverter.convert(products);
	}

	@Override
	public CountedProductsResultDto convertToCountedDto(PackagedProducts products) {
		CountedProductsResultDto countedProductsResultDto = new CountedProductsResultDto();

		populateResultDtoInfo(countedProductsResultDto, products);
		countedProductsResultDto.setProcessedProducts(createCountedProductDto(products.getProducts()));

		ProcessedProductsStatusDto processedProductStatusDto = new ProcessedProductsStatusDto();
		processedProductStatusDto.setValue(productStatusMapping.getRemoteServerProdutcStatus(products
				.getProductStatus()));
		countedProductsResultDto.setProcessedProductsStatusDto(processedProductStatusDto);
		return countedProductsResultDto;
	}

	private CountedProductsDto createCountedProductDto(final List<Product> list) {
		Date start = null;
		Date end = null;
		long skuId = 0;
		long codeTypeId = 0;

		for (Product product : list) {
			skuId = product.getSku().getId();
			codeTypeId = product.getSku().getCodeType().getId();

			if (start == null) {
				start = end = product.getActivationDate();
			} else {
				if (product.getActivationDate().before(start)) {
					start = product.getActivationDate();
				}
				if (product.getActivationDate().after(end)) {
					end = product.getActivationDate();
				}
			}
		}
		return new CountedProductsDto(skuId, codeTypeId, (long) list.size(), start, end);
	}

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

			authenticatedProductsDto.add(new AuthenticatedProductDto((long) product.getSku().getId(), codeTypeId,
					product.getCode().getEncoderId(), product.getCode().getSequence(), product.getActivationDate()));
		}

		AuthenticatedProductsResultDto authenticatedProductsResultDto = new AuthenticatedProductsResultDto();
		populateResultDtoInfo(authenticatedProductsResultDto, products);
		authenticatedProductsResultDto.setProcessedProducts(authenticatedProductsDto);

		authenticatedProductsResultDto.setProcessedProductsStatusDto(statusDto);
		authenticatedProductsResultDto.setActivationType(getActivationServiceKey(products));

		return authenticatedProductsResultDto;
	}

	protected String getActivationServiceKey(PackagedProducts products) {

		if (products.isPrinted()) {
			// if SCL
			return MARKED_PRODUCT_TYPE;
		}
		// if SAS
		return LABELED_PRODUCT_TYPE;
	}

	@Override
	public EventDto createEventDto(GlobalMonitoringToolInfo info) {
		EventDto event = new EventDto();
		event.setEventTime(new Date());
		EventTypeDto type = new EventTypeDto();
		if (info.isProductionStarted()) {
			event.setEventMessage(Messages.get("globalMonitoringTool.info.started"));
			type.setBusinessCode("eventype.production.started");
		} else {
			event.setEventMessage(Messages.get("globalMonitoringTool.info.notstarted"));
			type.setBusinessCode("eventype.production.stopped");
		}

		event.setEventType(type);

		return event;
	}

	public void setProductStatusMapping(IRemoteServerProductStatusMapping productStatusMapping) {
		this.productStatusMapping = productStatusMapping;
	}

	@Override
	public List<SicpadataGeneratorInfoDto> createEncoderInfo(List<EncoderInfo> infos) {
		List<SicpadataGeneratorInfoDto> dtos = new ArrayList<>();

		for (EncoderInfo info : infos) {
			SicpadataGeneratorInfoDto dto = new SicpadataGeneratorInfoDto(info.getEncoderId(),
					(long) info.getCodeTypeId(), info.getSequence(), info.getFirstCodeDate(), info.getLastCodeDate());
			dtos.add(dto);
		}
		return dtos;
	}

	@Override
	public SicpadataGeneratorOrderDto newEncoderRequestOrder(int quantity, CodeType codeType, int year) {
		SicpadataGeneratorOrderDto dto = new SicpadataGeneratorOrderDto();
		dto.setQuantity(quantity);
		dto.setCodeTypeId(codeType.getId());
		dto.setYear(year);
		return dto;
	}

	@Override
	public IAuthenticator convert(SicpadataReaderDto readerDto) {
		return new StdCryptoAuthenticatorWrapper(readerDto.getSicpadataReader(), cryptoFieldsConfig);
	}

	public void setCryptoFieldsConfig(ICryptoFieldsConfig cryptoFieldsConfig) {
		this.cryptoFieldsConfig = cryptoFieldsConfig;
	}

	public void setSkuConverter(ISkuConverter skuConverter) {
		this.skuConverter = skuConverter;
	}
}
