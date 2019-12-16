/*
 * @(#)CircleTransition2D.java
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
import java.awt.geom.Ellipse2D;

/** This clips to the shape of a circle zooming in/out.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 */
public class CircleTransition2D extends AbstractShapeTransition2D {

	/** Creates a new CircleTransition2D that zooms out
	 * 
	 */
	public CircleTransition2D() {
		super();
	}

	/** Creates a new CircleTransition2D
	 * 
	 * @param type must be IN or OUT
	 */
	public CircleTransition2D(int type) {
		super(type);
	}

	public Shape getShape() {
		return new Ellipse2D.Float(0,0,100,100);
	}

	public String getShapeName() {
		return "Circle";
	}
}
