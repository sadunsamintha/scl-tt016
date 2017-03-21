package com.sicpa.standard.sasscl.devices.plc;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

public class PlcUpdateDateTimeSender {

    private static final Logger logger = LoggerFactory.getLogger(PlcUpdateDateTimeSender.class);

    private PlcProvider plcProvider;

    private String updateDateTimeYearVarName;
    private String updateDateTimeMonthVarName;
    private String updateDateTimeDayOfWeekVarName;
    private String updateDateTimeDayVarName;
    private String updateDateTimeYHourVarName;
    private String updateDateTimeMinuteVarName;
    private String updateDateTimeSecondVarName;
    private String updateDateTimeMillisecondsVarName;


    public void sendUpdateDateTime() {
        IPlcAdaptor plcAdaptor = plcProvider.get();

        IPlcVariable<Short> updateDateTimeYearVar = PlcVariable.createShortVar(updateDateTimeYearVarName);
        IPlcVariable<Short> updateDateTimeMonthVar = PlcVariable.createShortVar(updateDateTimeMonthVarName);
        IPlcVariable<Short> updateDateTimeDayOfWeekVar = PlcVariable.createShortVar(updateDateTimeDayOfWeekVarName);
        IPlcVariable<Short> updateDateTimeDayVar = PlcVariable.createShortVar(updateDateTimeDayVarName);
        IPlcVariable<Short> updateDateTimeHourVar = PlcVariable.createShortVar(updateDateTimeYHourVarName);
        IPlcVariable<Short> updateDateTimeMinuteVar = PlcVariable.createShortVar(updateDateTimeMinuteVarName);
        IPlcVariable<Short> updateDateTimeSecondVar = PlcVariable.createShortVar(updateDateTimeSecondVarName);
        IPlcVariable<Short> updateDateTimeMillisecondsVar = PlcVariable.createShortVar(updateDateTimeMillisecondsVarName);

        LocalDateTime now = LocalDateTime.now();
        updateDateTimeYearVar.setValue((short) now.getYear());
        updateDateTimeMonthVar.setValue((short) now.getMonthValue());
        updateDateTimeDayOfWeekVar.setValue((short) now.getDayOfWeek().getValue());
        updateDateTimeDayVar.setValue((short) now.getDayOfMonth());
        updateDateTimeHourVar.setValue((short) now.getHour());
        updateDateTimeMinuteVar.setValue((short) now.getMinute());
        updateDateTimeSecondVar.setValue((short) now.getSecond());
        updateDateTimeMillisecondsVar.setValue((short) now.get(ChronoField.MILLI_OF_SECOND));

        try {
            plcAdaptor.write(updateDateTimeYearVar);
            plcAdaptor.write(updateDateTimeMonthVar);
            plcAdaptor.write(updateDateTimeDayOfWeekVar);
            plcAdaptor.write(updateDateTimeDayVar);
            plcAdaptor.write(updateDateTimeHourVar);
            plcAdaptor.write(updateDateTimeMinuteVar);
            plcAdaptor.write(updateDateTimeSecondVar);
            plcAdaptor.write(updateDateTimeMillisecondsVar);
        } catch (PlcAdaptorException e) {
            logger.error("Error sending update date time to PLC", e);
        }
    }

    public void setPlcProvider(PlcProvider plcProvider) {
        this.plcProvider = plcProvider;
    }

    public void setUpdateDateTimeYearVarName(String updateDateTimeYearVarName) {
        this.updateDateTimeYearVarName = updateDateTimeYearVarName;
    }

    public void setUpdateDateTimeMonthVarName(String updateDateTimeMonthVarName) {
        this.updateDateTimeMonthVarName = updateDateTimeMonthVarName;
    }

    public void setUpdateDateTimeDayOfWeekVarName(String updateDateTimeDayOfWeekVarName) {
        this.updateDateTimeDayOfWeekVarName = updateDateTimeDayOfWeekVarName;
    }

    public void setUpdateDateTimeDayVarName(String updateDateTimeDayVarName) {
        this.updateDateTimeDayVarName = updateDateTimeDayVarName;
    }

    public void setUpdateDateTimeYHourVarName(String updateDateTimeYHourVarName) {
        this.updateDateTimeYHourVarName = updateDateTimeYHourVarName;
    }

    public void setUpdateDateTimeMinuteVarName(String updateDateTimeMinuteVarName) {
        this.updateDateTimeMinuteVarName = updateDateTimeMinuteVarName;
    }

    public void setUpdateDateTimeSecondVarName(String updateDateTimeSecondVarName) {
        this.updateDateTimeSecondVarName = updateDateTimeSecondVarName;
    }

    public void setUpdateDateTimeMillisecondsVarName(String updateDateTimeMillisecondsVarName) {
        this.updateDateTimeMillisecondsVarName = updateDateTimeMillisecondsVarName;
    }
}
