package com.sicpa.tt080.sasscl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.sicpa.standard.client.common.ioc.PropertiesFile;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TT080SASSCLGlobalPropertiesTest {

    String custoId;
    String dmEncoding;
    String dmFormat;
    String sicpadataPassword;
    String language;
    String productionConfigFolder;
    String profileConfigFolderPath;
    Matcher<Object> leibingerAdminLevelMatcher;
    Matcher<Object> leibingerOperatorLevelMatcher;

    private PropertiesFile propertiesFile;

    public TT080SASSCLGlobalPropertiesTest(final String custoId, final String dmEncoding, final String dmFormat, final String sicpadataPassword,
                                           final String language, final String productionConfigFolder, final String profileFolderPath,
                                           final Matcher<Object> leibingerAdminLevelMatcher, final Matcher<Object> leibingerOperatorLevelMatcher){
        this.custoId = custoId;
        this.dmEncoding = dmEncoding;
        this.dmFormat = dmFormat;
        this.sicpadataPassword = sicpadataPassword;
        this.language = language;
        this.productionConfigFolder = productionConfigFolder;
        this.profileConfigFolderPath = profileFolderPath;
        this.leibingerAdminLevelMatcher = leibingerAdminLevelMatcher;
        this.leibingerOperatorLevelMatcher = leibingerOperatorLevelMatcher;
    }

    @Parameters()
    public static Iterable<Object[]> data() throws Throwable{
        return Arrays.asList(new Object[][]{
                {"TT080", "ASCII", "DM_8x18", "Dominican Republic*2o19.tRacE", "es", "productionConfig-SCL", "profiles/TT080-SCL/config", not(nullValue()), not(nullValue())},
                {"TT080", "ASCII", "DM_8x18", "Dominican Republic*2o19.tRacE", "es", "productionConfig-SAS", "profiles/TT080-SAS/config", nullValue(), nullValue()}
        });
    }

    @Before
    public void setup() throws IOException {
        final File file = new File(this.profileConfigFolderPath);
        this.propertiesFile = new PropertiesFile(Collections.emptyList(), Arrays.asList(file.getAbsolutePath()));
    }

    @Test
    public void custoId_ShouldBeEqualsTo_DominicanRepCustomID(){
        //Given
        final Object o = propertiesFile.get("custo.id");

        //Then
        assertThat((String) o, equalToIgnoringCase(this.custoId));
    }

    @Test
    public void DataMatrixEncoding_ShouldBe_Configured(){
        //Given
        final Object o = propertiesFile.get("dm.encoding");

        //Then
        assertThat(o, allOf(instanceOf(String.class), is(this.dmEncoding)));
    }

    @Test
    public void DataMatrixFormat_ShouldBe_Configured(){
        //Given
        final Object o = propertiesFile.get("dm.format");

        //Then
        assertThat(o, allOf(instanceOf(String.class), is(this.dmFormat)));
    }

    @Test
    public void sicpadataPassword_ShouldBeEqualsTo_SicpadataConfigLib_CryptoAccess(){
        //Given
        final Object o = propertiesFile.get("sicpadataPassword");

        //Then
        assertThat(o, allOf(instanceOf(String.class), is(this.sicpadataPassword)));
    }

    @Test
    public void defaultLanguage_ShouldBe_Espanol(){
        //Given
        final Object o = propertiesFile.get("language");

        //Then
        assertThat(o, allOf(instanceOf(String.class), is(this.language)));
    }

    @Test
    public void productionConfigFolder_ShouldBe_Configured(){
        //Given
        final Object o = propertiesFile.get("production.config.folder");

        //Then
        assertThat(o, allOf(instanceOf(String.class), is(this.productionConfigFolder)));
    }

    @Test
    public void leibingerFullAdminUserLevel_MatchSecurityDefinition(){
        //Given
        final Object o = propertiesFile.get("leibinger.user.level.0");

        //Then
        assertThat(o, this.leibingerAdminLevelMatcher);
    }

    @Test
    public void leibingerOperatorUserLevel_MatchSecurityDefinition(){
        //Given
        final Object o = propertiesFile.get("leibinger.user.level.operator");

        //Then
        assertThat(o, this.leibingerOperatorLevelMatcher);
    }

    @Test
    public void leibingerAdminOperatorUserLevel_MatchSecurityDefinition(){
        //Given
        final Object admin = propertiesFile.get("leibinger.user.level.0");
        final Object operator = propertiesFile.get("leibinger.user.level.operator");

        if ((admin != null) || (operator != null)) {
            assertThat(admin, allOf(instanceOf(String.class), is(operator.toString())));
        }

    }
}