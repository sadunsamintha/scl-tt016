package com.sicpa.tt018.scl.remoteServer.adapter;

import static com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator.validateCodeNotNull;
import static com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator.validateCodeTypeDTOComplete;
import static com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator.validateMarketTyptDTOComplete;
import static com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator.validateMarketTyptDTOSupported;
import static com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator.validatePackagedProductsNotEmpty;
import static com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator.validateProductAndSkuCodeTypeMatch;
import static com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator.validateProductNotNull;
import static com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator.validateProductSkuAndBatchIDNotNull;
import static com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator.validateProductStatusIsSupported;
import static com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator.validateProductionModeIsSupported;
import static com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator.validateSkuDTOComplete;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.common.exception.ProductionRuntimeException;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.tt018.interfaces.scl.master.constant.ProductPackagings;
import com.sicpa.tt018.interfaces.scl.master.constant.ProductStatuses;
import com.sicpa.tt018.interfaces.scl.master.constant.ProductionModes;
import com.sicpa.tt018.interfaces.scl.master.dto.CodeTypeDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.CountedProductsDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.EjectedActivationsDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.MarketTypeDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.PackagedProductsDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.ProductDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.SkuProductDTO;
import com.sicpa.tt018.scl.model.AlbaniaProduct;
import com.sicpa.tt018.scl.model.AlbaniaSKU;
import com.sicpa.tt018.scl.model.authenticator.utilities.AlbaniaAuthenticatorUtilities;
import com.sicpa.tt018.scl.model.productionParameters.impl.AlbaniaNavigationNode;
import com.sicpa.tt018.scl.model.productionParameters.impl.AlbaniaProductionModeNode;
import com.sicpa.tt018.scl.remoteServer.adapter.mapping.AlbaniaRemoteServerProductStatusMapping;
import com.sicpa.tt018.scl.remoteServer.adapter.mapping.AlbaniaRemoteServerProductionModeMapping;
import com.sicpa.tt018.scl.remoteServer.constants.AlbaniaRemoteServerConstants;
import com.sicpa.tt018.scl.remoteServer.constants.AlbaniaRemoteServerMessages;
import com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerUtilities;
import com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator;
import com.sicpa.tt018.scl.utils.ValidatorException;

public class AlbaniaRemoteServerAdapter implements IAlbaniaRemoteServerAdapter {

	private static final int ALLOWED_PRODUCT_BOTTLE = 0;
	private static final int ALLOWED_PRODUCT_CAN = 1;
	private static final int ALLOWED_PRODUCT_CAN_BOTTLE = 2;

	private static Logger logger = LoggerFactory.getLogger(AlbaniaRemoteServerAdapter.class);

	public static final int COUNTING_SKU_ID = -1;

	private int treeDepth;
	private final AlbaniaRemoteServerProductStatusMapping productStatusMapping = new AlbaniaRemoteServerProductStatusMapping();
	private final AlbaniaRemoteServerProductionModeMapping productionModeMapping = new AlbaniaRemoteServerProductionModeMapping();

	private IAuthenticator authenticator;

	private int allowedProductPackage;

	private List<ProductionMode> getCountingProductionMode() {
		return asList(AlbaniaRemoteServerConstants.COUNTING_MODES_SCL);
	}

