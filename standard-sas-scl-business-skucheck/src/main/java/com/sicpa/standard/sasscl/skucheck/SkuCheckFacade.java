package com.sicpa.standard.sasscl.skucheck;

import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.SkuCode;
import com.sicpa.standard.sasscl.skucheck.acquisition.Acquisition;
import com.sicpa.standard.sasscl.skucheck.acquisition.GroupAcquisitionType;
import com.sicpa.standard.sasscl.skucheck.acquisition.IAcquisitionListener;
import com.sicpa.standard.sasscl.skucheck.acquisition.SingleAcquisitionType;
import com.sicpa.standard.sasscl.skucheck.acquisition.statistics.IAcquisitionStatistics;
import com.sicpa.standard.sasscl.skucheck.buffer.ISkuBuffer;
import com.sicpa.standard.sasscl.skucheck.checking.ICheckingResultListener;
import com.sicpa.standard.sasscl.skucheck.checking.trigger.AbstractSkuCheckingTrigger;
import com.sicpa.standard.sasscl.skucheck.filter.ISkuFilter;
import com.sicpa.standard.sasscl.skucheck.notifier.ISkuNotifier;

import java.util.List;
import java.util.Map;

public class SkuCheckFacade {

    protected SkuCheckerService service;
    protected SkuChecker checker;

    public SkuCheckFacade() {
    }

    public SkuCheckFacade(int devicesCount, final SkuChecker checker, final AbstractSkuCheckingTrigger trigger,
                          final IAcquisitionStatistics stats, ISkuBuffer buffer, ISkuFilter filter, ISkuNotifier<SKU> notifier) {

        this.checker = checker;

        service =	new SkuCheckerService.Builder(checker).acquisition(new Acquisition(devicesCount)).trigger(trigger)
                .statistics(stats).skuBuffer(buffer).filter(filter).notifier(notifier).build();
    }

    public void addSku(int deviceIndex, SkuCode sku) {
        service.addSku(deviceIndex, sku);
    }

    public void addUnread(int deviceIndex) {
        service.addUnread(deviceIndex);
    }

    public void addUnread() {
        service.addUnread();
    }

    public void addSku(SkuCode sku) {
        service.addSku(sku);
    }

    public void querySkus() {
        service.querySkus();
    }

    public void addCheckingListener(ICheckingResultListener lis) {
        service.addCheckingListener(lis);
    }

    public Map<SingleAcquisitionType, Integer> getStatistics(int deviceIndex) {
        return service.getStatistics(deviceIndex);
    }

    public Map<GroupAcquisitionType, Integer> getStatistics() {
        return service.getStatistics();
    }

    public int getAcquisitionGroupCount() {
        return service.getAcquisitionGroupCount();
    }

    public void addAcquisitionListener(IAcquisitionListener lis) {
        service.addAcquisitionListener(lis);
    }

    public void startTriggering() {
        service.startTriggering();
    }

    public void stopTriggering() {
        service.stopTriggering();
    }

    public void addOfflineProduction(int count) {

        service.addOfflineProduction(count);
    }

    public List<SKU> getKnownSkus() {
        return checker.getKnownSkus();
    }
}
