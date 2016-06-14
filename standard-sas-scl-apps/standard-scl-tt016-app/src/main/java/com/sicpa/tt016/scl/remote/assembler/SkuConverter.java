package com.sicpa.tt016.scl.remote.assembler;

import static com.sicpa.standard.gui.utils.ImageUtils.convertToBufferedImage;
import static com.sicpa.tt016.scl.model.TT016Sku.SKU_PHYSICAL_PROPERTY;

import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import com.sicpa.tt016.common.dto.SkuDTO;

public class SkuConverter {
	private static final Logger logger = LoggerFactory.getLogger(SkuConverter.class);

	private long codeTypeId;

	public ProductionParameterRootNode convert(List<SkuDTO> dtos) {

		ProductionParameterRootNode root = new ProductionParameterRootNode();

		ProductionModeNode domesticMode = new ProductionModeNode(ProductionMode.STANDARD);
		ProductionModeNode refeedMode = new ProductionModeNode(ProductionMode.REFEED_NORMAL);
		ProductionModeNode exportMode = new ProductionModeNode(ProductionMode.EXPORT);
		ProductionModeNode maintenanceMode = new ProductionModeNode(ProductionMode.MAINTENANCE);

		for (SkuDTO dto : dtos) {
			SKU sku = convert(dto);
			SKUNode node = new SKUNode(sku);
			if (dto.isLocalMarket()) {
				domesticMode.addChildren(node);
				refeedMode.addChildren(node);
			} else if (dto.isExportMarket()) {
				exportMode.addChildren(node);
			} else {
				logger.error("unsupported market type id:" + dto.getMarketType());
			}
		}

		root.addChildren(domesticMode, exportMode, maintenanceMode);
		return root;
	}

	private SKU convert(SkuDTO dto) {
		SKU sku = new SKU();
		sku.setProperty(SKU_PHYSICAL_PROPERTY, createPhysicalId(dto));
		sku.setId(dto.getSkuId());
		sku.addBarcode(dto.getBarcode());

		BufferedImage image = convertToBufferedImage(dto.getIcon());
		sku.setImage(new ImageIcon(image));

		sku.setCodeType(new CodeType(codeTypeId));
		sku.setDescription(dto.getDescription());
		return sku;
	}

	private String createPhysicalId(SkuDTO sku) {
		return extractSkuDescription(sku) + "-" + +sku.getCategoryId() + "-" + "-" + sku.getPackageVolumeId();
	}

	private String extractSkuDescription(SkuDTO sku) {
		// brand-variant-packagingType-marketType
		String desc = sku.getDescription();
		int index = desc.lastIndexOf('-');
		return desc.substring(0, index);
	}

	public void setCodeTypeId(long codeTypeId) {
		this.codeTypeId = codeTypeId;
	}
}
