package com.sicpa.tt016.scl.model;

import java.util.List;

import com.sicpa.standard.sasscl.model.SKU;

public class MoroccoSKU extends SKU {

    private int productHeight = 0;

    public MoroccoSKU() {

    }

    public MoroccoSKU(int id, String description, List<String> barcodes) {
      super(id, description, barcodes);
    }

    public int getProductHeight() {
      return productHeight;
    }

    public void setProductHeight(int productHeight) {
      this.productHeight = productHeight;
    }
}
