/**
 * Author	: YYang
 * Date		: Jul 21, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.remote.simulator.stdCrypto.bean;

import com.sicpa.standard.sicpadata.api.fieldaccess.SicpadataField;

/**
 * 
 * Class that defines attributes for signed business fields
 * 
 * @author YYang
 * 
 */
public class DescriptorBean {

	@SicpadataField("batchId")
	protected long batchId;

	@SicpadataField("codeType")
	protected long codeType;

	/**
	 * date in long format
	 */
	@SicpadataField("date")
	protected long date;

	// getter and setter

	public long getDate() {
		return date;
	}

	public void setDate(final long date) {
		this.date = date;
	}

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(final long batchId) {
		this.batchId = batchId;
	}

	public long getType() {
		return codeType;
	}

	public void setType(final long type) {
		this.codeType = type;
	}
}
