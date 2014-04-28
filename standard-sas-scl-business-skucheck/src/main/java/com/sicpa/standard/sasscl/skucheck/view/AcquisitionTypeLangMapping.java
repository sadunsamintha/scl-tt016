package com.sicpa.standard.sasscl.skucheck.view;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.sasscl.skucheck.acquisition.GroupAcquisitionType;
import com.sicpa.standard.sasscl.skucheck.acquisition.SingleAcquisitionType;

public class AcquisitionTypeLangMapping {

	protected final static Map<Object, String> mapping;
	static {
		mapping = new HashMap<Object, String>();

		mapping.put(SingleAcquisitionType.MISSING, "sku.check.singleAcquisitionType.missing");
		mapping.put(SingleAcquisitionType.READ, "sku.check.singleAcquisitionType.read");
		mapping.put(SingleAcquisitionType.UNREADABLE, "sku.check.singleAcquisitionType.unreadable");

		mapping.put(GroupAcquisitionType.ALL_UNREAD, "sku.check.groupAcquisitionType.allUnread");
		mapping.put(GroupAcquisitionType.DIFFERENT_CODES, "sku.check.groupAcquisitionType.differentCodes");
		mapping.put(GroupAcquisitionType.MISSING, "sku.check.groupAcquisitionType.missing");
		mapping.put(GroupAcquisitionType.SINGLE_CODE, "sku.check.groupAcquisitionType.singleCode");
	}

	public static String get(Object key) {
		return mapping.get(key);
	}

	public static String getMessage(Object key) {
		String s = mapping.get(key);
		if (s == null) {
			return "<" + key + ">";
		}
		return Messages.get(s);
	}

}
