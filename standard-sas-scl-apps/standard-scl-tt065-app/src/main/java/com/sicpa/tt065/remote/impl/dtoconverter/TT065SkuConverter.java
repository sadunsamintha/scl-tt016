package com.sicpa.tt065.remote.impl.dtoconverter;

import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.SkuConverter;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import com.sicpa.std.common.api.base.dto.BaseDto;
import com.sicpa.std.common.api.base.dto.ComponentBehaviorDto;
import com.sicpa.std.common.api.sku.dto.MarketTypeDto;
import com.sicpa.std.common.api.staticdata.sku.dto.SkuProductDto;
import com.sicpa.tt065.model.TT065CustomProperties;
import com.sicpa.tt065.model.TT065ProductionMode;

import javax.swing.*;
import java.util.Arrays;

import static com.sicpa.standard.sasscl.model.ProductionMode.REFEED_CORRECTION;
import static com.sicpa.standard.sasscl.model.ProductionMode.REFEED_NORMAL;

public class TT065SkuConverter extends SkuConverter {

    @Override
    protected void convertSkuProductDto(ComponentBehaviorDto<? extends BaseDto<Long>> child,
                                        AbstractProductionParametersNode<?> convertedParentRoot) {
        SkuProductDto skuDto = (SkuProductDto) child.getNodeValue();
        SKU sku = new SKU(skuDto.getId().intValue(), skuDto.getInternalDescription(), Arrays.asList(skuDto
                .getSkuBarcode()));
        sku.setProperty(TT065CustomProperties.skuCompliant, skuDto.isCompliant());

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

    @Override
    protected void convertMarketTypeDto(ComponentBehaviorDto<? extends BaseDto<Long>> child, AbstractProductionParametersNode<?> convertedParentRoot) {
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
            copyTree(productionModeConverted, new ProductionModeNode(TT065ProductionMode.REFEED_STOCK), convertedParentRoot);
        }
    }
}
