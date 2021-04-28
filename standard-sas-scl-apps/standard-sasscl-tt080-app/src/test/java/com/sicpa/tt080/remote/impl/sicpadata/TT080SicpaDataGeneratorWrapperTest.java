package com.sicpa.tt080.remote.impl.sicpadata;


import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;
import com.sicpa.std.common.api.codetype.dto.CodeTypeDto;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorDto;
import com.sicpa.std.common.core.coding.business.generator.scl.SCLCodesFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
@PrepareForTest(SCLCodesFactory.class)
public class TT080SicpaDataGeneratorWrapperTest {

  @Mock private SicpadataGeneratorDto generator ;
  @Mock private IBSicpadataGenerator encoder ;
  @Mock private ICryptoFieldsConfig cryptoFieldsConfig ;
  private CodeTypeDto codeTypeDto;

  private static final long BEER_CODETYPE = 1L;
  private static final int YEAR = 2;
  private static final int VINICOLA_DEL_NORTESA_SITE = 801;

  @Before
  public void setup() throws SicpadataException {
    PowerMockito.mockStatic(SCLCodesFactory.class);
    when(SCLCodesFactory.generateSCLCodeForPrinting(any(SicpadataGeneratorDto.class), any(Object[].class), anyInt())).thenReturn(createSclCodes());

    this.codeTypeDto = new CodeTypeDto();
    codeTypeDto.setId(BEER_CODETYPE);
  }

  @Test
  public void getEncryptedCodes_Returns_CodeWithCorrectBlocks() throws CryptographyException, SicpadataException {
    //Given Stubs
    when(generator.getBatchId()).thenReturn(28L);
    when(generator.getCodeType()).thenReturn(codeTypeDto);
    when(generator.getSicpadataGeneratorObject()).thenReturn(encoder);
    when(encoder.getRemainingCapacity()).thenReturn(1500L);

    //When
    final TT080SicpaDataGeneratorWrapper sicpaDataGeneratorWrapper = new TT080SicpaDataGeneratorWrapper(generator, YEAR,
       VINICOLA_DEL_NORTESA_SITE,  cryptoFieldsConfig);
    final List<String> codes = sicpaDataGeneratorWrapper.getEncryptedCodes(1000L);

    //Then
    assertThat(codes.get(0), is("77448969872" + TT080SicpaDataGeneratorWrapper.PRINTER_SPACE_REPRESENTATION + "8E0W0PP3"));
  }


    @Test
    public void getEncryptedCodes_Invokes_InternalMethods() throws CryptographyException, SicpadataException {
        //Given Stubs
        when(generator.getBatchId()).thenReturn(28L);
        when(generator.getCodeType()).thenReturn(codeTypeDto);
        when(generator.getSicpadataGeneratorObject()).thenReturn(encoder);
        when(encoder.getRemainingCapacity()).thenReturn(1500L);

        //When
        final TT080SicpaDataGeneratorWrapper sicpaDataGeneratorWrapper = new TT080SicpaDataGeneratorWrapper(generator, YEAR, VINICOLA_DEL_NORTESA_SITE,  cryptoFieldsConfig);
        sicpaDataGeneratorWrapper.getEncryptedCodes(1000L);

        //Then
        PowerMockito.verifyStatic();
        SCLCodesFactory.generateSCLCodeForPrinting(any(SicpadataGeneratorDto.class), any(Object[].class), anyInt());
        verify(encoder, times(1)).getRemainingCapacity();
        verify(generator, times(1)).getBatchId();
        verify(generator, times(1)).getCodeType();
        verify(generator, times(1)).getSicpadataGeneratorObject();
    }

    @Test(expected = EncoderEmptyException.class)
    public void getEncryptedCodes_Throws_ExceptionWhenEncondersIsEmpty() throws CryptographyException, SicpadataException {
        //Given Stubs
        when(generator.getBatchId()).thenReturn(28L);
        when(generator.getCodeType()).thenReturn(codeTypeDto);
        when(generator.getSicpadataGeneratorObject()).thenReturn(encoder);
        when(encoder.getRemainingCapacity()).thenReturn(0L);

        //When
        TT080SicpaDataGeneratorWrapper sicpaDataGeneratorWrapper = new TT080SicpaDataGeneratorWrapper(generator, YEAR, VINICOLA_DEL_NORTESA_SITE,  cryptoFieldsConfig);
        sicpaDataGeneratorWrapper.getEncryptedCodes(1000L);
    }

    @Test(expected = CryptographyException.class)
    public void getEncryptedCodes_Throws_ExceptionWhenCryptographyErrorOccurs() throws CryptographyException, SicpadataException {
        //Given Stubs
        when(SCLCodesFactory.generateSCLCodeForPrinting(any(SicpadataGeneratorDto.class), any(Object[].class), anyInt())).thenThrow(SicpadataException.class);
        when(generator.getBatchId()).thenReturn(3L);
        when(generator.getCodeType()).thenReturn(codeTypeDto);
        when(generator.getSicpadataGeneratorObject()).thenReturn(encoder);
        when(encoder.getRemainingCapacity()).thenReturn(1500L);

        // test
        TT080SicpaDataGeneratorWrapper sicpaDataGeneratorWrapper = new TT080SicpaDataGeneratorWrapper(generator, YEAR, VINICOLA_DEL_NORTESA_SITE,  cryptoFieldsConfig);
        sicpaDataGeneratorWrapper.getEncryptedCodes(1000L);
    }

    private List<SCLCodesFactory.SCLCode> createSclCodes() {
        SCLCodesFactory.SCLCode sclCode = new SCLCodesFactory.SCLCode();
        sclCode.addCodeForTptinting(SCLCodesFactory.SCLCode.SERIAL_NUMBER_KEY,"77448969872");
        sclCode.addCodeForTptinting(SCLCodesFactory.SCLCode.HRD_KEY,"8E0W0PP3");
        return Collections.singletonList(sclCode);
    }
}
