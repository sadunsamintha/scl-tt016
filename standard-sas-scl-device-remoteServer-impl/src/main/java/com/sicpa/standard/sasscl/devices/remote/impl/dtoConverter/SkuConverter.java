package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import static com.sicpa.standard.sasscl.model.ProductionMode.REFEED_CORRECTION;
import static com.sicpa.standard.sasscl.model.ProductionMode.REFEED_NORMAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.remote.mapping.IProductionModeMapping;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.CodeTypeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.NavigationNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import com.sicpa.std.common.api.activation.dto.AuthorizedProductsDto;
import com.sicpa.std.common.api.base.dto.BaseDto;
import com.sicpa.std.common.api.base.dto.ComponentBehaviorDto;
import com.sicpa.std.common.api.sku.dto.MarketTypeDto;
import com.sicpa.std.common.api.sku.dto.PackagingTypeSkuDto;
import com.sicpa.std.common.api.staticdata.codetype.dto.CodeTypeDto;
import com.sicpa.std.common.api.staticdata.sku.dto.SkuProductDto;

public class SkuConverter implements ISkuConverter {

	private static final Logger logger = LoggerFactory.getLogger(SkuConverter.class);

	private IProductionModeMapping productionModeMapping;

	@Override
	public ProductionParameterRootNode convert(AuthorizedProductsDto products) {
		ProductionParameterRootNode root = convertDMSProductionParameter(products);
		pruneParametersTree(root);
		ProductionModeNode maintenanceNode = new ProductionModeNode(ProductionMode.MAINTENANCE);
		root.addChildren(maintenanceNode);
		return root;
	}

	@SuppressWarnings("unchecked")
	public ProductionParameterRootNode convertDMSProductionParameter(final AuthorizedProductsDto parentDTO) {
		ProductionParameterRootNode convertedRoot = new ProductionParameterRootNode();
		convertDMSProductionParameter(parentDTO, convertedRoot);
		return convertedRoot;
	}

	/**
	 *
	 * expected structure :
	 *
	 * <code>
	 * 
	 * MarketTypeDto
	 * |
	 * |- CodeTypeDto
	 * |
	 * |- SKUTypeDto
	 * 
	 * </code>
	 *
	 * @param parentDTO
	 * @param convertedParentRoot
	 */
	private void convertDMSProductionParameter(final ComponentBehaviorDto<? extends BaseDto<Long>> parentDTO,
			final AbstractProductionParametersNode<?> convertedParentRoot) {

		if (parentDTO.getChildren() == null)
			return;

		for (ComponentBehaviorDto<? extends BaseDto<Long>> child : parentDTO.getChildren())
			convert(convertedParentRoot, child);
	}

	private void convert(AbstractProductionParametersNode<?> convertedParentRoot,
			ComponentBehaviorDto<? extends BaseDto<Long>> child) {

		if (child.getNodeValue() instanceof MarketTypeDto) {
			convertMarketTypeDto(child, convertedParentRoot);
			return;
		}
		if (child.getNodeValue() instanceof SkuProductDto) {
			convertSkuProductDto(child, convertedParentRoot);
			return;
		}
		if (child.getNodeValue() instanceof CodeTypeDto) {
			convertCodeTypeDto(child, convertedParentRoot);
			return;
		}
		if (child.getNodeValue() instanceof PackagingTypeSkuDto) {
			convertPackagingTypeSkuDto(child, convertedParentRoot);
			return;
		}
		convertNavigationDto(child, convertedParentRoot);
	}

	private void convertPackagingTypeSkuDto(ComponentBehaviorDto<? extends BaseDto<Long>> child,
			final AbstractProductionParametersNode<?> convertedParentRoot) {
		PackagingTypeSkuDto packagingTypeSkuDto = (PackagingTypeSkuDto) child.getNodeValue();

		// For Packaging Type we need to get the business code, not the ID
		NavigationNode navigationNode = new NavigationNode(packagingTypeSkuDto.getDescription());
		convertedParentRoot.addChildren(navigationNode);
		convertDMSProductionParameter(child, navigationNode);
	}

	private void convertNavigationDto(ComponentBehaviorDto<? extends BaseDto<Long>> child,
			final AbstractProductionParametersNode<?> convertedParentRoot) {
		// navigation node only
		NavigationNode navigationNode = new NavigationNode(child.getNodeValue().getId() + "");
		convertedParentRoot.addChildren(navigationNode);
		convertDMSProductionParameter(child, navigationNode);
	}

	private void convertCodeTypeDto(ComponentBehaviorDto<? extends BaseDto<Long>> child,
			final AbstractProductionParametersNode<?> convertedParentRoot) {
		CodeTypeDto codeDto = (CodeTypeDto) child.getNodeValue();
		CodeType codeType = new CodeType(codeDto.getId().intValue());
		codeType.setDescription(codeDto.getInternalDescription());
		CodeTypeNode codeTypeConverted = new CodeTypeNode(codeType);
		convertedParentRoot.addChildren(codeTypeConverted);
		convertDMSProductionParameter(child, codeTypeConverted);
	}

