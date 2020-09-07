package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.gssd.ttth.server.common.dto.DailyBatchRequestDto;
import com.sicpa.standard.sasscl.model.BatchJobHistory;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.statistics.DailyBatchJobStatistics;
import com.sicpa.std.common.api.staticdata.sku.dto.SkuProductDto;

public class DailyBatchRequestRepository {

    private static final Logger logger = LoggerFactory.getLogger(DailyBatchRequestRepository.class);

    private List<DailyBatchRequestDto> dailyBatchRequests;
    private List<SKU> skuList;
    private int productsCount;

    private DailyBatchJobStatistics batchJobStatistics;

    private BatchJobHistory batchJobHistory;

    public DailyBatchRequestRepository() {
        productsCount = 0;
        dailyBatchRequests = new ArrayList<>();
        skuList = new ArrayList<>();
    }

    public void updateStatistics(int value) {
        logger.debug("Updating Statistics for " + batchJobStatistics.getBatchJobId() +
            " products added: " + value);
        batchJobStatistics
            .setProductCount(batchJobStatistics.getProductCount() + (value - productsCount));
        productsCount = value;
    }

    public void updateStatistics(String batchJobId, int quantity) {
        batchJobStatistics.setBatchJobId(batchJobId);
        batchJobStatistics.setBatchQuantity(quantity);
        batchJobStatistics.setProductCount(0);
        productsCount = 0;

        DailyBatchRequestDto batchRequestDto = getDownloadedDailyBatchRequest(batchJobId);
        if (batchRequestDto != null) {
            //Auto mode.
            batchJobStatistics.setBatchJobStartDate(batchRequestDto.getProductionStartDate());
            batchJobStatistics.setBatchJobStopDate(batchRequestDto.getProductionStopDate());
        } else {
            //Offline mode.
            Calendar now = Calendar.getInstance();
            batchJobStatistics.setBatchJobStartDate(now.getTime());
            now.add(Calendar.DATE, 1);
            batchJobStatistics.setBatchJobStopDate(now.getTime());
            //Add history for offline mode entry.
            this.batchJobHistory.addDailyBatchHistory(batchJobId, now.getTime());
        }
    }

    public void addDailyBatchRequest(DailyBatchRequestDto dailyBatchRequest) {
        this.dailyBatchRequests.add(dailyBatchRequest);
        this.batchJobHistory.addDailyBatchHistory(dailyBatchRequest.getBatchJobId(),
            dailyBatchRequest.getProductionStopDate());
    }

    private DailyBatchRequestDto getDownloadedDailyBatchRequest(String batchJobId) {
        try {
            return this.dailyBatchRequests
                .stream()
                .filter(e -> e.getBatchJobId().equals(batchJobId))
                .findAny()
                .get();
        } catch (NoSuchElementException e) {
            return null;
        }

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
        DailyBatchRequestDto batchRequestDto = getDownloadedDailyBatchRequest(batchJobId);
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

    public BatchJobHistory getBatchJobHistory() {
        return batchJobHistory;
    }

    public void setBatchJobHistory(BatchJobHistory batchJobHistory) {
        this.batchJobHistory = batchJobHistory;
    }

    public void setBatchJobStatistics(DailyBatchJobStatistics batchJobStatistics) {
        this.batchJobStatistics = batchJobStatistics;
    }

    public void setProductsCount(int productsCount) {
        this.productsCount = productsCount;
    }
}
