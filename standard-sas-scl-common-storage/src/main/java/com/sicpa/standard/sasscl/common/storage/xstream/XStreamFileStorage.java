package com.sicpa.standard.sasscl.common.storage.xstream;

import com.sicpa.standard.client.common.utils.ConfigUtils;

public class XStreamFileStorage extends com.sicpa.standard.client.common.storage.impl.XStreamFileStorage {

	public XStreamFileStorage() {
		// use the xstream common for all, so can use a common config mechanism
		xstream = ConfigUtils.getXStream();
	}

}
