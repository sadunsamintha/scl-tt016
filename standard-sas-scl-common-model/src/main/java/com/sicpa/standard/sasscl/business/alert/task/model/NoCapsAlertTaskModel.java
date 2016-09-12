package com.sicpa.standard.sasscl.business.alert.task.model;

public class NoCapsAlertTaskModel extends AbstractAlertTaskModel {

    private int delayInSec;
    private int threshold;
    private int sampleSize;


    public int getDelayInSec() {
        return delayInSec;
    }

    public void setDelayInSec(int delayInSec) {
        this.delayInSec = delayInSec;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }
}