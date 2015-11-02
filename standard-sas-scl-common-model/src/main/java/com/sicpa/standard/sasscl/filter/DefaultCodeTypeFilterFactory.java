package com.sicpa.standard.sasscl.filter;


import com.sicpa.standard.sasscl.model.CodeType;

import java.util.function.Predicate;

public class DefaultCodeTypeFilterFactory implements CodeTypeFilterFactory {

    public Predicate<CodeType> getFilter() {
        return new CodeTypeFilterBuilder().buildCompositeFilter();
    }

}
