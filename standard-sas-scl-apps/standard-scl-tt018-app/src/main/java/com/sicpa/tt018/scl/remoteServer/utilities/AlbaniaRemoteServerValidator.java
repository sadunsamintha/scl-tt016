package com.sicpa.tt018.scl.remoteServer.utilities;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.tt018.interfaces.common.constant.MarketTypes;
import com.sicpa.tt018.interfaces.scl.master.dto.AlbaniaEncoderDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.CodeTypeDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.MarketTypeDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.SkuProductDTO;
import com.sicpa.tt018.interfaces.security.IAlbaniaEncoder;
import com.sicpa.tt018.scl.model.utilities.AlbaniaModelUtilities;
import com.sicpa.tt018.scl.remoteServer.adapter.mapping.AlbaniaRemoteServerProductStatusMapping;
import com.sicpa.tt018.scl.remoteServer.adapter.mapping.AlbaniaRemoteServerProductionModeMapping;
import com.sicpa.tt018.scl.remoteServer.constants.AlbaniaRemoteServerMessages;
import com.sicpa.tt018.scl.utils.AlbaniaUtilities;
import com.sicpa.tt018.scl.utils.ValidatorException;

public class AlbaniaRemoteServerValidator {
	private static Logger logger = LoggerFactory.getLogger(AlbaniaRemoteServerValidator.class);

