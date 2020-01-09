package com.sicpa.tt080.scl.view.sku.batchId;

import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class BatchEvaluatorTest {

  String batchId;
  BatchEvaluator.EvaluationStatus expectedStatus;

  public BatchEvaluatorTest(String batchId, BatchEvaluator.EvaluationStatus expectedStatus) {
    this.batchId = batchId;
    this.expectedStatus = expectedStatus;
  }

  @Parameters()
  public static Iterable<Object[]> data() throws Throwable{
    return Arrays.asList(new Object[][]{
        //NULL, EMPTY, WHITE SPACES ONLY
        {null, BatchEvaluator.EvaluationStatus.BLANK},
        {"", BatchEvaluator.EvaluationStatus.BLANK},
        {"   ", BatchEvaluator.EvaluationStatus.BLANK},

        //SIZE
        {"242453466345634546465", BatchEvaluator.EvaluationStatus.INVALID_SIZE},
        {"TATFCHNVUSDBDYERBNFYERBDY", BatchEvaluator.EvaluationStatus.INVALID_SIZE},

        //FORMAT
        {"EFE$%", BatchEvaluator.EvaluationStatus.INVALID_FORMAT},
        {"^&AC1", BatchEvaluator.EvaluationStatus.INVALID_FORMAT},
        {"-----------", BatchEvaluator.EvaluationStatus.INVALID_FORMAT},

        //VALID
        {"124563", BatchEvaluator.EvaluationStatus.VALID},
        {"ADSV", BatchEvaluator.EvaluationStatus.VALID},
        {"AV2EG1", BatchEvaluator.EvaluationStatus.VALID},
        {"AV2-EG1", BatchEvaluator.EvaluationStatus.VALID},
        {"AV2_EG1", BatchEvaluator.EvaluationStatus.VALID},
        {"-AV2-EG1", BatchEvaluator.EvaluationStatus.VALID},
        {"AV2-EG1-", BatchEvaluator.EvaluationStatus.VALID},
        {"-AV2-EG1-", BatchEvaluator.EvaluationStatus.VALID},
        {"_AV2_EG1", BatchEvaluator.EvaluationStatus.VALID},
        {"AV2_EG1_", BatchEvaluator.EvaluationStatus.VALID},
        {"_AV2_EG1_", BatchEvaluator.EvaluationStatus.VALID},
        {" AETV", BatchEvaluator.EvaluationStatus.VALID},
        {"AETV ", BatchEvaluator.EvaluationStatus.VALID},
        {" AETV ", BatchEvaluator.EvaluationStatus.VALID}
    });
  }

  @Test
  public void evaluate_Returns_CorrectEvaluationStatus(){
    //when
    final BatchEvaluator.EvaluationStatus actualStatus = BatchEvaluator.evaluate(this.batchId);

    //then
    assertThat(actualStatus, is(this.expectedStatus));
  }
}