package com.sicpa.tt080.sasscl.controller.productionconfig.mapping;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sicpa.standard.sasscl.controller.productionconfig.mapping.IProductionConfigMapping;
import com.sicpa.standard.sasscl.model.ProductionMode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class TT080ProductionConfigMappingTest {

  private TT080ProductionConfigMapping tt080ProductionConfigMapping;
  private IProductionConfigMapping sampleMapping;

  @Before
  public void setup(){
   this.sampleMapping = Mockito.mock(IProductionConfigMapping.class);

    /**
     * STUB
     * Only a single mode will be available at mocked mapping
     */
    Mockito.when(this.sampleMapping.getProductionConfigId(ProductionMode.STANDARD)).thenReturn("standard");

   this.tt080ProductionConfigMapping = new TT080ProductionConfigMapping(sampleMapping);
  }

  @Test
  public void getProductionConfigId_Should_ReturnBaseMappedId() {
    //Given
    final String correctId = "standard";

    //When
    final String productionConfigId = this.tt080ProductionConfigMapping.getProductionConfigId(ProductionMode.STANDARD);

    //Then
    assertThat(productionConfigId, is(correctId));
  }

  @Test
  public void getProductionConfigId_Should_ReturnNullIfNothingWasFound() {
    //Given
    final ProductionMode inexistentMode = new ProductionMode(99, "non.existent.mode", false);

    //When
    final String productionConfigId = this.tt080ProductionConfigMapping.getProductionConfigId(inexistentMode);

    //Then
    assertThat(productionConfigId, is(nullValue()));
  }

  @Test
  public void getProductionConfigId_Should_HaveCorrectySearchPriority() {
    //Given
    final String customId = "notdefault";
    this.tt080ProductionConfigMapping.put(ProductionMode.STANDARD, customId);

    //When
    final String productionConfigId = this.tt080ProductionConfigMapping.getProductionConfigId(ProductionMode.STANDARD);

    //Then
    assertThat(productionConfigId, is(customId));
  }

  @Test
  public void put_Should_AddModeCorrectly() {
    //Given
    final String configId = "mock";
    final ProductionMode mockMode = new ProductionMode(99, "mock.mode", false);
    final int originalSize = this.tt080ProductionConfigMapping.getCustomMappings().size();

    //When
    this.tt080ProductionConfigMapping.put(mockMode, configId);

    //Then
    final Map<ProductionMode, String> customMappings = this.tt080ProductionConfigMapping.getCustomMappings();
    assertThat(customMappings.containsKey(mockMode), is(true));
    assertThat(customMappings.get(mockMode), is(configId));
    assertThat(customMappings.size(), is(originalSize+1));
  }

  @Test
  public void put_Should_NotTouchDefaultMapping() {
    //Given
    final String configId = "mockConfig";

    //When
    this.tt080ProductionConfigMapping.put(ProductionMode.EXPORT, configId);

    //Then
    verify(sampleMapping, never()).put(any(ProductionMode.class), anyString());
  }

  @Test(expected = NullPointerException.class)
  public void put_Should_NotAllowNullMode() {
    //Given
    final String configId = "mockConfig";

    //When
    this.tt080ProductionConfigMapping.put(null, configId);
  }

  @Test(expected = NullPointerException.class)
  public void put_Should_NotAllowNullProductionConfigId() {
    //When
    this.tt080ProductionConfigMapping.put(ProductionMode.EXPORT, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void put_Should_NotAllowEmptyProductionConfigId() {
    //When
    this.tt080ProductionConfigMapping.put( ProductionMode.EXPORT, " ");
  }

}