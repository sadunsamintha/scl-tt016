package com.sicpa.tt016.model;

import com.sicpa.standard.sasscl.model.Code;

/**
 * Models a code received from the camera. The code can be valid or invalid depending on the member variable isValid.
 */
public class TT016Code {

    private Code code;
    private boolean isValid;

    public TT016Code(Code code, boolean isValid) {
        this.code = code;
        this.isValid = isValid;
    }

    public Code getCode() {
        return code;
    }

    public boolean isValid() {
        return isValid;
    }
}