	@Override
	public ProductionParameterRootNode createSkuSelectionTree(MarketTypeDTO marketTypeDTO) {

		// Root node to be returned
		ProductionParameterRootNode root = new ProductionParameterRootNode();

		// filter SKUs
		Iterator<SkuProductDTO> it = marketTypeDTO.getSkuList().iterator();
		while (it.hasNext()) {
			SkuProductDTO item = it.next();
			try {
				AlbaniaSKU currentSku = (AlbaniaSKU) convertFromMaster(item);
				if (currentSku.getProductPackaging().getId() >= ProductPackagings.OTHER.getId()
						|| allowedProductPackage != ALLOWED_PRODUCT_CAN_BOTTLE
						&& currentSku.getProductPackaging().getId() != allowedProductPackage + 1) {
					it.remove();
				}
			} catch (ValidatorException e) {
				logger.error("Error during MarketTypeDTO validation = {}. Exception = {}",
						ToStringBuilder.reflectionToString(marketTypeDTO, ToStringStyle.MULTI_LINE_STYLE), e);
			}
		}

		// if we are in CAN or BOTTLE mode, create the tree with SASSCL rules
		// for layout (as existing before Kaon CR)
		if (allowedProductPackage != ALLOWED_PRODUCT_CAN_BOTTLE) {
			// Add domestic SKU tree

			if (!marketTypeDTO.getSkuList().isEmpty()) {
				addCommonDomesticModesToSkuTreeSelection(root, marketTypeDTO);
			}
			// Add Counting SKU tree
			addCommonCountingModeToSkuTreeSelection(root);
		}
		// if we are in CAN + BOTTLE mode, create the tree according to specific
		// rules
		else {
			// Add domestic SKU tree
			if (!marketTypeDTO.getSkuList().isEmpty()) {
				addDomesticModesToSkuTreeSelection(root, marketTypeDTO);
			}
			// Add Counting SKU tree
			addCountingModeToSkuTreeSelection(root);
		}

		logger.info("SKU tree created " + ToStringBuilder.reflectionToString(root, ToStringStyle.MULTI_LINE_STYLE));
		return root;
	}

	public void addDomesticModesToSkuTreeSelection(ProductionParameterRootNode root, MarketTypeDTO marketTypeDTO) {
		try {
			// Building the part of the tree for the "Domestic" production mode
			ProductionModeNode nationalProductionModeNode = new AlbaniaProductionModeNode(
					convertFromMarketTypeId(marketTypeDTO.getId()));
			root.addChildren(nationalProductionModeNode);

			// Maps of package type to create the SKU tree
			Map<String, AlbaniaNavigationNode> brands = new HashMap<>();

			// Iterate all SKUs from sorted SKU list from MarketTypeDTO
			for (SkuProductDTO skuProductDTO : AlbaniaRemoteServerUtilities.SkuDtoSorter.sort(marketTypeDTO
					.getSkuList())) {

				// -------------------------------------
				// Node 0 Package Type
				// -------------------------------------
				String currentPackageName;

				switch (skuProductDTO.getProductPackagingsId()) {
				case 1:
					currentPackageName = Messages.get(AlbaniaRemoteServerMessages.PACKAGE_BOTTLE);
					break;
				case 2:
					currentPackageName = Messages.get(AlbaniaRemoteServerMessages.PACKAGE_CAN);
					break;
				default:
					currentPackageName = Messages.get(AlbaniaRemoteServerMessages.PACKAGE_OTHER);
					break;
				}
				// Allow only bottle or can package types
				if (skuProductDTO.getProductPackagingsId() != ProductPackagings.BOTTLE.getId()
						&& skuProductDTO.getProductPackagingsId() != ProductPackagings.CAN.getId()) {
					continue;
				}

				// Checking if the Brand already exist in the tree, if it
				// doesn't then we add it
				AlbaniaNavigationNode currentPackageNode = brands.get(currentPackageName);
				if (currentPackageNode == null) {
					currentPackageNode = new AlbaniaNavigationNode(currentPackageName);
					brands.put(currentPackageName, currentPackageNode);
					nationalProductionModeNode.addChildren(currentPackageNode);
				}
				// -------------------------------------
				// Node 1 Brand + Variant + Description
				// -------------------------------------
				String currentFullName = skuProductDTO.getBrand().trim() + " "
						+ (skuProductDTO.getVariant() != null ? skuProductDTO.getVariant().trim() : "") + " "
						+ skuProductDTO.getDescription().trim();

				AlbaniaSKU sku;

				sku = convertFromMaster(skuProductDTO);
				SKUNode currentFullNode = new SKUNode(sku);
				currentFullNode.setText(currentFullName);
				currentPackageNode.addChildren(currentFullNode);

			}
		} catch (ValidatorException e) {
			logger.error("Error during MarketTypeDTO validation = {}. Exception = {}",
					ToStringBuilder.reflectionToString(marketTypeDTO, ToStringStyle.MULTI_LINE_STYLE), e);
		}

	}

