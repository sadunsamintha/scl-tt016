/*
 * @(#)BoxTransition2D.java
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
import java.awt.geom.Rectangle2D;

/** This clips to the shape of a square zooming in/out.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 */
public class BoxTransition2D extends AbstractShapeTransition2D {

	/** Creates a new BoxTransition2D that zooms out
	 */
	public BoxTransition2D() {}

	/** Creates a new BoxTransition2D
	 * 
	 * @param type must be IN or OUT
	 */
	public BoxTransition2D(int type) {
		super(type);
	}

	public Shape getShape() {
		return new Rectangle2D.Float(0,0,100,100);
	}

	public String getShapeName() {
		return "Box";
	}

}
