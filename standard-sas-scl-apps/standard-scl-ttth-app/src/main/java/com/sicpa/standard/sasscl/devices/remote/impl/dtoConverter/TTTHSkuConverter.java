package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import com.sicpa.gssd.ttth.server.common.dto.DailyBatchRequestDto;
import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;
import com.sicpa.std.common.api.base.dto.BaseDto;
import com.sicpa.std.common.api.base.dto.ComponentBehaviorDto;

public class TTTHSkuConverter extends SkuConverter {

    private DailyBatchRequestRepository dailyBatchRequestRepository;

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
        dailyBatchRequestRepository.addDailyBatchRequest(dailyBatchRequestDto);
    }

    public void setDailyBatchRequestRepository(DailyBatchRequestRepository dailyBatchRequestRepository) {
        this.dailyBatchRequestRepository = dailyBatchRequestRepository;
    }
}