	public static void validateMarketTyptDTOComplete(final MarketTypeDTO marketTypeDTO) throws ValidatorException {
		if (marketTypeDTO == null || AlbaniaUtilities.isEmpty(marketTypeDTO.getSkuList())) {
			logger.error("MarketTypeDTO is empty  = {}", marketTypeDTO);
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_EMPTY_MARKET_TYPE, marketTypeDTO);
		}
	}

	public static void validateMarketTyptDTOSupported(final int marketTypeId) throws ValidatorException {
		if (marketTypeId != MarketTypes.DOMESTIC_MASS_PRODUCER.getId()) {
			logger.error("Unsupported Market type ID recieved from Master = {}.", marketTypeId);
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_UNSUPPORTED_MARKET_TYPE, marketTypeId);
		}
	}

	public static void validateSkuDTOComplete(final SkuProductDTO skuProductDTO) throws ValidatorException {
		if (!AlbaniaRemoteServerUtilities.isComplete(skuProductDTO)) {
			logger.error("SkuProduct DTO is null or uncomplete = {}", skuProductDTO);
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_SKU_PRODUCT_DTO_INCOMPLETE,
					skuProductDTO);
		}
	}

	public static void validateCodeTypeDTOComplete(final CodeTypeDTO codeTypeDTO) throws ValidatorException {
		if (codeTypeDTO == null || codeTypeDTO.getId() == null || codeTypeDTO.getId() == 0) {
			logger.error("CodeType DTO is null or uncomplete = {}", codeTypeDTO);
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_CODE_TYPE_DTO_INCOMPLETE, codeTypeDTO);
		}
	}

	public static void validatePackagedProductsNotEmpty(final PackagedProducts packagedProducts)
			throws ValidatorException {
		if (AlbaniaRemoteServerUtilities.isEmpty(packagedProducts)) {
			logger.info("PackagedProducts is empty  = {}", packagedProducts);
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_EMPTY_PACKAGE_PRODUCTS,
					ToStringBuilder.reflectionToString(packagedProducts, ToStringStyle.MULTI_LINE_STYLE));
		}
	}

	public static void validateProductionModeIsSupported(final ProductionMode productionMode,
			final AlbaniaRemoteServerProductionModeMapping productionModeMapping) throws ValidatorException {
		// Not NULL
		if (productionMode == null) {
			logger.error("Production Mode is NULL");
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_PRODUCTION_MODE_NULL);
		}

		// Check if production Mode is supported
		if (!productionModeMapping.hasKey(productionMode)) {
			logger.error("Unsupported production mode = {}.", productionMode);
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_UNSUPPORTED_PRODUCTION_MODE,
					productionMode);
		}
	}

	public static void validateProductStatusIsSupported(final ProductStatus productStatus,
			final AlbaniaRemoteServerProductStatusMapping productStatusMapping, boolean isDomesticCheck)
			throws ValidatorException {
		// Not NULL
		if (productStatus == null) {
			logger.error("Product status is NULL");
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_PRODUCT_STATUS_NULL);
		}

		if (!productStatusMapping.hasKey(productStatus)) {
			logger.error("Unsupported product status = {}.", productStatus);
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_UNSUPPORTED_PRODUCT_STATUS,
					productStatus);
		}

		if (isDomesticCheck && productStatusMapping.getValue(productStatus) == null) {
			logger.error("Unsupported product status = {}.", productStatus);
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_UNSUPPORTED_PRODUCT_STATUS,
					productStatus);
		} else

		if (!isDomesticCheck && !ProductStatus.UNREAD.equals(productStatus)
				&& productStatusMapping.getValue(productStatus) != null) {
			logger.error("Unsupported product status = {}.", productStatus);
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_UNSUPPORTED_PRODUCT_STATUS,
					productStatus);
		}
	}

	public static void validateCodeNotNull(final Code code) throws ValidatorException {
		// Code must not be empty
		if (AlbaniaModelUtilities.isEmpty(code)) {
			logger.error("Invalid product Creation. Code is NULL or empty = {}");
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_CODE_NULL, code);
		}
	}

	public static void validateProductNotNull(final Product product) throws ValidatorException {
		// Code must not be empty
		if (product == null) {
			logger.error("Invalid product Creation. Product is NULL");
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_CODE_NULL);
		}
	}

	public static void validateProductSkuAndBatchIDNotNull(final Product Product) throws ValidatorException {
		// SKU must not be NULL
		if (Product.getSku() == null) {
			logger.error("Invalid product Creation. SKU is NULL = {}", Product);
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_SKU_NULL, Product);
		}
		// Batch ID must not be NULL
		if (StringUtils.isEmpty(Product.getProductionBatchId())) {
			logger.error("Invalid product Creation. Batch ID is NULL = {}", Product);
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_BATCH_ID_NULL, Product);
		}
	}

	public static void validateProductAndSkuCodeTypeMatch(Product product) throws ValidatorException {
		// Product Code type not NULL
		if (product.getCode().getCodeType() == null) {
			logger.error("Invalid product Creation. Product Code Type is NULL or empty = {}.", product.getStatus());
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_PRODUCT_CODE_TYPE_NULL, product);
		}

		// SKU Code Type not NULL
		if (product.getSku().getCodeType() == null) {
			logger.error("Invalid product Creation. SKU Code Type is NULL or empty = {}.", product.getStatus());
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_SKU_CODE_TYPE_NULL, product);
		}

		// Product & SKU Code type matching
		if (!product.getSku().getCodeType().equals(product.getCode().getCodeType())) {
			logger.error("Invalid product Creation. SKU and Prroduct Code Type not match product = {}, sku = {}.",
					product.getSku().getCodeType(), product.getCode());
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_SKU_CODE_TYPE_NOT_MATCH, product
					.getCode().getCodeType().getId(), product.getSku().getCodeType().getId());
		}
	}

	public static void validateEncoders(final List<AlbaniaEncoderDTO> encoders, final int qty, final int subsystemID)
			throws ValidatorException {
		// Validate Quantity
		if (encoders.size() > qty) {
			logger.error("Recived {0} encoders, expect {1}.", encoders.size(), qty);
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_TOO_MANY_ECODERS_RECIEVED, encoders
					.get(0).getClass().getName(), encoders.size(), qty);
		}

		// Validate each encoder data
		for (final AlbaniaEncoderDTO encoderDTO : encoders) {
			// Validate SubsystemID
			if (encoderDTO.getSubsystemId() != subsystemID) {
				logger.error("Encoders Received with invalid subsystemID = {} expected = {}.",
						encoderDTO.getSubsystemId(), subsystemID);
				throw new ValidatorException(
						AlbaniaRemoteServerMessages.EXCEPTION_ECODERS_RECIEVED_INVALID_SUBSYSTEM_ID, encoderDTO
								.getClass().getName(), encoderDTO.getSubsystemId(), subsystemID);
			}

			final IAlbaniaEncoder encoder = encoderDTO.getCryptoEncoder();
			// Wrapped encoder not Null
			if (encoder == null) {
				logger.error("Encoder recieved from master is NULL.");
				throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_NULL_ENCODER_RECIEVED, encoderDTO
						.getClass().getName());

			}

			logger.debug("Validating encoder... BatchID = {}, Capacity = {}.", encoder.getBatchId(),
					encoder.getCapacity());
			// BatchID Not NULL
			if (encoder.getBatchId() <= 0) {
				logger.error("Encoder batch ID recieved from master is NULL.");
				throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_NULL_ENCODER_BATCHID_RECIEVED,
						encoder.getClass().getName(), encoder.getBatchId());

			}
			// Capacity not NULL
			if (encoder.getCapacity() <= 0) {
				logger.error("Encoder capacity recieved from master is 0 !!!");
				throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_NULL_ENCODER_CAPACITY_RECIEVED,
						encoder.getClass().getName(), encoder.getCapacity());
			}
		}
	}

}
