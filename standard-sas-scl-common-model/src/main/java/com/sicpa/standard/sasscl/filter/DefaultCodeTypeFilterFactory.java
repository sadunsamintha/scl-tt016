package com.sicpa.standard.sasscl.filter;


import com.sicpa.standard.sasscl.model.CodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DefaultCodeTypeFilterFactory implements CodeTypeFilterFactory {

    private List<Long> codeTypesToFilter = new ArrayList<>();

    public Predicate<CodeType> getFilter() {
        return new CodeTypeFilterBuilder().addFilter(
                ct -> !codeTypesToFilter.contains(ct.getId())).buildCompositeFilter();
    }

    public void setCodeTypesToFilter(List<Long> codeTypesToFilter) {
        this.codeTypesToFilter = codeTypesToFilter;
    }
}
