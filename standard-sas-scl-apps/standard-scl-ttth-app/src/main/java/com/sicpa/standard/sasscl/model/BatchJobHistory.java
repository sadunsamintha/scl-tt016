package com.sicpa.standard.sasscl.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.ttth.scl.utils.TTTHDailyBatchJobUtils;

public class BatchJobHistory implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(BatchJobHistory.class);

    private static final long serialVersionUID = 1L;

    private Map<String, Date> dailyBatchHistory;

    public BatchJobHistory() {
        dailyBatchHistory = new HashMap<>();
    }

    public void addDailyBatchHistory(String batchJobId, Date batchJobStopDate) {
        if (!dailyBatchHistory.containsKey(batchJobId)) {
            this.dailyBatchHistory.put(batchJobId, batchJobStopDate);
        }
    }

    public void sortHistory() {
        dailyBatchHistory = dailyBatchHistory
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new
            ));
    }

    public void clearOldBatchJobs() {
        dailyBatchHistory.entrySet().removeIf(e -> {
            try {
                return TTTHDailyBatchJobUtils.isBatchJobHistoryDated(e.getValue());
            } catch (IOException ex) {
                logger.error("Failed to validate batch job history", e);
                return false;
            }
        });
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + dailyBatchHistory.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BatchJobHistory other = (BatchJobHistory) obj;
        return dailyBatchHistory.equals(other.dailyBatchHistory);
    }

    public Map<String, Date> getDailyBatchHistory() {
        return dailyBatchHistory;
    }

    public void setDailyBatchHistory(Map<String, Date> dailyBatchHistory) {
        this.dailyBatchHistory = dailyBatchHistory;
    }
}
