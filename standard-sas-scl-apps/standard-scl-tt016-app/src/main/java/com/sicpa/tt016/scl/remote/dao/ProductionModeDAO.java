/*
 * Author   		: JBarbieri
 * Date     		: 20-Oct-2010
 *
 * Project  		: tt016-spl
 * Package 			: com.sicpa.tt016.spl.business.model.mode
 * File   			: ProductionModeDAO.java
 *
 * Revision 		: $Revision$
 * Last modified	: $LastChangedDate$
 * Last modified by	: $LastChangedBy$
 *
 * Copyright (c) 2010 SICPA Product Security SA, all rights reserved.
 */
package com.sicpa.tt016.scl.remote.dao;


/**
 * Production mode.
 */
public class ProductionModeDAO {

	/** Standard production mode. */
	public static final int PRODUCTION_MODE_STANDARD_ID = 0;

	/** For export */
	public static final int PRODUCTION_MODE_EXPORT_ID = 1;

	/** For maintenance */
	public static final int PRODUCTION_MODE_MAINTENANCE = 2;

	/** Not available */
	public static final int PRODUCTION_MODE_NOT_AVAILABLE = 3;

	/** Aged Wines production mode. */
	public static final int PRODUCTION_MODE_AGED_WINES = 4;

	/** Un-coded production mode. */
	public static final int PRODUCTION_MODE_UNCODED = 5;

	/** Non alcohol */
	public static final int PRODUCTION_MODE_NON_ALCOHOOL_ID = 6;

	/** Refeed production mode. */
	public static final int PRODUCTION_MODE_REFEED = 7;

	/** Off Production **/
	public static final int PRODUCTION_DOWNTIME = 99;

}
