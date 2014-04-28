package com.sicpa.standard.sasscl.devices.plc;


import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class PlcProviderTest {

    private PlcProvider plcProvider;
    private PropertyChangeListener propertyChangeListener;
    private IPlcAdaptor iplc;
    private IPlcAdaptor iplc2;

    @Before
    public void setUp() throws Exception {

        plcProvider = new PlcProvider();
        plcProvider.addChangeListener(propertyChangeListener = mock(PropertyChangeListener.class));
        iplc = mock(IPlcAdaptor.class);
        iplc2 = mock(IPlcAdaptor.class);
    }

    @Test
    public void testSetFirstTime() throws Exception {

        checkSet(iplc);
    }

    @Test
    public void testSetSecondTimeTime() throws Exception {

        plcProvider.set(iplc);

        plcProvider.set(iplc2);
        ArgumentCaptor<PropertyChangeEvent> argCapture = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(propertyChangeListener, times(2)).propertyChange(argCapture.capture());

        assertThat(argCapture.getValue().getPropertyName(), equalTo("PLC"));
        assertThat((IPlcAdaptor) argCapture.getValue().getOldValue(), equalTo(iplc));
        assertThat((IPlcAdaptor) argCapture.getValue().getNewValue(), equalTo(iplc2));
    }

    @Test
    public void testGet() throws Exception {

        checkSet(iplc);
        assertThat(plcProvider.get(), equalTo(iplc));
    }

    private void checkSet(IPlcAdaptor iplc) {

        plcProvider.set(iplc);
        verify(propertyChangeListener, times(1)).propertyChange(Matchers.<PropertyChangeEvent>anyObject());
    }
}
