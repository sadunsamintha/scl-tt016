package com.sicpa.tt016.business;


import com.sicpa.standard.sasscl.business.postPackage.PostPackageBehavior;
import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterType;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.provider.impl.ProductionConfigProvider;
import com.sicpa.tt016.business.postpackage.TT016PostPackage;
import com.sicpa.tt016.printer.simulator.TT016PrinterAdaptorSimulator;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TT016PostPackageTest {

    @Mock private ProductionConfigProvider productionConfigProvider;

    @Mock private IProductionConfig config;

    @InjectMocks private TT016PostPackage postPackage = new TT016PostPackage();

    @Test
    public void provideCode() {

        //stub
        when(productionConfigProvider.get()).thenReturn(config);
        when(config.getPrinterConfigs()).thenReturn(createPrinterConfigs());

        //prepare the test
        TT016PrinterAdaptorSimulator requestor = new TT016PrinterAdaptorSimulator();
        TestPostPackageBehavior postPackageBehavior = new TestPostPackageBehavior();
        postPackage.registerModule(postPackageBehavior, Arrays.asList(requestor));
        List<String> codes = Arrays.asList("0996908468>-<093G001YG");
        postPackage.provideCode(codes, requestor);

        //assert & verify
        List<Code> sclCodes = postPackageBehavior.getCodes();
        String pruneSclCode = sclCodes.get(0).toString();

        Assert.assertEquals("0996908468", pruneSclCode);
        verify(productionConfigProvider, times(2)).get();
        verify(config, times(2)).getPrinterConfigs();

    }

    @Test
    public void provideCodePostPostPackageNotEnable() {
        //stub
        when(productionConfigProvider.get()).thenReturn(config);
        // simulate is not enable
        when(config.getPrinterConfigs()).thenReturn(null);

        //prepare the test
        TT016PrinterAdaptorSimulator requestor = new TT016PrinterAdaptorSimulator();
        TestPostPackageBehavior postPackageBehavior = new TestPostPackageBehavior();
        postPackage.registerModule(postPackageBehavior, Arrays.asList(requestor));
        List<String> codes = Arrays.asList("0996908468>-<093G001YG");
        postPackage.provideCode(codes, requestor);

        //assert & verify
        List<Code> sclCodes = postPackageBehavior.getCodes();

        Assert.assertTrue(sclCodes.isEmpty());
        verify(productionConfigProvider, times(1)).get();
        verify(config, times(1)).getPrinterConfigs();

    }

    private Collection<PrinterConfig> createPrinterConfigs() {
        PrinterConfig printerConfig = new PrinterConfig();
        printerConfig.setPrinterType(PrinterType.LEIBINGER);
        printerConfig.setValidatedBy("validatedBy");
        return Arrays.asList(printerConfig);
    }

    class TestPostPackageBehavior extends PostPackageBehavior {

        public List<Code> getCodes() {
            return codes;
        }

    }
}
