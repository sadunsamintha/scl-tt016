/*
 * @(#)StarTransition2D.java
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

/** This clips to the shape of a star zooming in/out.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 */
public class StarTransition2D extends AbstractShapeTransition2D {

	/** Creates a new StarTransition2D that zooms out.
	 * 
	 */
	public StarTransition2D() {
		super();
	}

	/** Creates a new StarTransition2D.
	 * 
	 * @param type must be IN or OUT
	 */
	public StarTransition2D(int type) {
		super(type);
	}

	public Shape getShape() {
		GeneralPath p = new GeneralPath();
		double angle = Math.PI/10;
		float r2 = 2.5f;
		double k = Math.PI*2/10;
		p.moveTo( (float)(Math.cos(angle)) , (float)(Math.sin(angle)) );
		for(int a = 0; a<5; a++) {
			p.lineTo( (float)(r2*Math.cos(angle+k)), (float)(r2*Math.sin(angle+k)) );
			angle+= Math.PI*2.0/5.0;
			p.lineTo( (float)(Math.cos(angle)), (float)(Math.sin(angle)) );
		}
		p.closePath();
		return p;
	}
	
	public String getShapeName() {
		return "Star";
	}

}