	public void addCommonDomesticModesToSkuTreeSelection(ProductionParameterRootNode root, MarketTypeDTO marketTypeDTO) {
		logger.debug("Adding Domestic mode. Depth = " + getTreeDepth());
		try {
			// Empty Arg check
			validateMarketTyptDTOComplete(marketTypeDTO);

			// Depending on the tree depth
			switch (getTreeDepth()) {
			case 1:
				addSkuTreeSelectionDomesticModeDepth1(root, marketTypeDTO);
				break;
			case 2:
				addSkuTreeSelectionDomesticModeDepth2(root, marketTypeDTO);
				break;
			case 20:
				addSkuTreeSelectionDomesticModeDepth2bis(root, marketTypeDTO);
				break;
			case 3:
				addSkuTreeSelectionDomesticModeDepth3(root, marketTypeDTO);
				break;
			default:
				addSkuTreeSelectionDomesticModeDepth1(root, marketTypeDTO);
				break;
			}
		} catch (ValidatorException e) {
			logger.error("Error during MarketTypeDTO validation = {}. Exception = {}",
					ToStringBuilder.reflectionToString(marketTypeDTO, ToStringStyle.MULTI_LINE_STYLE), e);
		}
	}

	private void addCountingModeToSkuTreeSelection(ProductionParameterRootNode root) {
		for (ProductionMode productionMode : getCountingProductionMode()) {
			logger.debug("Adding " + productionMode.getDescription() + " counting mode");

			AlbaniaProductionModeNode productionNode = new AlbaniaProductionModeNode(productionMode);
			root.addChildren(productionNode);

			// -------------------------------------
			// CAN Package Type
			// -------------------------------------
			AlbaniaSKU canSKU = new AlbaniaSKU(COUNTING_SKU_ID, Messages.get(AlbaniaRemoteServerMessages.PACKAGE_CAN),
					"", "", "", emptyList(), false, ProductPackagings.CAN);
			SKUNode canNode = new SKUNode(canSKU);
			canNode.setText(Messages.get(AlbaniaRemoteServerMessages.PACKAGE_CAN));
			productionNode.addChildren(canNode);

			// -------------------------------------
			// BOTTLE Package Type
			// -------------------------------------
			AlbaniaSKU bottleSKU = new AlbaniaSKU(COUNTING_SKU_ID,
					Messages.get(AlbaniaRemoteServerMessages.PACKAGE_BOTTLE), "", "", "", emptyList(), false,
					ProductPackagings.BOTTLE);
			SKUNode bottleNode = new SKUNode(bottleSKU);
			bottleNode.setText(Messages.get(AlbaniaRemoteServerMessages.PACKAGE_BOTTLE));
			productionNode.addChildren(bottleNode);
		}

	}

	private void addCommonCountingModeToSkuTreeSelection(ProductionParameterRootNode root) {
		for (ProductionMode productionMode : getCountingProductionMode()) {
			logger.debug("Adding " + productionMode.getDescription() + " counting mode");
			root.addChildren(new AlbaniaProductionModeNode(productionMode));
		}
	}

	public int getAllowedProductType() {
		return allowedProductPackage;
	}

	public void setAllowedProductType(final String allowedProductType) {
		this.allowedProductPackage = Integer.parseInt(allowedProductType);
	}

