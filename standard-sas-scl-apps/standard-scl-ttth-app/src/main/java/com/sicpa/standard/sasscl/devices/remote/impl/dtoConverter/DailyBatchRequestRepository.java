package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.gssd.ttth.server.common.dto.DailyBatchRequestDto;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.statistics.DailyBatchJobStatistics;
import com.sicpa.std.common.api.staticdata.sku.dto.SkuProductDto;

public class DailyBatchRequestRepository {

    private List<DailyBatchRequestDto> dailyBatchRequests;
    private List<SKU> skuList;

    private DailyBatchJobStatistics batchJobStatistics;

    private int productsCount;

    public DailyBatchRequestRepository() {
        productsCount = 0;
        dailyBatchRequests = new ArrayList<>();
        skuList = new ArrayList<>();
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

    public void addSKU(SKU sku) {
        this.skuList.add(sku);
    }

    public void clearDailyBatchRequest() {
        dailyBatchRequests.clear();
    }

    public void clearSkuList() {
        skuList.clear();
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
        return skuList
            .stream()
            .filter(e -> e.getId() == skuProductDto.getId())
            .findAny()
            .get();
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
