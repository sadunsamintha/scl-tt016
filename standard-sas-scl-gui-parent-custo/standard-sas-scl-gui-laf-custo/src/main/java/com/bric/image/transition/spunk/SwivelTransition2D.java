/*
 * @(#)SwivelTransition2D.java
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

import java.awt.Color;
import java.awt.geom.Point2D;

/** This transition resembles two still images on a turntable.  The table spins
 * clockwise or counter-clockwise, and the foremost image rotates to the
 * background and the new image rotates forward.
 *
 */
public class SwivelTransition2D extends AbstractPlanarTransition2D {

	int multiplier;

	/** Creates a new swivel transition that moves clockwise.
	 * 
	 */
	public SwivelTransition2D() {
		this(CLOCKWISE);
	}

	/** Creates a new swivel transition against a black background.
	 * 
	 * @param direction must be CLOCKWISE or MOVE_COUNTERCLOCKWISE.
	 */
	public SwivelTransition2D(int direction) {
		this(Color.black, direction);
	}
	
	/** Creates a new swivel transition.
	 * 
	 * @param direction must be CLOCKWISE or MOVE_COUNTERCLOCKWISE.
	 */
	public SwivelTransition2D(Color background,int direction) {
		super(background);
		if(direction==CLOCKWISE) {
			multiplier = 1;
		} else if(direction==COUNTER_CLOCKWISE) {
			multiplier = -1;
		} else {
			throw new IllegalArgumentException("The direction must be CLOCKWISE or COUNTER_CLOCKWISE");
		}
	}

	
	public String toString() {
        if(multiplier==-1) {
		return "Swivel Counterclockwise";
        }
        return "Swivel Clockwise";
	}
	
	public float getFrameAOpacity(float p) {
		if(p<.5f) {
			return 1f;
		}
		p = 1-(p-.5f)/.5f;
		p = (float)Math.sqrt(p);
		return p;
	}
	
	public float getFrameBOpacity(float p) {
		if(p>.5f)
			return 1f;
		p = p/.5f;
		p = (float)Math.pow(p, .5);
		return p;
	}

	public Point2D getFrameALocation(float p) {
		p = multiplier*p;
		return new Point2D.Double(.5*Math.cos(Math.PI*p+Math.PI/2)+.5,
				.5*Math.sin(Math.PI*p+Math.PI/2)+.5);
	}
	
	public Point2D getFrameBLocation(float p) {
		p = multiplier*p;
		return new Point2D.Double(.5*Math.cos(Math.PI*p+3*Math.PI/2)+.5,
				.5*Math.sin(Math.PI*p+3*Math.PI/2)+.5);
	}
}
