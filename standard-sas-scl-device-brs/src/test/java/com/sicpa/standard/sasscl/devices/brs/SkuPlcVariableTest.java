package com.sicpa.standard.sasscl.devices.brs;

import com.sicpa.standard.plc.value.PlcString;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;

import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SkuPlcVariableTest {

    private static final String SKU_PLC_VARIABLE_NAME = "sku.plc.variable.name";
    private static final String A_BARCODE = "a barcode";
    private static final String SOME_VALUE = "some value";
    private SkuPlcVariable skuPlcVariable;

    @Before
    public void setUp() throws Exception {
    	ProductionParameters productionParameters = mock(ProductionParameters.class);    	    	
    	when(productionParameters.getSku()).thenReturn(new SKU(1, "a description", asList(A_BARCODE)));
    	
    	skuPlcVariable = new SkuPlcVariable(productionParameters, SKU_PLC_VARIABLE_NAME);       
    }

    @Test
    public void testGetVariableName() throws Exception {

        assertThat(skuPlcVariable.getVariableName(), equalTo(SKU_PLC_VARIABLE_NAME));
    }

    @SuppressWarnings("unchecked")
	@Test
    public void testGetVariableType() throws Exception {

        //noinspection unchecked
        assertThat((Class<PlcString>) skuPlcVariable.getVariableType(), equalTo(PlcString.class));
    }

    @Test
    public void testGetValueType() throws Exception {

        assertThat(skuPlcVariable.getValueType(), equalTo(String.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testSetPlcValue() throws Exception {

        skuPlcVariable.setPlcValue(mock(PlcString.class));
    }

    @Test
    public void testGetEmptyValue() throws Exception {
    	//NOTE: ask purpose
    	skuPlcVariable.setValue(EMPTY);
        assertThat(skuPlcVariable.getValue(), equalTo(EMPTY));
    }

    @Test
    public void testGetBarCodeValue() throws Exception {

        skuPlcVariable.setValue(SOME_VALUE);

        assertThat(skuPlcVariable.getValue(), equalTo(A_BARCODE));
    }
}
