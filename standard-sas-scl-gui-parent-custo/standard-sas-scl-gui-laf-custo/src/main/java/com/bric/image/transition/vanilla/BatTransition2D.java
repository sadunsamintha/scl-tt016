/*
 * @(#)BatTransition2D.java
 *
 * $Date: 2010-03-19 19:09:48 -0500 (Fri, 19 Mar 2010) $
 *
 * Copyright (c) 2009 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * http://javagraphics.blogspot.com/
 * 
 * And the latest version should be available here:
 * https://javagraphics.dev.java.net/
 */
package com.bric.image.transition.vanilla;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

/** This clips to the shape of a bat zooming in/out.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 */
public class BatTransition2D extends AbstractShapeTransition2D {


	/** Creates a BatTransition2D that zooms out.
	 * 
	 */
	public BatTransition2D() {}

	/** Creates a BatTransition2D
	 * 
	 * @param type must be IN or OUT
	 */
	public BatTransition2D(int type) {
		super(type);
	}

	public Shape getShape() {
		GeneralPath batPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		batPath.moveTo(0.0f, 2.0f);
		batPath.lineTo(2.0f, 0.0f);
		batPath.curveTo(2.0f, 1.0f, 2.0f, 1.0f, 3.0f, 1.0f);
		batPath.lineTo(3.0f, 0.0f);
		batPath.curveTo(3.5f, 1.0f, 3.5f, 1.0f, 4.0f, 0.0f);
		batPath.lineTo(4.0f, 1.0f);
		batPath.curveTo(5.0f, 1.0f, 5.0f, 1.0f, 5.0f, 0.0f);
		batPath.lineTo(7.0f, 2.0f);
		batPath.curveTo(6.5f, 1.5f, 6.5f, 1.5f, 6.0f, 2.0f);
		batPath.curveTo(5.5f, 1.5f, 5.5f, 1.5f, 5.0f, 2.0f);
		batPath.curveTo(4.5f, 1.5f, 4.5f, 1.5f, 4.0f, 2.0f);
		batPath.curveTo(3.5f, 3.0f, 3.5f, 3.0f, 3.0f, 2.0f);
		batPath.curveTo(2.5f, 1.5f, 2.5f, 1.5f, 2.0f, 2.0f);
		batPath.curveTo(1.5f, 1.5f, 1.5f, 1.5f, 1.0f, 2.0f);
		batPath.curveTo(0.5f, 1.5f, 0.5f, 1.5f, 0.0f, 2.0f);
		batPath.closePath();
		
		return batPath;
	}

	public String getShapeName() {
		return "Bat";
	}
}