	@Override
	public PackagedProductsDTO convertDomesticProduction(PackagedProducts packagedProducts, int subsystemId) {
		try {
			validatePackagedProductsNotEmpty(packagedProducts);

			Product initProduct = packagedProducts.getProducts().get(0);

			restoreProductParam(initProduct, packagedProducts, subsystemId);

			// Data validation
			validateDomesticProduct(initProduct);

			// PackagedProductsDTO creation
			final PackagedProductsDTO packagedProductsDTO = new PackagedProductsDTO();
			packagedProductsDTO.setSubsystemId(subsystemId);
			packagedProductsDTO.setUid(packagedProducts.getUID());
			packagedProductsDTO.setCustomerSkuId(initProduct.getSku().getId());
			packagedProductsDTO.setProductionBatchId(initProduct.getProductionBatchId());
			packagedProductsDTO.setProductionMode(convertToMaster(getProductionModeFromProduct(initProduct)));

			// Add all product
			packagedProductsDTO.setProducts(convertDomesticProductToDTOList(packagedProducts, subsystemId));

			return packagedProductsDTO;

		} catch (CryptographyException e) {
			logger.error("Error while converting product to DTO {" + getClass().getName() + "} - {" + packagedProducts
					+ " } ", e);
			return null;
		} catch (ValidatorException e) {
			logger.error("Error while validating product to DTO {" + getClass().getName() + "} - {" + packagedProducts
					+ " } ", e);
			return null;
		}

	}

	private void restoreProductParam(Product p, PackagedProducts packagedProducts, int subsystemId) {
		p.setStatus(packagedProducts.getProductStatus());
		p.setProductionBatchId(packagedProducts.getProductionBatchId());
		p.setSubsystem(packagedProducts.getSubsystem());
		p.setPrinted(packagedProducts.isPrinted());
	}

	private ProductionMode getProductionModeFromProduct(Product product) {
		return ((AlbaniaProduct) product).getProdMode();
	}

	@Override
	public EjectedActivationsDTO convertEjectedProduction(PackagedProducts products, int subsystemId) {
		logger.error("Error convertEjectedProduction is not implemented on SCL.");
		throw new ProductionRuntimeException(
				AlbaniaRemoteServerMessages.EXCEPTION_CONVERT_EJECTED_PRODUCTS_NOT_SUPPORTED);
	}

	@Override
	public CountedProductsDTO convertCountingProduction(PackagedProducts packagedProducts, int subsystemId) {
		// NULL arg check
		try {
			AlbaniaRemoteServerValidator.validatePackagedProductsNotEmpty(packagedProducts);

			// Init product
			Product initProduct = packagedProducts.getProducts().get(0);

			restoreProductParam(initProduct, packagedProducts, subsystemId);

			// Data validation
			validateCountingProduct(initProduct);

			// CountedProductsDTO creation
			final CountedProductsDTO countedProducts = new CountedProductsDTO();
			countedProducts.setSubsystemId(subsystemId);
			countedProducts.setUid(packagedProducts.getUID());
			countedProducts.setProductionMode(convertToMaster(getProductionModeFromProduct(initProduct)));

			// Initialize begin & End Date
			countedProducts.setBeginDate(initProduct.getActivationDate());
			countedProducts.setEndDate(initProduct.getActivationDate());

			// To count only product without error
			int prodCount = 0;

			// Iterate all products and check for begin and end date
			for (Product product : packagedProducts.getProducts()) {
				// Means product in Error
				if (ProductStatus.UNREAD.equals(product.getStatus())) {
					logger.debug("Error product in Counting mode - NOT SEND TO DMS - Product = {}.", product);

				} else
				// Counted products to send
				{
					prodCount++;
					// Check for Begin and End Date
					if (countedProducts.getBeginDate().after(product.getActivationDate())) {
						countedProducts.setBeginDate(product.getActivationDate());
					} else if (countedProducts.getEndDate().before(product.getActivationDate())) {
						countedProducts.setEndDate(product.getActivationDate());
					}
				}
			}
			countedProducts.setNumberOfProducts(prodCount);

			// Means Only product in Error (Should never append)
			if (prodCount == 0) {
				logger.info("Sending {} production with only product in error - PackagedProducts = {}.",
						getProductionModeFromProduct(initProduct),
						ToStringBuilder.reflectionToString(packagedProducts, ToStringStyle.MULTI_LINE_STYLE));
				return null;
			}

			logger.debug("PackagedProductsDTO = {}",
					ToStringBuilder.reflectionToString(countedProducts, ToStringStyle.MULTI_LINE_STYLE));

			return countedProducts;
		} catch (ValidatorException e) {
			logger.error("Error during validation of counted products ", e);
			return null;
		}
	}

