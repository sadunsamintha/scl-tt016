package com.sicpa.tt016.scl.remote.assembler;

import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import com.sicpa.tt016.common.dto.SkuDTO;
import com.sicpa.tt016.scl.model.MoroccoSKU;

import static com.sicpa.standard.gui.utils.ImageUtils.convertToBufferedImage;

public class SkuConverter {
    private static final Logger logger = LoggerFactory.getLogger(SkuConverter.class);

    private long codeTypeId;
    private boolean refeedAvailable;
    private boolean heightAvailable;
    private  String 	productionNormal;
    private  String 	productionRefeedNormal;


    public ProductionParameterRootNode convert(List<SkuDTO> dtos) {

        ProductionParameterRootNode root = new ProductionParameterRootNode();

        ProductionModeNode domesticMode = new ProductionModeNode(ProductionMode.STANDARD);
        ProductionModeNode refeedMode = new ProductionModeNode(ProductionMode.REFEED_NORMAL);
        ProductionModeNode exportMode = new ProductionModeNode(ProductionMode.EXPORT);
        ProductionModeNode maintenanceMode = new ProductionModeNode(ProductionMode.MAINTENANCE);

       	productionNormal = Messages.messages.get("language/sasscl").getString("productionmode.standard");
        productionRefeedNormal = Messages.messages.get("language/sasscl").getString("productionmode.refeed.normal");
        
        for (SkuDTO dto : dtos) {
            SKU sku = convert(dto);
            SKUNode node = new SKUNode(sku);
            SKUNode nodeRefeed;
                  
            if (dto.isLocalMarket()) {
                domesticMode.addChildren(node);
                if(refeedAvailable){
                	SKU skuRefeed = convert(dto);
                	skuRefeed.setDescription(skuRefeed.getDescription().replace("- "+productionNormal, "- "+productionRefeedNormal));
                	nodeRefeed = new SKUNode(skuRefeed);
                	refeedMode.addChildren(nodeRefeed);
                }
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
        SKU sku;
        if (heightAvailable) {
            sku = new MoroccoSKU();
            try {
                ((MoroccoSKU)sku).setProductHeight(dto.getHeight());
            } catch (Exception e) {
                logger.error("Unable to retrieve product height from SKU. ", e);
            }
        } else {
            sku = new SKU();
        }
        sku.setAppearanceCode(createPhysicalId(dto));
        sku.setId(dto.getSkuId());
        sku.addBarcode(dto.getBarcode());

        if(dto.getIcon() != null) {
        	BufferedImage image = convertToBufferedImage(dto.getIcon());
        	sku.setImage(new ImageIcon(image));
        }
        
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

    public void setHeightAvailable(boolean heightAvailable) {
        this.heightAvailable = heightAvailable;
    }

    public void setProductionNormal(String productionNormal) {
		this.productionNormal = productionNormal;
	}

	public void setProductionRefeedNormal(String productionRefeedNormal) {
		this.productionRefeedNormal = productionRefeedNormal;
	}
  
    
}
