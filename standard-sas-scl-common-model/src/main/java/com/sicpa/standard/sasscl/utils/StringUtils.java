package com.sicpa.standard.sasscl.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.*;

public class StringUtils {

	public static List<Long> commaDelimitedListToList(String values) {
		if (values == null || values.isEmpty()) {
			return Collections.emptyList();
		}

		return asList(values.split(",")).stream().mapToLong(Long::parseLong).boxed().collect(Collectors.toList());
	}
}
