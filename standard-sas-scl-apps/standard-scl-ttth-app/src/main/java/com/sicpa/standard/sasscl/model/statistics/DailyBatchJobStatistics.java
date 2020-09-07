package com.sicpa.standard.sasscl.model.statistics;

import java.io.Serializable;
import java.util.Date;

public class DailyBatchJobStatistics implements Serializable {
    private static final long serialVersionUID = 1L;

    private String batchJobId;
    private int batchQuantity;
    private int productCount;
    private Date batchJobStartDate;
    private Date batchJobStopDate;

    public DailyBatchJobStatistics() {
        batchJobId = "";
        batchQuantity = 0;
        productCount = 0;
        batchJobStartDate = new Date();
        batchJobStopDate = new Date();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + batchJobId.hashCode();
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
        DailyBatchJobStatistics other = (DailyBatchJobStatistics) obj;
        return batchJobId.equals(other.batchJobId);
    }

    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
    }

    public void setBatchQuantity(int batchQuantity) {
        this.batchQuantity = batchQuantity;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public void setBatchJobStartDate(Date batchJobStartDate) {
        this.batchJobStartDate = batchJobStartDate;
    }

    public void setBatchJobStopDate(Date batchJobStopDate) {
        this.batchJobStopDate = batchJobStopDate;
    }

    public String getBatchJobId() {
        return batchJobId;
    }

    public int getBatchQuantity() {
        return batchQuantity;
    }

    public int getProductCount() {
        return productCount;
    }

    public Date getBatchJobStartDate() {
        return batchJobStartDate;
    }

    public Date getBatchJobStopDate() {
        return batchJobStopDate;
    }
}