	private void convertSkuProductDto(ComponentBehaviorDto<? extends BaseDto<Long>> child,
			final AbstractProductionParametersNode<?> convertedParentRoot) {
		SkuProductDto skuDto = (SkuProductDto) child.getNodeValue();
		SKU sku = new SKU(skuDto.getId().intValue(), skuDto.getInternalDescription(), Arrays.asList(skuDto
				.getSkuBarcode()));
		if (skuDto.getIcon() != null) {
			sku.setImage(new ImageIcon(skuDto.getIcon()));
		}
		CodeType codeType = this.getCodeTypeForSku(child);

		// skip if fail to get code type
		if (codeType == null) {
			return;
		}

		sku.setCodeType(this.getCodeTypeForSku(child));
		SKUNode skuConverted = new SKUNode(sku);
		convertedParentRoot.addChildren(skuConverted);
		convertDMSProductionParameter(child, skuConverted);
	}

	private void convertMarketTypeDto(ComponentBehaviorDto<? extends BaseDto<Long>> child,
			final AbstractProductionParametersNode<?> convertedParentRoot) {

		MarketTypeDto marketDto = (MarketTypeDto) child.getNodeValue();
		ProductionMode productionMode = productionModeMapping.getProductionModeFromRemoteId(marketDto.getId()
				.intValue());
		if (productionMode == null) {

			logger.error("no production mode for {}", marketDto.toString());
			return;
		}
		ProductionModeNode productionModeConverted = new ProductionModeNode(productionMode);
		convertedParentRoot.addChildren(productionModeConverted);
		convertDMSProductionParameter(child, productionModeConverted);

		if (ProductionMode.STANDARD.equals(productionMode)) {
			// if standard mode duplicate the tree for refeed
			copyTree(productionModeConverted, new ProductionModeNode(REFEED_NORMAL), convertedParentRoot);
			copyTree(productionModeConverted, new ProductionModeNode(REFEED_CORRECTION), convertedParentRoot);
		}
	}

	private void copyTree(ProductionModeNode from, ProductionModeNode to,
			final AbstractProductionParametersNode<?> convertedParentRoot) {
		convertedParentRoot.addChildren(to);
		to.addChildren(from.getChildren());
	}

	private CodeType getCodeTypeForSku(final ComponentBehaviorDto<? extends BaseDto<Long>> skuDto) {

		if (skuDto == null)
			return null;

		if (skuDto.getParent().getNodeValue() instanceof CodeTypeDto) {

			CodeTypeDto codeTypeDto = (CodeTypeDto) skuDto.getParent().getNodeValue();
			CodeType codeType = new CodeType(codeTypeDto.getId().intValue());
			codeType.setDescription(codeTypeDto.getInternalDescription());
			return codeType;
		}

		return getCodeTypeForSku(skuDto.getParent());
	}

	/**
	 * Trim the branches of the tree that do not contain at least one ProductionModeNode and one SKUNode in the path
	 * starting in the root node.
	 *
	 * @param tree
	 */
	private void pruneParametersTree(ProductionParameterRootNode tree) {
		pruneParametersTree(tree, false, false);
	}

	/**
	 * This method trims the branches of the tree given the root node. It trims branches that don't contain at least one
	 * ProductionModeNode and one SKUNode in the path starting from the given node.
	 *
	 * @param node
	 *            the node to prune.
	 * @param ancestorWithProdMode
	 *            true indicates if the ancestors of the node contains a ProductionModeNode.
	 * @param ancestorWithSKU
	 *            true indicates if the ancestors of the node contains a SKUNode.
	 * @return true if the tree was pruned, false otherwise.
	 */
	private boolean pruneParametersTree(IProductionParametersNode node, boolean ancestorWithProdMode,
			boolean ancestorWithSKU) {
		boolean isProdModeNode = node instanceof ProductionModeNode;
		boolean isSKUNode = node instanceof SKUNode;

		if (ancestorWithProdMode && ancestorWithSKU)
			return false;

		if (isProdModeNode && ancestorWithSKU)
			return false;

		if (isSKUNode && ancestorWithProdMode)
			return false;

		// search for child nodes to trim
		List<IProductionParametersNode> trimmedChildren = new ArrayList<IProductionParametersNode>();

		for (IProductionParametersNode childNode : node.getChildren()) {

			if (pruneParametersTree(childNode, isProdModeNode || ancestorWithProdMode, isSKUNode || ancestorWithSKU))
				trimmedChildren.add(childNode);
		}

		// trim the found nodes
		for (IProductionParametersNode child : trimmedChildren) {
			node.getChildren().remove(child);
		}

		return node.getChildren().isEmpty();
	}

	public void setProductionModeMapping(IProductionModeMapping productionModeMapping) {
		this.productionModeMapping = productionModeMapping;
	}
}
