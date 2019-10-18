package com.sicpa.tt080.scl.view.sku.batchId;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ProductionBatch {

  private final String batchId;
  private final int skuId;
  private final String productionBatchId;

  public static final ProductionBatch EMPTY = new ProductionBatch();

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuuMMddHHmmss")
      .withLocale(Locale.getDefault())
      .withZone(ZoneId.of("UTC"));

  public ProductionBatch(final String batchId, final int skuId) {
    this.batchId = batchId;
    this.skuId = skuId;
    this.productionBatchId = String.format("%s_%s_%s", FORMATTER.format(Instant.now()), this.skuId, this.batchId);
  }

  ProductionBatch(){
    this.batchId = "";
    this.skuId = 0;
    this.productionBatchId = "";
  }

  public String getBatchId() {
    return batchId;
  }

  public int getSkuId() {
    return skuId;
  }

  public String getProductionBatchId(){
    return productionBatchId;
  }
}
