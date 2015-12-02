package com.sicpa.standard.sasscl.filter;


import com.sicpa.standard.sasscl.model.CodeType;
import java.util.function.Predicate;

public interface CodeTypeFilterFactory {

     Predicate<CodeType> getFilter();

}
