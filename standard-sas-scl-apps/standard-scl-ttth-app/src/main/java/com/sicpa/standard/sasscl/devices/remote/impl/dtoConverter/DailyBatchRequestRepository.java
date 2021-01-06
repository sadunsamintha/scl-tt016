package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.gssd.ttth.server.common.dto.DailyBatchRequestDto;
import com.sicpa.standard.sasscl.model.BatchJobHistory;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.statistics.DailyBatchJobStatistics;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;

public class DailyBatchRequestRepository {

    private static final Logger logger = LoggerFactory.getLogger(DailyBatchRequestRepository.class);

    private List<DailyBatchRequestDto> dailyBatchRequests;
    private SkuListProvider skuListProvider;
    private int productsCount;

    private DailyBatchJobStatistics batchJobStatistics;

    private BatchJobHistory batchJobHistory;

    public DailyBatchRequestRepository() {
        productsCount = 0;
        dailyBatchRequests = new ArrayList<>();
    }

    public void updateStatistics(int value) {
        logger.debug("Updating Statistics for " + batchJobStatistics.getBatchJobId() +
            " products added: " + value);
        batchJobStatistics
            .setProductCount(batchJobStatistics.getProductCount() + (value - productsCount));
        productsCount = value;
    }

    public void updateStatistics(String batchJobId) {
        batchJobStatistics.setBatchJobId(batchJobId);
        batchJobStatistics.setProductCount(0);
        productsCount = 0;

        DailyBatchRequestDto batchRequestDto = getDownloadedDailyBatchRequest(batchJobId);
        if (batchRequestDto != null) {
            //Auto mode.
            batchJobStatistics.setBatchJobStartDate(batchRequestDto.getProductionStartDate());
            batchJobStatistics.setBatchJobStopDate(batchRequestDto.getProductionStopDate());
            batchJobStatistics.setBatchQuantity(batchRequestDto.getQuantity().intValue());
        } else {
            //Offline mode.
            Calendar now = Calendar.getInstance();
            batchJobStatistics.setBatchJobStartDate(now.getTime());
            now.add(Calendar.DATE, 1);
            batchJobStatistics.setBatchJobStopDate(now.getTime());
            //TODO:: To change upon requirement confirmation for offline mode.
            batchJobStatistics.setBatchQuantity(9999999);
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

    public void clearDailyBatchRequest() {
        dailyBatchRequests.clear();
    }

    public List<DailyBatchRequestDto> getDailyBatchRequests() {
        return dailyBatchRequests;
    }

    public SKU getDailyBatchSKU(String batchJobId) throws NoSuchElementException {
        String skuID = batchJobId.split("-", 6)[4];
        return skuListProvider.getAvailableSKUsForProductionMode(ProductionMode.STANDARD)
            .stream()
            .filter(e -> String.valueOf(e.getId()).equals(skuID))
            .findAny()
            .get();
    }

    public void setSkuListProvider(SkuListProvider skuListProvider) {
        this.skuListProvider = skuListProvider;
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
