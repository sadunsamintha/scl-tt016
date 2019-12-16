/*
 * @(#)WipeTransition2D.java
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
import java.awt.geom.Rectangle2D;

import com.bric.image.transition.ImageInstruction;
import com.bric.image.transition.Transition2D;
import com.bric.image.transition.Transition2DInstruction;

/** This is the standard "wipe" transition.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 */
public class WipeTransition2D extends Transition2D {
	int direction;

	/** Creates a wipe transition that wipes to the right.
	 * 
	 */
	public WipeTransition2D() {
		this(RIGHT);
	}

	/** Creates a wipe transition
	 * 
	 * @param direction must be LEFT, UP, DOWN or RIGHT
	 */
	public WipeTransition2D(int direction) {
		this.direction = direction;
		if(!(direction==LEFT || direction==UP ||
				direction==RIGHT || direction==DOWN))
			throw new IllegalArgumentException();
	}

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		Rectangle2D clipping = null;
		if(direction==RIGHT) {
			clipping = new Rectangle2D.Double(0,0,progress*size.width,size.height);
		} else if(direction==LEFT) {
			double x = (1-progress)*size.width;
			clipping = new Rectangle2D.Double(x,0,size.width-x,size.height);
		} else if(direction==DOWN) {
			clipping = new Rectangle2D.Double(0,0,size.width,progress*size.width);
		} else if(direction==UP) {
			double y = (1-progress)*size.height;
			clipping = new Rectangle2D.Double(0,y,size.width,size.height-y);
		}
		return new Transition2DInstruction[] {
				new ImageInstruction(true),
				new ImageInstruction(false,null,clipping)
		};
	}
	
	public String toString() {
		if(direction==RIGHT) {
			return "Wipe Right";
		} else if(direction==LEFT) {
			return "Wipe Left";
		} else if(direction==DOWN) {
			return "Wipe Down";
		} else {
			return "Wipe Up";
		}
	}
}
