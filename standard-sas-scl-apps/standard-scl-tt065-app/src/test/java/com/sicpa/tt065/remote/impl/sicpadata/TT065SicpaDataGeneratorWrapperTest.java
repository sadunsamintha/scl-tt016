package com.sicpa.tt065.remote.impl.sicpadata;


import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;
import com.sicpa.std.common.api.codetype.dto.CodeTypeDto;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorDto;
import com.sicpa.std.common.core.coding.business.generator.scl.SCLCodesFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SCLCodesFactory.class)
public class TT065SicpaDataGeneratorWrapperTest {


    @Mock private SicpadataGeneratorDto generator ;
    @Mock private IBSicpadataGenerator encoder ;
    @Mock private ICryptoFieldsConfig cryptoFieldsConfig ;


    @Test
    public void getEncryptedCodes() throws CryptographyException, SicpadataException {

        // stub static methods
        PowerMockito.mockStatic(SCLCodesFactory.class);
        when(SCLCodesFactory.generateSCLCodeForPrinting(any(SicpadataGeneratorDto.class), any(Object[].class), anyInt())).thenReturn(createSclCodes());

        // stub non static methods
        final int year = 2;
        final int subsystemId = 1165;
        final CodeTypeDto codeTypeDto = new CodeTypeDto();
        codeTypeDto.setId(1000L);

        when(generator.getBatchId()).thenReturn(3L);
        when(generator.getCodeType()).thenReturn(codeTypeDto);
        when(generator.getSicpadataGeneratorObject()).thenReturn(encoder);
        when(encoder.getRemainingCapacity()).thenReturn(1500L);

        // test
        TT065SicpaDataGeneratorWrapper sicpaDataGeneratorWrapper = new TT065SicpaDataGeneratorWrapper(generator, year, subsystemId,  cryptoFieldsConfig);
        List<String> codes = sicpaDataGeneratorWrapper.getEncryptedCodes(1000L);

        // assert & verify
        Assert.assertEquals("0996908468" + TT065SicpaDataGeneratorWrapper.BLOCK_SEPARATOR + "093G001YG", codes.get(0));

        //verify that the static method is call only ones
        PowerMockito.verifyStatic();
        SCLCodesFactory.generateSCLCodeForPrinting(any(SicpadataGeneratorDto.class), any(Object[].class), anyInt());

        //verify non static method calls
        verify(encoder, times(1)).getRemainingCapacity();
        verify(generator, times(1)).getBatchId();
        verify(generator, times(1)).getCodeType();
        verify(generator, times(1)).getSicpadataGeneratorObject();
    }

    @Test(expected = EncoderEmptyException.class)
    public void getEncryptedCodesWithAnEmptyEncoder() throws CryptographyException, SicpadataException {

        // stub static methods
        PowerMockito.mockStatic(SCLCodesFactory.class);
        when(SCLCodesFactory.generateSCLCodeForPrinting(any(SicpadataGeneratorDto.class), any(Object[].class), anyInt())).thenReturn(createSclCodes());

        // stub non static methods
        final int year = 2;
        final int subsystemId = 1165;
        final CodeTypeDto codeTypeDto = new CodeTypeDto();
        codeTypeDto.setId(1000L);

        when(generator.getBatchId()).thenReturn(3L);
        when(generator.getCodeType()).thenReturn(codeTypeDto);
        when(generator.getSicpadataGeneratorObject()).thenReturn(encoder);
        //simulated the encoder is emtpy
        when(encoder.getRemainingCapacity()).thenReturn(0L);

        // test
        TT065SicpaDataGeneratorWrapper sicpaDataGeneratorWrapper = new TT065SicpaDataGeneratorWrapper(generator, year, subsystemId,  cryptoFieldsConfig);
        sicpaDataGeneratorWrapper.getEncryptedCodes(1000L);
    }

    @Test(expected = CryptographyException.class)
    public void getEncryptedCodesWithAnCryptographyException() throws CryptographyException, SicpadataException {

        // stub static methods
        PowerMockito.mockStatic(SCLCodesFactory.class);
        when(SCLCodesFactory.generateSCLCodeForPrinting(any(SicpadataGeneratorDto.class), any(Object[].class), anyInt())).thenThrow(SicpadataException.class);

        // stub non static methods
        final int year = 2;
        final int subsystemId = 1165;
        final CodeTypeDto codeTypeDto = new CodeTypeDto();
        codeTypeDto.setId(1000L);

        when(generator.getBatchId()).thenReturn(3L);
        when(generator.getCodeType()).thenReturn(codeTypeDto);
        when(generator.getSicpadataGeneratorObject()).thenReturn(encoder);
        when(encoder.getRemainingCapacity()).thenReturn(1500L);

        // test
        TT065SicpaDataGeneratorWrapper sicpaDataGeneratorWrapper = new TT065SicpaDataGeneratorWrapper(generator, year, subsystemId,  cryptoFieldsConfig);
        sicpaDataGeneratorWrapper.getEncryptedCodes(1000L);
    }



    private List<SCLCodesFactory.SCLCode> createSclCodes() {
        SCLCodesFactory.SCLCode sclCode = new SCLCodesFactory.SCLCode();
        sclCode.addCodeForTptinting(SCLCodesFactory.SCLCode.SERIAL_NUMBER_KEY,"0996908468");
        sclCode.addCodeForTptinting(SCLCodesFactory.SCLCode.HRC1_KEY,"093G001YG");
        return Arrays.asList(sclCode);
    }


}
