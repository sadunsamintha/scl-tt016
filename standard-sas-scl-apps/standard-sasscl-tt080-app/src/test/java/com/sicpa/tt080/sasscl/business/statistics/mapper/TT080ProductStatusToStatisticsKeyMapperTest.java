package com.sicpa.tt080.sasscl.business.statistics.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sicpa.standard.sasscl.business.statistics.mapper.IProductStatusToStatisticKeyMapper;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;

import static com.sicpa.standard.sasscl.model.statistics.StatisticsKey.BAD;
import static com.sicpa.standard.sasscl.model.statistics.StatisticsKey.BLOB;
import static com.sicpa.standard.sasscl.model.statistics.StatisticsKey.GOOD;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class TT080ProductStatusToStatisticsKeyMapperTest {

  private IProductStatusToStatisticKeyMapper tt080KeyMapper;
  private IProductStatusToStatisticKeyMapper sampleDefaultKeyMapper;

  /**
   * Mapper returns the mutable StatisticsKey, instead of ReadOnlyStatisticsKey. This will help us with our comparison checks
   */
  private StatisticsKey referenceBLOB;
  private StatisticsKey referenceGOOD;
  private StatisticsKey referenceBAD;


  private static final ProductStatus CUSTOM_STATUS = new ProductStatus(1999, "CUSTOMSTATUS");

  @Before
  public void setUp(){

    //Start here and guarantee that no tests will interfere with each other, regarding this list of keys
    referenceBLOB = new StatisticsKey(BLOB.getDescription());
    referenceGOOD = new StatisticsKey(GOOD.getDescription());
    referenceBAD = new StatisticsKey(BAD.getDescription());

    /**
     * Sample Default Mapper will have:
     * CUSTOM_STATUS -> Mutable {@link StatisticsKey} GOOD
     * AUTHENTICATED -> Mutable {@link StatisticsKey} GOOD
     *
     * TT080 Key Mapper will have:
     * - none -
     */
    this.sampleDefaultKeyMapper = Mockito.mock(IProductStatusToStatisticKeyMapper.class);
    Mockito.when(sampleDefaultKeyMapper.getKey(or(eq(CUSTOM_STATUS), eq(ProductStatus.AUTHENTICATED))))
        .thenReturn(Arrays.asList(new StatisticsKey[]{referenceGOOD}));

    /**
     * Stub response to getAllKeys of our mocked sample key mapper. Returns:
     * - Mutable StaticKey BLOB
     * - Mutable StaticKey GOOD
     */
    Mockito.when(sampleDefaultKeyMapper.getAllKeys()).thenReturn(Arrays.asList(new StatisticsKey[]{referenceBLOB, referenceGOOD}));

    this.tt080KeyMapper = new TT080ProductStatusToStatisticsKeyMapper(sampleDefaultKeyMapper);
  }

  @Test
  public void getKey_Should_ReturnTheCorrectStatisticKey() {
    //Given
    final ProductStatus customProductStatus = new ProductStatus(2000, "CUSTOMSTATUS");

    //When
    tt080KeyMapper.add(customProductStatus, GOOD);

    //Then
    final Collection<StatisticsKey> keys = tt080KeyMapper.getKey(customProductStatus);
    assertThat(keys.contains(referenceGOOD), is(true));
  }

  @Test
  public void getKey_Should_ReturnDefaultStaticsKey() {
    //Given
    final Collection<StatisticsKey> keys = tt080KeyMapper.getKey(ProductStatus.AUTHENTICATED);

    //Then
    assertThat(keys.contains(referenceGOOD), is(true));
  }

  @Test
  public void getKey_Should_ReturnCombinationOfCustomAndDefaultKeys() {
    //Given - We add a custom BLOB Key to a CUSTOM_STATUS
    tt080KeyMapper.add(CUSTOM_STATUS, BLOB);

    //When
    final Collection<StatisticsKey> keys = tt080KeyMapper.getKey(CUSTOM_STATUS);

    //Then - We should have BLOB from custom and GOOD from Default sample Mapper
    assertThat(keys.contains(referenceGOOD), is(true));
    assertThat(keys.contains(referenceBLOB), is(true));
  }

  @Test(expected = IllegalArgumentException.class)
  public void getKey_Should_ThrowExceptionForUnexistentStatus() {
    //Given
    final ProductStatus customProductStatus = new ProductStatus(2001, "NON_EXISTENT_STATUS");

    //When
     tt080KeyMapper.getKey(customProductStatus);
  }

  @Test
  public void add_ShouldNot_ChangeTheDefaultKeyMapper() {
    //Given
    final ProductStatus customStatus = new ProductStatus(2000, "MOCK");

    //When
    tt080KeyMapper.add(customStatus, GOOD);

    //Then
    verify(sampleDefaultKeyMapper, never()).add(customStatus, GOOD);
  }

  @Test
  public void getAllKeys_Should_ConcatenatedCustomAndDefaultKeys() {
    //Given
    final ProductStatus customStatus = new ProductStatus(2000, "MOCK");

    //When
    tt080KeyMapper.add(customStatus, BAD);

    //And
    final Collection<StatisticsKey> allKeys = tt080KeyMapper.getAllKeys();

    //Then
    assertThat(allKeys.size(), is(3));
    assertThat(allKeys.contains(referenceGOOD), is(true));
    assertThat(allKeys.contains(referenceBLOB), is(true));
    assertThat(allKeys.contains(referenceBAD), is(true));
  }
}