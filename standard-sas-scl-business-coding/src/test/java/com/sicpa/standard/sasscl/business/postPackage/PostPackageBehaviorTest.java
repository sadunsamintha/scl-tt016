package com.sicpa.standard.sasscl.business.postPackage;


import com.sicpa.standard.client.common.provider.IProviderGetter;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorModeProvider;
import com.sicpa.standard.sasscl.provider.impl.ProductionBatchProvider;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PostPackageBehaviorTest {

    private @Mock ProductionParameters productionParameters;
    private @Mock ProductionBatchProvider batchIdProvider;
    private @Mock SubsystemIdProvider subsystemIdProvider;
    private @Mock AuthenticatorModeProvider authenticatorModeProvider;
    private @Mock BlobDetectionUtils blobUtils;
    private @Mock IProviderGetter<IAuthenticator> authenticatorProvider;
    private @Mock IAuthenticator authenticator;


    @Spy
    @InjectMocks
    private PostPackageBehavior postPackageBehavior = new PostPackageBehavior();


    @Test
    public void handleBadCodeWithEmptyCodes() {


        Code badCode = new Code();

        //stub
        when(blobUtils.isBlobDetected(any())).thenReturn(true);

        List<Product> products = postPackageBehavior.handleBadCode(badCode);

        Assert.assertTrue(products.isEmpty());
    }

    @Test
    public void handleBadCode() throws CryptographyException {

        SKU sku = new SKUWithBlobEnable();

        String code = "1234";

        //stub
        when(blobUtils.isBlobDetected(any())).thenReturn(true);

        postPackageBehavior.addCodes(Arrays.asList(code));

        //stub
        when(productionParameters.getSku()).thenReturn(sku);
        when(authenticatorProvider.get()).thenReturn(authenticator);


        Code badCode = new Code();

        List<Product> products = postPackageBehavior.handleBadCode(badCode);

        Assert.assertEquals(1, products.size());
        Assert.assertEquals("1234", products.get(0).getCode().getStringCode());
    }

    private class SKUWithBlobEnable extends SKU {

       @Override
       public boolean isBlobDetectionEnable() {
            return true;
        }


    }




}