	// -----------------------------------------------------------------------
	// //
	// SKU TREE UTILITY METHODS
	// -----------------------------------------------------------------------
	// //

	private void addSkuTreeSelectionDomesticModeDepth1(ProductionParameterRootNode root, MarketTypeDTO marketTypeDTO)
			throws ValidatorException {
		// Building the part of the tree for the "Domestic" production mode
		ProductionModeNode nationalProductionModeNode = new AlbaniaProductionModeNode(
				convertFromMarketTypeId(marketTypeDTO.getId()));
		root.addChildren(nationalProductionModeNode);

		// Maps of brands and variants to create the SKU tree
		Map<String, SKUNode> skus = new HashMap<>();

		// Iterate all SKUs from sorted SKU list from MarketTypeDTO
		for (SkuProductDTO skuProductDTO : AlbaniaRemoteServerUtilities.SkuDtoSorter.sort(marketTypeDTO.getSkuList())) {

			// -------------------------------------
			// Node 1 Brand + Variant + Description
			// -------------------------------------
			String currentFullName = skuProductDTO.getBrand().trim() + " "
					+ (skuProductDTO.getVariant() != null ? skuProductDTO.getVariant().trim() : "") + " "
					+ skuProductDTO.getDescription().trim();

			// Checking if the Brand already exist in the tree, if it doesn't
			// then we add it
			SKUNode currentFullNode = skus.get(currentFullName);
			if (currentFullNode == null) {
				AlbaniaSKU sku = convertFromMaster(skuProductDTO);
				currentFullNode = new SKUNode(sku);
				currentFullNode.setText(currentFullName);

				nationalProductionModeNode.addChildren(currentFullNode);
				skus.put(currentFullName, currentFullNode);
			}

			// Same description should not appear 2 times in the SKU list
			else {
				logger.info(
						"SKU already exist in production tree, brand = {} , varinat = {} , description = {}.",
						new Object[] { skuProductDTO.getBrand(), skuProductDTO.getVariant(),
								skuProductDTO.getDescription() });
			}
		}
	}

	private void addSkuTreeSelectionDomesticModeDepth2(ProductionParameterRootNode root, MarketTypeDTO marketTypeDTO)
			throws ValidatorException {
		// Building the part of the tree for the "Domestic" production mode
		ProductionModeNode nationalProductionModeNode = new AlbaniaProductionModeNode(
				convertFromMarketTypeId(marketTypeDTO.getId()));
		root.addChildren(nationalProductionModeNode);

		// Maps of brands and variants to create the SKU tree
		Map<String, AlbaniaNavigationNode> brands = new HashMap<>();

		// Iterate all SKUs from sorted SKU list from MarketTypeDTO
		for (SkuProductDTO skuProductDTO : AlbaniaRemoteServerUtilities.SkuDtoSorter.sort(marketTypeDTO.getSkuList())) {

			// -------------------------------------
			// Node 1 Brand
			// -------------------------------------
			String currentBrandName = skuProductDTO.getBrand().trim();

			// Checking if the Brand already exist in the tree, if it doesn't
			// then we add it
			AlbaniaNavigationNode currentBrandNode = brands.get(currentBrandName);
			if (currentBrandNode == null) {
				currentBrandNode = new AlbaniaNavigationNode(currentBrandName);
				nationalProductionModeNode.addChildren(currentBrandNode);
				brands.put(currentBrandName, currentBrandNode);
			}

			// -------------------------------------
			// Node 2 Variant + Description
			// -------------------------------------
			String currentVariantDescritptionName = (skuProductDTO.getVariant() != null ? skuProductDTO.getVariant()
					.trim() : "") + " " + skuProductDTO.getDescription().trim();

			// Checking if the description already exist in the tree, if it
			// doesn't then we add it
			if (currentBrandNode.getChild(currentVariantDescritptionName) == null) {
				SKUNode skuMode = new SKUNode(convertFromMaster(skuProductDTO));
				skuMode.setText(currentVariantDescritptionName);
				currentBrandNode.addChildren(skuMode);
			}

			// Same description should not appear 2 times in the SKU list
			else {
				logger.info(
						"SKU already exist in production tree, brand = {} , variant = {} , description = {}.",
						new Object[] { skuProductDTO.getBrand(), skuProductDTO.getVariant(),
								skuProductDTO.getDescription() });
			}
		}
	}

