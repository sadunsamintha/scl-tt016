package com.sicpa.standard.sasscl.devices.brs;

import com.sicpa.standard.sasscl.skucheck.SkuCheckFacade;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class SkuCheckFacadeProviderTest {

    @Test
    public void constructor() throws Exception {

        SkuCheckFacadeProvider provider = new SkuCheckFacadeProvider();
        SkuCheckFacade skuCheckFacade = mock(SkuCheckFacade.class);
        provider.set(skuCheckFacade);
        assertThat(provider.get(), equalTo(skuCheckFacade));
    }
}
