/*
 * @(#)ZoomTransition2D.java
 *
 * $Date: 2010-03-19 19:07:56 -0500 (Fri, 19 Mar 2010) $
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
package com.bric.image.transition.spunk;

import java.awt.geom.Point2D;

/** In this 3D-ish transition the 2 frames slide forward, as if the viewer
 * is at the end of a conveyer belt.
 */
public class ZoomTransition2D extends AbstractPlanarTransition2D {

	int multiplier;
	
	/** Creates a zoom transition that moves to the right */
	public ZoomTransition2D() {
		this(RIGHT);
	}
	
	/** Creates a zoom transition.
	 * 
	 * @param direction move be RIGHT or MOVE_LEFT.
	 */
	public ZoomTransition2D(int direction) {
		if(direction==RIGHT) {
			multiplier = 1;
		} else if(direction==LEFT) {
			multiplier = -1;
		} else {
			throw new IllegalArgumentException("The direction must be LEFT or RIGHT");
		}
	}
	
	public float getFrameBOpacity(float p) {
		return .5f+.5f*p;
	}
	
	public float getFrameAOpacity(float p) {
		return 1;
	}

	public Point2D getFrameBLocation(float p) {
		double y = p;
		double x = .5-multiplier*(1-p);
		return new Point2D.Double(x,y);
	}

	public Point2D getFrameALocation(float p) {
		double y = p+1;
		double x = .5+multiplier*(p);
		return new Point2D.Double(x,y);
	}

	
	public String toString() {
        if(multiplier==1) {
            return "Zoom Right";
        } else {
            return "Zoom Left";
        }
	}
}