	private void addSkuTreeSelectionDomesticModeDepth2bis(ProductionParameterRootNode root, MarketTypeDTO marketTypeDTO)
			throws ValidatorException {

		// Building the part of the tree for the "Domestic" production mode
		ProductionModeNode nationalProductionModeNode = new AlbaniaProductionModeNode(
				convertFromMarketTypeId(marketTypeDTO.getId()));
		root.addChildren(nationalProductionModeNode);

		// Maps of brands and variants to create the SKU tree
		Map<String, AlbaniaNavigationNode> brands = new HashMap<String, AlbaniaNavigationNode>();

		// Iterate all SKUs from sorted SKU list from MarketTypeDTO
		for (SkuProductDTO skuProductDTO : AlbaniaRemoteServerUtilities.SkuDtoSorter.sort(marketTypeDTO.getSkuList())) {
			// -------------------------------------
			// Node 1 Brand + Variant
			// -------------------------------------
			String currentBrandVariantName = skuProductDTO.getBrand().trim() + " "
					+ (skuProductDTO.getVariant() != null ? skuProductDTO.getVariant().trim() : "");

			// Checking if the Brand already exist in the tree, if it doesn't
			// then we add it
			AlbaniaNavigationNode currentBrandVariantNode = brands.get(currentBrandVariantName);
			if (currentBrandVariantNode == null) {
				currentBrandVariantNode = new AlbaniaNavigationNode(currentBrandVariantName);
				brands.put(currentBrandVariantName, currentBrandVariantNode);
				nationalProductionModeNode.addChildren(currentBrandVariantNode);
			}

			// -------------------------------------
			// Node 2 Description
			// -------------------------------------
			// Checking if the description already exist in the tree, if it
			// doesn't then we add it
			if (currentBrandVariantNode.getChild(skuProductDTO.getDescription()) == null) {
				currentBrandVariantNode.addChildren(new SKUNode(convertFromMaster(skuProductDTO)));
			}

			// Same description should not appear 2 times in the SKU list
			else {
				logger.info(
						"SKU already exist in production tree, brand = {} , variant = {} , description = {}.",
						new Object[] { skuProductDTO.getBrand(), skuProductDTO.getVariant(),
								skuProductDTO.getDescription() });
			}
		}
	}

