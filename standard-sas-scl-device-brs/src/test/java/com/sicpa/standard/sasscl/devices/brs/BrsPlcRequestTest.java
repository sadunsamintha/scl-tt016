package com.sicpa.standard.sasscl.devices.brs;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

public class BrsPlcRequestTest {

    @Test
    public void testIdentity() throws Exception {

        Assert.assertThat(new BrsPlcRequest(1, "description one"), IsEqual.equalTo(new BrsPlcRequest(1, "drugi opis")));
    }
}
