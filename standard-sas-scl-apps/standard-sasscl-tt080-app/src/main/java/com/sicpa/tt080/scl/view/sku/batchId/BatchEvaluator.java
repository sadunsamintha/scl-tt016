package com.sicpa.tt080.scl.view.sku.batchId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

/**
 * Class with sole purpose to evaluate the state of a BatchId received, from the SAS/SCL UI.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class BatchEvaluator {

  private static final int MAX_FIELD_LENGTH = 15;
  private static final Pattern ALL_SYMBOLS_RESTRICTION_PATTERN =  Pattern.compile("^(?!-*$)[a-zA-Z0-9_\\- ]+$");


  @RequiredArgsConstructor
  @Getter
  @ToString
  public enum EvaluationStatus{
    INVALID_SIZE(false, "sku.batch.id.validation.size"),
    INVALID_FORMAT(false, "sku.batch.id.validation.format"),
    BLANK(false, "sku.batch.id.validation.blank"),
    VALID(true, "sku.batch.id.validation.valid");

    private final boolean valid;
    private final String statusCode;
  }

  public static EvaluationStatus evaluate(final String batchId){
    if (StringUtils.isBlank(batchId)){
      return EvaluationStatus.BLANK;
    }

    if(hasInvalidFormat(batchId)){
      return EvaluationStatus.INVALID_FORMAT;
    }

    if(hasInvalidBatchSize(batchId)){
      return EvaluationStatus.INVALID_SIZE;
    }
    return EvaluationStatus.VALID;
  }

  private static boolean hasInvalidFormat(String strBatchId) {
    final Matcher matcher = ALL_SYMBOLS_RESTRICTION_PATTERN.matcher(strBatchId);
    return !matcher.find();
  }

  private static boolean hasInvalidBatchSize(final String strBatchId) {
    return strBatchId.length() > MAX_FIELD_LENGTH;
  }
}