	private void addSkuTreeSelectionDomesticModeDepth3(ProductionParameterRootNode root, MarketTypeDTO marketTypeDTO)
			throws ValidatorException {
		// Building the part of the tree for the "Domestic" production mode
		ProductionModeNode nationalProductionModeNode = new AlbaniaProductionModeNode(
				convertFromMarketTypeId(marketTypeDTO.getId()));
		root.addChildren(nationalProductionModeNode);

		// Maps of brands and variants to create the SKU tree
		Map<String, AlbaniaNavigationNode> brands = new HashMap<>();

		// Iterate all SKUs from sorted SKU list from MarketTypeDTO
		for (SkuProductDTO skuProductDTO : AlbaniaRemoteServerUtilities.SkuDtoSorter.sort(marketTypeDTO.getSkuList())) {
			// -------------------------------------
			// Node 1 Brand
			// -------------------------------------
			String currentBrandName = skuProductDTO.getBrand().trim();

			// Checking if the Brand already exist in the tree, if it doesn't
			// then we add it
			AlbaniaNavigationNode currentBrandNode = brands.get(currentBrandName);
			if (currentBrandNode == null) {
				currentBrandNode = new AlbaniaNavigationNode(currentBrandName);
				brands.put(currentBrandName, currentBrandNode);
				nationalProductionModeNode.addChildren(currentBrandNode);
			}

			// -------------------------------------
			// Node 2 Variant
			// -------------------------------------
			String currentVariantName = (skuProductDTO.getVariant() != null ? skuProductDTO.getVariant().trim() : "");

			// Checking if the Variant already exist in the tree, if it doesn't
			// then we add it
			AlbaniaNavigationNode currentVariantNode = (AlbaniaNavigationNode) currentBrandNode
					.getChild(currentVariantName);
			if (currentVariantNode == null) {
				currentVariantNode = new AlbaniaNavigationNode(currentVariantName);
				currentBrandNode.addChildren(currentVariantNode);
			}

			// -------------------------------------
			// Node 3 Description
			// -------------------------------------
			// Checking if the Description already exist in the tree, if it
			// doesn't then we add it
			if (currentVariantNode.getChild(skuProductDTO.getDescription()) == null) {
				currentVariantNode.addChildren(new SKUNode(convertFromMaster(skuProductDTO)));
			}

			// Same description should not appear 2 times in the SKU list
			else {
				logger.info(
						"SKU already exist in production tree, brand = {} , varinat = {} , description = {}.",
						new Object[] { skuProductDTO.getBrand(), skuProductDTO.getVariant(),
								skuProductDTO.getDescription() });
			}
		}
	}

	protected void validateDomesticProduct(Product product) throws ValidatorException {
		// Product not NULL
		validateProductNotNull(product);

		// Product Code not NULL
		validateCodeNotNull(product.getCode());

		// SKU & Batch ID
		validateProductSkuAndBatchIDNotNull(product);

		// Production Mode supported
		validateProductionModeIsSupported(getProductionModeFromProduct(product), productionModeMapping);

		// Production Mode NOT counting
		if (getCountingProductionMode().contains(getProductionModeFromProduct(product))) {
			logger.error("Sending counting production in domestic mode = {}.", product);
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_SENDING_COUNTING_PRODUCTION_IN_DOMESTIC,
					AlbaniaRemoteServerValidator.class.getName(), product);
		}

		// Status supported
		validateProductStatusIsSupported(product.getStatus(), productStatusMapping, true);

