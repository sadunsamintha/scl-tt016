package com.sicpa.standard.sasscl.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {

	public static List<Long> commaDelimitedListToList(String values) {
		if (values == null || values.isEmpty()) {
			return new ArrayList<>();
		}

		return Arrays.asList(values.split(",")).stream().mapToLong(Long::parseLong).boxed().collect(
				Collectors.toList());
	}
}
