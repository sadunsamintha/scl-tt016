package com.sicpa.tt016.model;

import com.sicpa.standard.sasscl.model.Product;

/**
 * Models a result received by camera. The result can either be a TT016Code (tuple - code + is code valid) or a
 * product resulting from a bad code. Both tuple and product are mutually exclusive (i.e. both can't be set at the
 * same time)
 */
public class CameraResult {

    private Product product;
    private TT016Code code;

    public CameraResult(Product product) {
        this.product = product;
    }

    public CameraResult(TT016Code code) {
        this.code = code;
    }

    public Product getProduct() {
        return product;
    }

    public TT016Code getCode() {
        return code;
    }
}
