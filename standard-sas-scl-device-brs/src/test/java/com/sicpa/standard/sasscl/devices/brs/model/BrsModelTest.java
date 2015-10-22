package com.sicpa.standard.sasscl.devices.brs.model;


import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BrsModelTest {


    @Test
    public void getActiveAddresses() {

        BrsModel brsModel = new BrsModel();
        brsModel.setIp1("192.168.1.121");
        brsModel.setIp2("");
        brsModel.setIp3(null);
        brsModel.setIp4("192.168.1.124");

        List<String> addresses = brsModel.getActiveAddresses();
        List<String> expectedAddresses = Arrays.asList("192.168.1.121", "192.168.1.124");

        assertThat(addresses, is(expectedAddresses));
    }
}
