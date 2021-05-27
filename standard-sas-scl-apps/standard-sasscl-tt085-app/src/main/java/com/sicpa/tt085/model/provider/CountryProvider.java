package com.sicpa.tt085.model.provider;

import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.ICustomProperty;
import com.sicpa.tt085.model.TT085Country;

public interface CountryProvider {

    ICustomProperty<TT085Country> country = new CustomProperty<TT085Country>("country",TT085Country.class, null);
    
}
