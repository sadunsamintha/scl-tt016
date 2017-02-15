package com.sicpa.tt016.provider.impl;

import com.sicpa.standard.client.common.provider.AbstractProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TT016RefeedSkuProvider extends AbstractProvider<List<Integer>> {

    public TT016RefeedSkuProvider(String refeedSkuIds) {
        super("RefeedSkuProvider");

        value = createRefeedSkuList(refeedSkuIds);
    }

    private List<Integer> createRefeedSkuList(String refeedSkuIds) {
        return Stream.of(refeedSkuIds.split(","))
                .map(String::trim)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

}
