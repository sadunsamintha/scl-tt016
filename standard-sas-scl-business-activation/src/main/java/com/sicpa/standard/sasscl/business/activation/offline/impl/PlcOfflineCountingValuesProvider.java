package com.sicpa.standard.sasscl.business.activation.offline.impl;

import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

import java.util.Calendar;
import java.util.Date;

import static com.sicpa.standard.plc.value.PlcVariable.createInt32Var;
import static com.sicpa.standard.plc.value.PlcVariable.createShortVar;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.replaceLinePlaceholder;

public class PlcOfflineCountingValuesProvider {

    private PlcProvider plcProvider;

    private String quantityVarName;

    private String lastStopYearVarName;
    private String lastStopMonthVarName;
    private String lastStopDayVarName;
    private String lastStopHourVarName;
    private String lastStopMinuteVarName;
    private String lastStopSecondVarName;

    private String lastProductYearVarName;
    private String lastProductMonthVarName;
    private String lastProductDayVarName;
    private String lastProductHourVarName;
    private String lastProductMinuteVarName;
    private String lastProductSecondVarName;

    public int getQuantityProducts(int lineIndex) throws PlcAdaptorException {
        return plcProvider.get().read(createInt32Var(replaceLinePlaceholder(quantityVarName, lineIndex)));
    }

    public Date getLastStopDateTime(int lineIndex) throws PlcAdaptorException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                readPlcOfflineVar(lastStopYearVarName, lineIndex),
                readPlcOfflineVar(lastStopMonthVarName, lineIndex) - 1,
                readPlcOfflineVar(lastStopDayVarName, lineIndex),
                readPlcOfflineVar(lastStopHourVarName, lineIndex),
                readPlcOfflineVar(lastStopMinuteVarName, lineIndex),
                readPlcOfflineVar(lastStopSecondVarName, lineIndex));

        return calendar.getTime();
    }

    public Date getLastProductDateTime(int lineIndex) throws PlcAdaptorException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                readPlcOfflineVar(lastProductYearVarName, lineIndex),
                readPlcOfflineVar(lastProductMonthVarName, lineIndex) - 1,
                readPlcOfflineVar(lastProductDayVarName, lineIndex),
                readPlcOfflineVar(lastProductHourVarName, lineIndex),
                readPlcOfflineVar(lastProductMinuteVarName, lineIndex),
                readPlcOfflineVar(lastProductSecondVarName, lineIndex));

        return calendar.getTime();
    }

    private short readPlcOfflineVar(String varName, Integer lineIndex) throws PlcAdaptorException {
        return plcProvider.get().read(createShortVar(replaceLinePlaceholder(varName, lineIndex)));
    }

    public void setPlcProvider(PlcProvider plcProvider) {
        this.plcProvider = plcProvider;
    }

    public void setQuantityVarName(String quantityVarName) {
        this.quantityVarName = quantityVarName;
    }

    public void setLastStopYearVarName(String lastStopYearVarName) {
        this.lastStopYearVarName = lastStopYearVarName;
    }

    public void setLastStopMonthVarName(String lastStopMonthVarName) {
        this.lastStopMonthVarName = lastStopMonthVarName;
    }

    public void setLastStopDayVarName(String lastStopDayVarName) {
        this.lastStopDayVarName = lastStopDayVarName;
    }

    public void setLastStopHourVarName(String lastStopHourVarName) {
        this.lastStopHourVarName = lastStopHourVarName;
    }

    public void setLastStopMinuteVarName(String lastStopMinuteVarName) {
        this.lastStopMinuteVarName = lastStopMinuteVarName;
    }

    public void setLastStopSecondVarName(String lastStopSecondVarName) {
        this.lastStopSecondVarName = lastStopSecondVarName;
    }

    public void setLastProductYearVarName(String lastProductYearVarName) {
        this.lastProductYearVarName = lastProductYearVarName;
    }

    public void setLastProductMonthVarName(String lastProductMonthVarName) {
        this.lastProductMonthVarName = lastProductMonthVarName;
    }

    public void setLastProductDayVarName(String lastProductDayVarName) {
        this.lastProductDayVarName = lastProductDayVarName;
    }

    public void setLastProductHourVarName(String lastProductHourVarName) {
        this.lastProductHourVarName = lastProductHourVarName;
    }

    public void setLastProductMinuteVarName(String lastProductMinuteVarName) {
        this.lastProductMinuteVarName = lastProductMinuteVarName;
    }

    public void setLastProductSecondVarName(String lastProductSecondVarName) {
        this.lastProductSecondVarName = lastProductSecondVarName;
    }
}