package com.sicpa.standard.sasscl.devices.plc.alert;

import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.business.alert.task.model.NoCapsAlertTaskModel;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NoCapsAlertTest {

    @Mock
    private PlcProvider plcProvider;

    @Mock
    private IPlcAdaptor plcAdaptor;

    private NoCapsAlertTaskModel noCapsAlertTaskModel;

    @InjectMocks
    private NoCapsAlertTask noCapsAlertTask = new NoCapsAlertTask();

    private String productCounterVarName = ".com.stLine[#x].stNotifications.nCounterTrigs";
    private String noCapsCounterVarName = ".com.stLine[#x].stNotifications.nNoCapTrigs";

    static {
        PlcLineHelper.addLineIndex(1);
    }


    @Before
    public void setup() throws PlcAdaptorException {
        //Set model
        noCapsAlertTaskModel = new NoCapsAlertTaskModel();

        noCapsAlertTaskModel.setEnabled(true);
        noCapsAlertTaskModel.setDelayInSec(5);
        noCapsAlertTaskModel.setSampleSize(5);
        noCapsAlertTaskModel.setThreshold(2);

        noCapsAlertTask.setModel(noCapsAlertTaskModel);

        //Set PLC variable names
        noCapsAlertTask.setProductCounterVarName(productCounterVarName);
        noCapsAlertTask.setNoCapsCounterVarName(noCapsCounterVarName);

        Mockito.when(plcProvider.get()).thenReturn(plcAdaptor);
    }

    @Test
    public void alertIsNotPresentNoPreviousPlcCounters() throws PlcAdaptorException {
        Mockito.when(plcAdaptor.read(Mockito.any())).thenReturn(10);

        Assert.assertFalse(noCapsAlertTask.isAlertPresent());
    }

    @Test
    public void alertIsNotPresentSampleSizeNotEnough() throws PlcAdaptorException {
        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(productCounterVarName, 1)))).thenReturn(10);
        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(noCapsCounterVarName, 1)))).thenReturn(1);
        Assert.assertFalse(noCapsAlertTask.isAlertPresent());

        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(productCounterVarName, 1)))).thenReturn(14);
        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(noCapsCounterVarName, 1)))).thenReturn(1);
        Assert.assertFalse(noCapsAlertTask.isAlertPresent());
    }

    @Test
    public void alertIsNotPresentThresholdNotReached() throws PlcAdaptorException {
        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(productCounterVarName, 1)))).thenReturn(10);
        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(noCapsCounterVarName, 1)))).thenReturn(1);
        Assert.assertFalse(noCapsAlertTask.isAlertPresent());

        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(productCounterVarName, 1)))).thenReturn(15);
        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(noCapsCounterVarName, 1)))).thenReturn(2);
        Assert.assertFalse(noCapsAlertTask.isAlertPresent());
    }

    @Test
    public void alertIsPresent() throws PlcAdaptorException {
        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(productCounterVarName, 1)))).thenReturn(10);
        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(noCapsCounterVarName, 1)))).thenReturn(1);
        Assert.assertFalse(noCapsAlertTask.isAlertPresent());

        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(productCounterVarName, 1)))).thenReturn(15);
        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(noCapsCounterVarName, 1)))).thenReturn(3);
        Assert.assertTrue(noCapsAlertTask.isAlertPresent());
    }

    @Test
    public void alertIsPresentTwice() throws PlcAdaptorException {
        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(productCounterVarName, 1)))).thenReturn(10);
        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(noCapsCounterVarName, 1)))).thenReturn(1);
        Assert.assertFalse(noCapsAlertTask.isAlertPresent());

        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(productCounterVarName, 1)))).thenReturn(15);
        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(noCapsCounterVarName, 1)))).thenReturn(3);
        Assert.assertTrue(noCapsAlertTask.isAlertPresent());

        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(productCounterVarName, 1)))).thenReturn(20);
        Mockito.when(plcAdaptor.read(PlcVariable.createInt32Var(
                PlcLineHelper.replaceLinePlaceholder(noCapsCounterVarName, 1)))).thenReturn(5);
        Assert.assertTrue(noCapsAlertTask.isAlertPresent());
    }
}