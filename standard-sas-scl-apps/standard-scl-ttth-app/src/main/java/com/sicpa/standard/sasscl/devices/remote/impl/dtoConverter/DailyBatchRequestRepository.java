package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sicpa.gssd.ttth.server.common.dto.DailyBatchRequestDto;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.statistics.DailyBatchJobStatistics;
import com.sicpa.std.common.api.staticdata.sku.dto.SkuProductDto;

public class DailyBatchRequestRepository {

    private List<DailyBatchRequestDto> dailyBatchRequests;
    private CodeType codeType;

    private DailyBatchJobStatistics batchJobStatistics;

    private int productsCount;

    public DailyBatchRequestRepository() {
        productsCount = 0;
        dailyBatchRequests = new ArrayList<>();
    }

    public void updateStatistics(int value) {
        batchJobStatistics
            .setProductCount(batchJobStatistics.getProductCount() + (value - productsCount));
        productsCount = value;
    }

    public void updateStatistics(String batchJobId, int quantity) {
        batchJobStatistics.setBatchJobId(batchJobId);
        batchJobStatistics.setBatchQuantity(quantity);
        batchJobStatistics.setProductCount(0);
        productsCount = 0;
    }

    public void addDailyBatchRequest(DailyBatchRequestDto dailyBatchRequest) {
        this.dailyBatchRequests.add(dailyBatchRequest);
    }

    public void clearDailyBatchRequest() {
        dailyBatchRequests.clear();
    }

    public void setCodeType (CodeType codeType) {
        this.codeType = codeType;
    }

    public List<DailyBatchRequestDto> getDailyBatchRequests() {
        return dailyBatchRequests;
    }

    public SKU getDailyBatchSKU(String batchJobId) {
        //Safe to do as the filter is only done post selection.
        DailyBatchRequestDto batchRequestDto = this.dailyBatchRequests
            .stream()
            .filter(e -> e.getBatchJobId().equals(batchJobId))
            .findAny()
            .get();
        //Update statistics.
        updateStatistics(batchRequestDto.getBatchJobId(), batchRequestDto.getQuantity().intValue());

        //Generate SKU.
        SkuProductDto skuProductDto = batchRequestDto.getSkuProductDto();
        SKU sku = new SKU((skuProductDto.getId().intValue()), skuProductDto.getInternalDescription(),
            Collections.singletonList(skuProductDto.getSkuBarcode()));
        //TODO:: Implement Assignment of SKU image.
        sku.setCodeType(codeType);

        return sku;
    }

    public DailyBatchJobStatistics getBatchJobStatistics() {
        return batchJobStatistics;
    }

    public void setBatchJobStatistics(DailyBatchJobStatistics batchJobStatistics) {
        this.batchJobStatistics = batchJobStatistics;
    }

    public void setProductsCount(int productsCount) {
        this.productsCount = productsCount;
    }
}
