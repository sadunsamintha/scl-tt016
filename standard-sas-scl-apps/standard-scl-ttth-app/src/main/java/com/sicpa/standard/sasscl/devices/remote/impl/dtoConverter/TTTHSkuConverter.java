package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import com.sicpa.gssd.ttth.server.common.dto.DailyBatchRequestDto;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.CodeTypeNode;
import com.sicpa.std.common.api.base.dto.BaseDto;
import com.sicpa.std.common.api.base.dto.ComponentBehaviorDto;
import com.sicpa.std.common.api.staticdata.codetype.dto.CodeTypeDto;

public class TTTHSkuConverter extends SkuConverter {

    protected void convert(AbstractProductionParametersNode<?> convertedParentRoot,
                           ComponentBehaviorDto<? extends BaseDto<Long>> child) {
        super.convert(convertedParentRoot, child);
        if (child.getNodeValue() instanceof DailyBatchRequestDto) {
            convertDailyBatchRequestDto(child);
        }
    }

    private void convertDailyBatchRequestDto(ComponentBehaviorDto<? extends BaseDto<Long>> child) {
        DailyBatchRequestDto dailyBatchRequestDto = (DailyBatchRequestDto) child.getNodeValue();
        //Save daily batch jobs to daily batch request manager
        EventBusService.post(dailyBatchRequestDto);
    }

    @Override
    protected void convertCodeTypeDto(ComponentBehaviorDto<? extends BaseDto<Long>> child,
                                      final AbstractProductionParametersNode<?> convertedParentRoot) {
        CodeTypeDto codeDto = (CodeTypeDto) child.getNodeValue();
        CodeType codeType = new CodeType(codeDto.getId().intValue());
        codeType.setDescription(codeDto.getInternalDescription());

        //Save code type to daily batch request manager.
        EventBusService.post(codeType);

        CodeTypeNode codeTypeConverted = new CodeTypeNode(codeType);
        convertedParentRoot.addChildren(codeTypeConverted);
        convertDMSProductionParameter(child, codeTypeConverted);
    }

}