		// Product Code Type == SKU Code Type
		validateProductAndSkuCodeTypeMatch(product);
	}

	private void validateCountingProduct(Product product) throws ValidatorException {
		// Production Mode is counting
		if (!getCountingProductionMode().contains(getProductionModeFromProduct(product))) {
			logger.error("Sending domestic production in counting mode = {}.", product);
			throw new ValidatorException(AlbaniaRemoteServerMessages.EXCEPTION_SENDING_DOMESTIC_PRODUCTION_IN_COUNTING,
					AlbaniaRemoteServerValidator.class.getName(), product);
		}

		// Status supported
		AlbaniaRemoteServerValidator.validateProductStatusIsSupported(product.getStatus(), productStatusMapping, false);
	}

	private ProductionMode convertFromMarketTypeId(int marketTypeId) throws ValidatorException {
		validateMarketTyptDTOSupported(marketTypeId);
		return ProductionMode.STANDARD;
	}

	private AlbaniaSKU convertFromMaster(SkuProductDTO skuProductDTO) throws ValidatorException {
		// DTO validation
		validateSkuDTOComplete(skuProductDTO);

		if (skuProductDTO.getProductPackagingsId() == null) {
			skuProductDTO.setProductPackagingsId(ProductPackagings.OTHER.getId());
		}

		AlbaniaSKU sku = new AlbaniaSKU(skuProductDTO.getCustomerSkuId(), convertDescription(skuProductDTO.getBrand(),
				skuProductDTO.getVariant(), skuProductDTO.getDescription()), skuProductDTO.getDescription(),
				skuProductDTO.getBrand(), skuProductDTO.getVariant(), new ArrayList<>(), skuProductDTO.isBlobMode());

		switch (skuProductDTO.getProductPackagingsId()) {

		case 1:
			sku.setProductPackaging(ProductPackagings.BOTTLE);
			break;
		case 2:
			sku.setProductPackaging(ProductPackagings.CAN);
			break;
		default:
			logger.warn("Unexpected Package Type : {}", skuProductDTO.getProductPackagingsId());
			sku.setProductPackaging(ProductPackagings.OTHER);
			break;

		}

		sku.setCodeType(convertFromMaster(skuProductDTO.getCodeType()));

		if (skuProductDTO.getImage() != null) {
			sku.setImage(new ImageIcon(skuProductDTO.getImage()));
			logger.debug("Image for SKU : " + sku + " image: w= " + sku.getImage().getIconWidth() + ", h= "
					+ sku.getImage().getIconHeight());
		} else {
			logger.debug("No image for SKU : " + sku);
		}

		// Adding the skuNode under the correct Brand / Variant in the tree
		return sku;
	}

	private String convertDescription(String brand, String variant, String description) {
		return Messages.format(AlbaniaSKU.SKU, brand, variant, description);
	}

	private CodeType convertFromMaster(CodeTypeDTO codeTypeDTO) throws ValidatorException {
		// DTO validation
		validateCodeTypeDTOComplete(codeTypeDTO);

		CodeType codeType = new CodeType(codeTypeDTO.getId());
		codeType.setDescription(codeTypeDTO.getDescription());
		return codeType;
	}

	private ProductStatuses convertToMaster(ProductStatus clientStatus) throws ValidatorException {
		return productStatusMapping.getValue(clientStatus);
	}

	private ProductionModes convertToMaster(ProductionMode clientMode) {
		return productionModeMapping.getValue(clientMode);
	}

	private ProductDTO convertDomesticProductToDTO(Product product) throws CryptographyException, ValidatorException {
		// Validate product
		validateDomesticProduct(product);

		// If product code is not decoded, decode the code (for
		// SENT_TO_PRINTER_WASTED)
		if (!AlbaniaAuthenticatorUtilities.isProductCodeDecoded(product.getCode())) {
			AlbaniaAuthenticatorUtilities.populate(product.getCode(), authenticator);
		}

		// DTO to be return
		ProductDTO productDTO = new ProductDTO();
		productDTO.setCodeTypeId((int) product.getCode().getCodeType().getId());
		productDTO.setCodeSequence(product.getCode().getSequence());
		productDTO.setCodeBatchId(product.getCode().getEncoderId());
		productDTO.setStatus(convertToMaster(product.getStatus()));
		productDTO.setActivationDate(product.getActivationDate());

		return productDTO;
	}

	private List<ProductDTO> convertDomesticProductToDTOList(PackagedProducts packagedProducts, int subSystemId)
			throws CryptographyException, ValidatorException {
		List<ProductDTO> productDTOs = new ArrayList<>();

		// Convert all product from PackagedProducts
		for (Product product : packagedProducts.getProducts()) {
			restoreProductParam(product, packagedProducts, subSystemId);
			if (ProductStatus.TYPE_MISMATCH.equals(product.getStatus())) {
				logger.debug("Domestic Product type mismatch :" + product);
			} else {
				// Add converted product
				productDTOs.add(convertDomesticProductToDTO(product));
			}
		}
		return productDTOs;
	}

	public void setTreeDepth(int depth) {
		treeDepth = depth;
	}

	private int getTreeDepth() {
		return treeDepth;
	}

	public IAuthenticator getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(IAuthenticator authenticator) {
		this.authenticator = authenticator;
	}

}
