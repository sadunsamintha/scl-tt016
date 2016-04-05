/*
 * Author   : S.Queva, R.Maio
 * Date     : 20/07/2006
 *
 * Project  : SAS_Skeleton
 * Package  : com.sicpa.ttxxx.spl
 * File     : CodeDAO
 *
 * Revision : $Id$
 *
 * Copyright (c) 2004 SICPA Product Security SA, all rights reserved.
 */

package com.sicpa.tt016.scl.remote.dao;

import java.io.Serializable;

/**
 * Code DAO.
 * 
 * @author S.Queva, R.Maio
 * 
 */

public class CodeDAO implements Serializable {

	@Override
	public String toString() {
		return "CodeDAO [sequence=" + sequence + ", batchId=" + batchId + ", encryptedCode=" + encryptedCode
				+ ", status=" + status + ", type=" + type + "]";
	}

	/** Serializable */
	private static final long serialVersionUID = 1L;

	// ~ Code statuses ---------------------------------------------------------

	/** Code has not been initialized yet **/
	public static final int CODE_NOT_INITIALIZED = -1;

	/** Code not authenticated (or it's invalid) */
	public static final int CODE_NOT_AUTHENTICATED = 0;

	/** Code is authenticated (valid) */
	public static final int CODE_AUTHENTICATED = 1;

	/** Code is unread, e.g new product. */
	public static final int CODE_UNREAD = 2;

	/** Code type is mismatched. */
	public static final int CODE_TYPE_MISMATCH = 3;

	/** Code good but ejected by producer. */
	public static final int CODE_AUTHENTICATED_EJECTED = 4;

	/** Code bad but not ejected. */
	public static final int CODE_NOT_AUTHENTICATED_NOT_EJECTED = 5;

	/** Code not present. */
	public static final int CODE_NOT_PRESENT = 6;

	/** Any type */
	public static final long TYPE_ANY = -2;

	/** Type not initialized **/
	public static final int TYPE_NOT_INITIALIZED = -1;

	/** used to reset all values **/
	public static final int VALUE_NOT_INITIALIZED = -1;

	/**
	 * Sequence value
	 */
	private long sequence = VALUE_NOT_INITIALIZED;

	/**
	 * Batch ID
	 */
	private long batchId = VALUE_NOT_INITIALIZED;

	/**
	 * Encrypted Code
	 */
	private String encryptedCode = null;

	/**
	 * Code status
	 */
	private int status = CODE_NOT_INITIALIZED;

	/**
	 * Code Type
	 */
	private long type = TYPE_NOT_INITIALIZED;

	/**
	 * Constructor.
	 */
	public CodeDAO() {
	}

	/**
	 * Create a CodeDAO with sequence and batch id.
	 * 
	 * @param sequence
	 * @param batchId
	 */
	public CodeDAO(long sequence, int batchId) {
		type = TYPE_NOT_INITIALIZED;
		this.sequence = sequence;
		this.batchId = batchId;
	}

	/**
	 * Create a CodeDAO with code.
	 * 
	 * @param code
	 */
	public CodeDAO(String code) {
		type = TYPE_NOT_INITIALIZED;
		this.encryptedCode = code;
	}

	/**
	 * Get Code status
	 * 
	 * @return code status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set Code status
	 * 
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return batch id.
	 */
	public long getBatchId() {
		return batchId;
	}

	/**
	 * Set batch id.
	 * 
	 * @param batchId
	 */
	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	/**
	 * @return sequence
	 */
	public long getSequence() {
		return sequence;
	}

	/**
	 * @param sequence
	 */
	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return encrypted code
	 */
	public String getEncryptedCode() {
		return encryptedCode;
	}

	/**
	 * 
	 * @param encryptedCode
	 */
	public void setEncryptedCode(String encryptedCode) {
		this.encryptedCode = encryptedCode;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(long type) {
		this.type = type;
	}

	/**
	 * @return code type.
	 */

	public long getType() {
		return type;
	}

	/**
	 * 
	 * @param codeType
	 * @return true if type matched, false otherwise.
	 */
	public boolean isExpectedType(int codeType) {
		if ((type == TYPE_ANY) || (type == codeType)) {
			return true;
		}
		return false;
	}
}
