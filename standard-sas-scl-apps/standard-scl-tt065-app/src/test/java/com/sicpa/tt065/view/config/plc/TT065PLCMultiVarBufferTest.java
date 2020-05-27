package com.sicpa.tt065.view.config.plc;

import java.util.Map;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TT065PLCMultiVarBufferTest {

  TT065PLCMultiVarBuffer plcMultiVarBuffer;

  @Before
  public void setup(){
    this.plcMultiVarBuffer = new TT065PLCMultiVarBuffer();
  }

  @Test(expected = IllegalArgumentException.class)
  public void when_bufferPLCChange_withNullLogicalName_ExpectException(){
    //given
    final String value = UUID.randomUUID().toString();
    final int lineIndex = 10;

    //when
    this.plcMultiVarBuffer.bufferPLCChange(null, value, lineIndex);
  }

  @Test(expected = NullPointerException.class)
  public void when_bufferPLCChange_withNullValue_ExpectException(){
    //given
    final String logicalName = UUID.randomUUID().toString();
    final int lineIndex = 11;

    //when
    this.plcMultiVarBuffer.bufferPLCChange(logicalName, null, lineIndex);
  }

  @Test
  public void when_bufferPLCChange_withEmptyValue_ItWillBeBuffered(){
    //given
    final String logicalName = "MyValue";
    final String value = "";
    final int lineIndex = 10;

    //when
    this.plcMultiVarBuffer.bufferPLCChange(logicalName, value, lineIndex);

    //then
    final Map<Integer, Map<String, String>> integerMapMap = this.plcMultiVarBuffer.drainBuffer();
    assertEquals(integerMapMap.size(), 1);
    assertEquals( integerMapMap.get(lineIndex).get(logicalName), value);
  }

  @Test
  public void when_bufferPLCChange_Changes_ItWillBeBuffered(){
    //given
    final String logicalName = "MyValue";
    final String value = UUID.randomUUID().toString();
    final int lineIndex = 10;

    //when
    this.plcMultiVarBuffer.bufferPLCChange(logicalName, value, lineIndex);

    //then
    final Map<Integer, Map<String, String>> integerMapMap = this.plcMultiVarBuffer.drainBuffer();
    assertEquals(integerMapMap.size(), 1);
    assertEquals( integerMapMap.get(lineIndex).get(logicalName), value);
  }

  @Test
  public void when_drainBuffer_WithRepeatedVariablesBuffered_ItWillReturnOnlyTheLastUpdatedValues(){
    //given
    final String logicalName = "MyValue";
    final int lineIndex = 10;
    for(int count = 0; count < 20; count ++){
      this.plcMultiVarBuffer.bufferPLCChange(logicalName, UUID.randomUUID().toString(), lineIndex);
    }
    final String lastValue = UUID.randomUUID().toString();
    this.plcMultiVarBuffer.bufferPLCChange("noise", "noise_value", lineIndex);

    //when
    this.plcMultiVarBuffer.bufferPLCChange(logicalName, lastValue, lineIndex);

    //then
    final Map<Integer, Map<String, String>> integerMapMap = this.plcMultiVarBuffer.drainBuffer();
    assertEquals(integerMapMap.get(lineIndex).size(), 2);
    assertEquals(integerMapMap.get(lineIndex).get(logicalName), lastValue);
  }

  @Test
  public void when_drainBuffer_WithDifferentLines_ItWillReturnGroupedPerLine(){
    //given
    final int maxLines = 10;
    //when
    for(int count = 0; count < maxLines; count ++){
      this.plcMultiVarBuffer.bufferPLCChange("MyValue", UUID.randomUUID().toString(), count);
      this.plcMultiVarBuffer.bufferPLCChange("noise", "noise_value", count);
    }

    //then
    final Map<Integer, Map<String, String>> integerMapMap = this.plcMultiVarBuffer.drainBuffer();
    assertEquals(integerMapMap.size(), maxLines);
  }

  @Test
  public void when_drainBuffer_WithDifferentLines_ItWillReturnOrganizeChangesCorrectly(){
    //given
    final String line1Value = UUID.randomUUID().toString();
    final String line2Value = UUID.randomUUID().toString();
    final String line3Value = UUID.randomUUID().toString();
    final String logicalName = "MyValue";

    this.plcMultiVarBuffer.bufferPLCChange(logicalName, line1Value, 1);
    this.plcMultiVarBuffer.bufferPLCChange(logicalName, line2Value, 2);
    this.plcMultiVarBuffer.bufferPLCChange(logicalName, line3Value, 3);

    //when
    final Map<Integer, Map<String, String>> integerMapMap = this.plcMultiVarBuffer.drainBuffer();

    //then
    assertEquals(integerMapMap.get(1).get(logicalName), line1Value);
    assertEquals(integerMapMap.get(2).get(logicalName), line2Value);
    assertEquals(integerMapMap.get(3).get(logicalName), line3Value);
  }

  @Test
  public void when_drainBuffer_itWillRemoveReturnedItemsInternally() {
    //given
    final String value = UUID.randomUUID().toString();
    final String logicalName = "MyValue";
    this.plcMultiVarBuffer.bufferPLCChange(logicalName, value, 1);

    //when
    this.plcMultiVarBuffer.drainBuffer();

    //then
    final Map<Integer, Map<String, String>> emptyMap = this.plcMultiVarBuffer.drainBuffer();
    assertTrue(emptyMap.isEmpty());
  }

  @Test
  public void when_clearBuffer_itWillRemoveAllItemsInternally() {
    //given
    final String value = UUID.randomUUID().toString();
    final String logicalName = "MyValue";
    this.plcMultiVarBuffer.bufferPLCChange(logicalName, value, 1);

    //when
    this.plcMultiVarBuffer.clearBuffer();

    //then
    final Map<Integer, Map<String, String>> emptyMap = this.plcMultiVarBuffer.drainBuffer();
    assertTrue(emptyMap.isEmpty());
  }
}