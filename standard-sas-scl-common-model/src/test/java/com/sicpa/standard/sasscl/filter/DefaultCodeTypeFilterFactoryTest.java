package com.sicpa.standard.sasscl.filter;


import com.sicpa.standard.sasscl.model.CodeType;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultCodeTypeFilterFactoryTest {

    @Test
    public void defaultFilter() {

        DefaultCodeTypeFilterFactory factory = new DefaultCodeTypeFilterFactory();
        factory.setCodeTypesToFilter(Arrays.stream(new long[] {1L, 3L}).boxed().collect(Collectors.toList()));

        List<CodeType> codeTypes = new ArrayList<>();
        codeTypes.add(new CodeType(1L));
        codeTypes.add(new CodeType(2L));

        Set<CodeType> codeTypesFiltered = codeTypes.stream()
                .filter(factory.getFilter()).collect(Collectors.toSet());

        Assert.assertEquals(1, codeTypesFiltered.size());
    }

    @Test
    public void defaultFilter_noCodeTypeToFilter() {

        DefaultCodeTypeFilterFactory factory = new DefaultCodeTypeFilterFactory();
        factory.setCodeTypesToFilter(new ArrayList<>());

        List<CodeType> codeTypes = new ArrayList<>();
        codeTypes.add(new CodeType(1L));
        codeTypes.add(new CodeType(2L));

        Set<CodeType> codeTypesFiltered = codeTypes.stream()
                .filter(factory.getFilter()).collect(Collectors.toSet());

        Assert.assertEquals(codeTypes.size(), codeTypesFiltered.size());
    }
}
