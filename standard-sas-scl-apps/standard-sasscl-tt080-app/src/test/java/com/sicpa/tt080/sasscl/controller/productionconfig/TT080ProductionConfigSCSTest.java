package com.sicpa.tt080.sasscl.controller.productionconfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sicpa.standard.sasscl.controller.productionconfig.validator.ProductionParametersValidator;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.tt080.TT080TestSpringContextConfig;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("SCL")
@ContextConfiguration(classes = TT080TestSpringContextConfig.class)
public class TT080ProductionConfigSCSTest {

    @Autowired private ProductionParametersValidator productionParametersValidator;

    @Test
    public void productionConfig_ShouldHave_ValidExportMode(){
        //Given
        final ProductionMode exportMode = ProductionMode.EXPORT;

        //When
        final boolean validate = productionParametersValidator.validate(exportMode);

        //Then
        assertThat(validate, is(true));
    }

    @Test
    public void productionConfig_ShouldHave_ValidStandardMode(){
        //Give
        final ProductionMode exportMode = ProductionMode.STANDARD;

        //When
        final boolean validate = productionParametersValidator.validate(exportMode);

        //Then
        assertThat(validate, is(true));
    }

    @Test
    public void productionConfig_ShouldHave_ValidMaintenanceMode(){
        //Give
        final ProductionMode exportMode = ProductionMode.MAINTENANCE;

        //When
        final boolean validate = productionParametersValidator.validate(exportMode);

        //Then
        assertThat(validate, is(true));
    }

    @Test
    public void productionConfig_ShouldHave_ValidExportCodingMode(){
        //Give
        final ProductionMode exportMode = ProductionMode.EXPORT_CODING;

        //When
        final boolean validate = productionParametersValidator.validate(exportMode);

        //Then
        assertThat(validate, is(true));
    }

    @Test
    public void productionConfig_ShouldHave_ValidRefeedNormalMode(){
        //Give
        final ProductionMode exportMode = ProductionMode.REFEED_NORMAL;

        //When
        final boolean validate = productionParametersValidator.validate(exportMode);

        //Then
        assertThat(validate, is(true));
    }

    @Test
    public void productionConfig_ShouldHave_ValidRefeedCorrectionMode(){
        //Give
        final ProductionMode exportMode = ProductionMode.REFEED_CORRECTION;

        //When
        final boolean validate = productionParametersValidator.validate(exportMode);

        //Then
        assertThat(validate, is(true));
    }

    @Test
    public void productionConfig_ShouldHave_ValidCountingMode(){
        //Given
        final ProductionMode exportMode = ProductionMode.COUNTING;

        //When
        final boolean validate = productionParametersValidator.validate(exportMode);

        //Then
        assertThat(validate, is(true));
    }
}