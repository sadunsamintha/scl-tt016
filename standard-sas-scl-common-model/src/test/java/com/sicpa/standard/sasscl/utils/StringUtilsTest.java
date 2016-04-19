package com.sicpa.standard.sasscl.utils;

import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

public class StringUtilsTest {

	@Test
	public void commaDelimitedListToList_null() {
		Assert.assertTrue(StringUtils.commaDelimitedListToList(null).size() == 0);
	}

	@Test
	public void commaDelimitedListToList_empty() {
		Assert.assertTrue(StringUtils.commaDelimitedListToList("").size() == 0);
	}

	@Test
	public void commaDelimitedListToList_singleValue() {
		Assert.assertTrue(StringUtils.commaDelimitedListToList("1").get(0) == 1L);
	}

	@Test
	public void commaDelimitedListToList_multipleValue() {
		List<Long> values = StringUtils.commaDelimitedListToList("1,2,3,4");

		Assert.assertEquals(1, values.get(0).longValue());
		Assert.assertEquals(2, values.get(1).longValue());
		Assert.assertEquals(3, values.get(2).longValue());
		Assert.assertEquals(4, values.get(3).longValue());
	}
}
