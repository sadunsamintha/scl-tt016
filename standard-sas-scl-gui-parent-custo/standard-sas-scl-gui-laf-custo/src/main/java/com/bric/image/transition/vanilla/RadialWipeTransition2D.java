/*
 * @(#)RadialWipeTransition2D.java
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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import com.bric.image.transition.ImageInstruction;
import com.bric.image.transition.Transition2D;
import com.bric.image.transition.Transition2DInstruction;

/** This uses clipping to sweep out a circular path, revealing the new frame.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 *
 */
public class RadialWipeTransition2D extends Transition2D {
	int type;
	
	/** Creates a new RadialTransition2D
	 * 
	 */
	public RadialWipeTransition2D() {
		this(CLOCKWISE);
	}
	
	/** Creates a new RadialTransition2D.
	 * 
	 * @param type must be CLOCKWISE or COUNTER_CLOCKWISE
	 */
	public RadialWipeTransition2D(int type) {
		if(!(type==CLOCKWISE || type==COUNTER_CLOCKWISE)) {
			throw new IllegalArgumentException("Type must be CLOCKWISE or COUNTER_CLOCKWISE.");
		}
		this.type = type;
	}

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		
		int multiplier2 = -1;
		if(type==COUNTER_CLOCKWISE) 
			multiplier2 = 1;
		//for a good time, don't make multiplier1 = 0
		int multiplier1 = 0; //multiplier2;
		int k = Math.max(size.width,size.height);
		Area area = new Area(new Arc2D.Double(new Rectangle2D.Double(size.width/2-2*k,size.height/2-2*k,k*4,k*4),
				90+multiplier1*progress*360, multiplier2*progress*360,Arc2D.PIE));
		area.intersect(new Area(new Rectangle(0,0,size.width,size.height)));
		
		return new ImageInstruction[] {
				new ImageInstruction(true),
				new ImageInstruction(false, null, area)
		};
	}
	
	public String toString() {
		if(type==CLOCKWISE) {
			return "Radial Wipe Clockwise";
		}
		return "Radial Wipe Counterclockwise";
	}

}
