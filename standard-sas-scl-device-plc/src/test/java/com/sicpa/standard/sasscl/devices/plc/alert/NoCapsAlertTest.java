package com.sicpa.standard.sasscl.devices.plc.alert;

import com.sicpa.standard.sasscl.business.alert.task.model.NoCapsAlertTaskModel;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.sicpa.standard.plc.value.PlcVariable.createInt32Var;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.addLineIndex;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.replaceLinePlaceholder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

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
        addLineIndex(1);
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

        when(plcProvider.get()).thenReturn(plcAdaptor);
    }

    @Test
    public void alertIsNotPresentNoPreviousPlcCounters() throws PlcAdaptorException {
        updatePlcCounters(10, 0);
        assertFalse(noCapsAlertTask.isAlertPresent());
    }

    @Test
    public void alertIsNotPresentSampleSizeNotEnough() throws PlcAdaptorException {
        updatePlcCounters(10, 1);
        assertFalse(noCapsAlertTask.isAlertPresent());

        updatePlcCounters(14, 1);
        assertFalse(noCapsAlertTask.isAlertPresent());
    }

    @Test
    public void alertIsNotPresentThresholdNotReached() throws PlcAdaptorException {
        updatePlcCounters(10, 1);
        assertFalse(noCapsAlertTask.isAlertPresent());

        updatePlcCounters(15, 2);
        assertFalse(noCapsAlertTask.isAlertPresent());
    }

    @Test
    public void alertIsPresent() throws PlcAdaptorException {
        updatePlcCounters(10, 1);
        assertFalse(noCapsAlertTask.isAlertPresent());

        updatePlcCounters(15, 3);
        assertTrue(noCapsAlertTask.isAlertPresent());
    }

    @Test
    public void alertIsPresentTwice() throws PlcAdaptorException {
        updatePlcCounters(10, 1);
        assertFalse(noCapsAlertTask.isAlertPresent());

        updatePlcCounters(15, 3);
        assertTrue(noCapsAlertTask.isAlertPresent());

        updatePlcCounters(20, 5);
        assertTrue(noCapsAlertTask.isAlertPresent());
    }

    private void updatePlcCounters(int numProducts, int numNoCapsProducts) throws PlcAdaptorException {
        when(plcAdaptor.read(createInt32Var(
                replaceLinePlaceholder(productCounterVarName, 1)))).thenReturn(numProducts);
        when(plcAdaptor.read(createInt32Var(
                replaceLinePlaceholder(noCapsCounterVarName, 1)))).thenReturn(numNoCapsProducts);
    }
}