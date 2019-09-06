package com.sicpa.tt080.sasscl.model;

import com.sicpa.standard.sasscl.model.ProductStatus;

public class TT080ProductStatus{

  //Core's ProductStatus Composition
  public final static ProductStatus UNREAD = ProductStatus.UNREAD;
  public final static ProductStatus NOT_AUTHENTICATED = ProductStatus.NOT_AUTHENTICATED;
  public final static ProductStatus AUTHENTICATED = ProductStatus.AUTHENTICATED;
  public final static ProductStatus TYPE_MISMATCH = ProductStatus.TYPE_MISMATCH;
  public final static ProductStatus EXPORT = ProductStatus.EXPORT;
  public final static ProductStatus MAINTENANCE = ProductStatus.MAINTENANCE;
  public final static ProductStatus SENT_TO_PRINTER_WASTED = ProductStatus.SENT_TO_PRINTER_WASTED;
  public final static ProductStatus SENT_TO_PRINTER_UNREAD = ProductStatus.SENT_TO_PRINTER_UNREAD;
  public final static ProductStatus COUNTING = ProductStatus.COUNTING;
  public final static ProductStatus OFFLINE = ProductStatus.OFFLINE;
  public final static ProductStatus REFEED = ProductStatus.REFEED;
  public final static ProductStatus INK_DETECTED = ProductStatus.INK_DETECTED;

  //Project Custom Status
  public final static ProductStatus DECLARED = new ProductStatus(101, "DECLARED");
}
