package com.sicpa.tt080.sasscl.devices.remote.mapping;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import com.sicpa.standard.sasscl.devices.remote.mapping.IRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.model.ProductStatus;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


public class TT080RemoteServerProductStatusMappingTest {

  private IRemoteServerProductStatusMapping tt080Mapping;
  private IRemoteServerProductStatusMapping sampleMapping;

  @Before
  public void setup(){
    this.sampleMapping = Mockito.mock(IRemoteServerProductStatusMapping.class);

    //STUB - Will return ZERO (0) when ProductsStatus is not found
    Mockito.when(this.sampleMapping.getRemoteServerProdutcStatus(eq(ProductStatus.AUTHENTICATED)))
        .thenReturn(1);

    this.tt080Mapping = new TT080RemoteServerProductStatusMapping(this.sampleMapping);
  }

  @Test
  public void add_ShouldNot_TouchDefaultMapping() {
    //Given
    final ProductStatus customStatus = new ProductStatus(2000, "MOCK");
    final int idOnRemote = 200;

    //When
    this.tt080Mapping.add(customStatus, idOnRemote);

    //Then
    verify(this.sampleMapping, never()).add(customStatus, idOnRemote);
  }

  @Test
  public void add_Should_AddToTheCustomMapping() {
    //Given
    final ProductStatus customStatus = new ProductStatus(2000, "MOCK");
    final int idOnRemote = 200;

    //When
    this.tt080Mapping.add(customStatus, idOnRemote);

    //And
    final int remoteServerProdutcStatus = ((TT080RemoteServerProductStatusMapping) this.tt080Mapping)
        .customMap.get(customStatus);

    //Then
    assertThat(remoteServerProdutcStatus, is(idOnRemote));
  }

  @Test(expected=NullPointerException.class)
  public void add_Should_ThrowExceptionCaseStatusIsNull() {
    //Given
    final int idOnRemote = 200;

    //When
    this.tt080Mapping.add(null, idOnRemote);
  }

  @Test
  public void getRemoteServerProdutcStatus_Should_HasAccessToDefaultMappings() {
    //When
    final int idOnRemote = this.tt080Mapping.getRemoteServerProdutcStatus(ProductStatus.AUTHENTICATED);

    //Then
    assertThat(idOnRemote, is(1));
  }

  @Test
  public void getRemoteServerProdutcStatus_Should_ReturnDefaultCaseNoneFound() {
    //When
    final ProductStatus inexistentStatus = new ProductStatus(1999, "INEXISTENT");
    final int idOnRemote = this.tt080Mapping.getRemoteServerProdutcStatus(inexistentStatus);

    //Then
    assertThat(idOnRemote, is(0));
  }

  @Test
  public void getRemoteServerProdutcStatus_Should_ReturnCustomMappings() {
    //Given
    final ProductStatus customStatus = new ProductStatus(2000, "MOCK");
    final int idOnRemote = 200;

    //When
    this.tt080Mapping.add(customStatus, idOnRemote);

    //And
    final int remoteServerProdutcStatus = this.tt080Mapping.getRemoteServerProdutcStatus(customStatus);

    //Then
    assertThat(remoteServerProdutcStatus, is(idOnRemote));
  }
}