package com.sicpa.tt016.scl.remote.assembler;

import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import com.sicpa.tt016.common.dto.SkuDTO;
import com.sicpa.tt016.provider.impl.TT016RefeedSkuProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static com.sicpa.standard.gui.utils.ImageUtils.convertToBufferedImage;

public class SkuConverter {
    private static final Logger logger = LoggerFactory.getLogger(SkuConverter.class);

    private long codeTypeId;
    private boolean refeedAvailable;
    private TT016RefeedSkuProvider refeedSkuProvider;


    public ProductionParameterRootNode convert(List<SkuDTO> dtos) {

        ProductionParameterRootNode root = new ProductionParameterRootNode();

        ProductionModeNode domesticMode = new ProductionModeNode(ProductionMode.STANDARD);
        ProductionModeNode refeedMode = new ProductionModeNode(ProductionMode.REFEED_NORMAL);
        ProductionModeNode exportMode = new ProductionModeNode(ProductionMode.EXPORT);
        ProductionModeNode maintenanceMode = new ProductionModeNode(ProductionMode.MAINTENANCE);

        for (SkuDTO dto : dtos) {
            SKU sku = convert(dto);
            SKUNode node = new SKUNode(sku);
            if (refeedAvailable && refeedSkuProvider.get().contains(dto.getSkuId())) {
                refeedMode.addChildren(node);
            } else if (dto.isLocalMarket()) {
                domesticMode.addChildren(node);
            } else if (dto.isExportMarket()) {
                exportMode.addChildren(node);
            } else {
                logger.error("unsupported market type id:" + dto.getMarketType());
            }
        }

        if (refeedAvailable && !refeedMode.getChildren().isEmpty()) {
            root.addChildren(domesticMode, exportMode, refeedMode, maintenanceMode);
        } else {
            root.addChildren(domesticMode, exportMode, maintenanceMode);
        }
        return root;
    }

    private SKU convert(SkuDTO dto) {
        SKU sku = new SKU();
        sku.setAppearanceCode(createPhysicalId(dto));
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

    public void setRefeedAvailable(boolean refeedAvailable) {
        this.refeedAvailable = refeedAvailable;
    }

    public void setRefeedSkuProvider(TT016RefeedSkuProvider refeedSkuProvider) {
        this.refeedSkuProvider = refeedSkuProvider;
    }
}
