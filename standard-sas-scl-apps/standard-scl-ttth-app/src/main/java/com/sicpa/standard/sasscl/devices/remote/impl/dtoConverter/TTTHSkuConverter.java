package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import java.util.Arrays;
import javax.swing.ImageIcon;

import com.sicpa.gssd.ttth.server.common.dto.DailyBatchRequestDto;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import com.sicpa.std.common.api.base.dto.BaseDto;
import com.sicpa.std.common.api.base.dto.ComponentBehaviorDto;
import com.sicpa.std.common.api.staticdata.sku.dto.SkuProductDto;

public class TTTHSkuConverter extends SkuConverter {

    private DailyBatchRequestRepository dailyBatchRequestRepository;

    protected void convert(AbstractProductionParametersNode<?> convertedParentRoot,
                           ComponentBehaviorDto<? extends BaseDto<Long>> child) {
        super.convert(convertedParentRoot, child);
        if (child.getNodeValue() instanceof DailyBatchRequestDto) {
            convertDailyBatchRequestDto(child);
        }
    }

    @Override
    protected void convertSkuProductDto(ComponentBehaviorDto<? extends BaseDto<Long>> child,
                                        final AbstractProductionParametersNode<?> convertedParentRoot) {
        SkuProductDto skuDto = (SkuProductDto) child.getNodeValue();
        SKU sku = new SKU(skuDto.getId().intValue(), skuDto.getInternalDescription(), Arrays.asList(skuDto
            .getSkuBarcode()));

        if (skuDto.getIcon() != null && skuDto.getIcon().length > 0) {
            sku.setImage(new ImageIcon(skuDto.getIcon()));
        }

        CodeType codeType = this.getCodeTypeForSku(child);

        // skip if fail to get code type
        if (codeType == null) {
            return;
        }

        sku.setCodeType(this.getCodeTypeForSku(child));
        dailyBatchRequestRepository.addSKU(sku);

        SKUNode skuConverted = new SKUNode(sku);
        convertedParentRoot.addChildren(skuConverted);
        convertDMSProductionParameter(child, skuConverted);
    }

    private void convertDailyBatchRequestDto(ComponentBehaviorDto<? extends BaseDto<Long>> child) {
        DailyBatchRequestDto dailyBatchRequestDto = (DailyBatchRequestDto) child.getNodeValue();
        //Save daily batch jobs to daily batch request manager
        dailyBatchRequestRepository.addDailyBatchRequest(dailyBatchRequestDto);
    }

    public void setDailyBatchRequestRepository(DailyBatchRequestRepository dailyBatchRequestRepository) {
        this.dailyBatchRequestRepository = dailyBatchRequestRepository;
    }
}

