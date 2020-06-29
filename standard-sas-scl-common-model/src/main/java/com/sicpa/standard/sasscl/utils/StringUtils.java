package com.sicpa.standard.sasscl.utils;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;

public class StringUtils {

	public static List<Long> commaDelimitedListToList(String values) {
		if (values == null || values.isEmpty()) {
			return Collections.emptyList();
		}

		return asList(values.split(",")).stream().mapToLong(Long::parseLong).boxed().collect(toList());
	}

	public static String convertToUTF8(String str) {
		return new String(str.getBytes(ISO_8859_1), UTF_8);
	}
}
