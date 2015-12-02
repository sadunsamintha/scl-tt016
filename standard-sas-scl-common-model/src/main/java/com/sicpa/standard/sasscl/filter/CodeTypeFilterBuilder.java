package com.sicpa.standard.sasscl.filter;


import com.sicpa.standard.sasscl.model.CodeType;

import java.util.function.Predicate;

public class CodeTypeFilterBuilder {

    private  Predicate <CodeType> filters = noFilter();  // default empty filter

    public CodeTypeFilterBuilder() {

    }

    public CodeTypeFilterBuilder addFilter(Predicate <CodeType> filter ) {
        filters =  filters.and(filter);
        return this;
    }

    public Predicate <CodeType> buildCompositeFilter() {
        return filters;
    }

    private Predicate<CodeType> noFilter(){
        return ct -> true;
    }

}
