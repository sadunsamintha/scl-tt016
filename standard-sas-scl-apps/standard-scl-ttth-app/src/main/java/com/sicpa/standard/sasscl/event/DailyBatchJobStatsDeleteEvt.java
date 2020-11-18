package com.sicpa.standard.sasscl.event;

import java.util.List;

public class DailyBatchJobStatsDeleteEvt {

    private List<String> dailyBatchJobs;

    public DailyBatchJobStatsDeleteEvt(List<String> dailyBatchJobs) {
        this.dailyBatchJobs = dailyBatchJobs;
    }

    public List<String> getDailyBatchJobs() {
        return dailyBatchJobs;
    }
}
