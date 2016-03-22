package com.sicpa.tt018.scl.model.utilities;

import com.sicpa.standard.sasscl.model.Code;

public class AlbaniaModelUtilities
{

    public static boolean isEmpty(final Code code)
    {
        return code == null || code.getStringCode() == null || code.getStringCode().isEmpty();
    }

}

