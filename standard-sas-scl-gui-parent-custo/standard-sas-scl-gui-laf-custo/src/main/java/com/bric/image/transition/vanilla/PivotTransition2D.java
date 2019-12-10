/*
 * @(#)PivotTransition2D.java
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
import java.awt.geom.AffineTransform;

import com.bric.image.transition.ImageInstruction;
import com.bric.image.transition.Transition2D;
import com.bric.image.transition.Transition2DInstruction;

/** This pivots a frame in/out from a specific corner, as if there
 * is a hinge involved.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 */
public class PivotTransition2D extends Transition2D {
	
	boolean in;
	int type;
	
	/** Creates a new PivotTransition2D that pivots in from the top-left corner.
	 * 
	 */
	public PivotTransition2D() {
		this(TOP_LEFT, true);
	}
	
	/** Creates a new PivotTransition2D.
	 * 
	 * @param type must be TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT or BOTTOM_RIGHT
	 * @param in whether the incoming frame is pivoting in on top of the old frame, or
	 * whether the old frame is pivoting out revealing the new frame.
	 */
	public PivotTransition2D(int type, boolean in) {
		if(!(type==TOP_LEFT || type==TOP_RIGHT || type==BOTTOM_LEFT || type==BOTTOM_RIGHT))
			throw new IllegalArgumentException("Type must be TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT or BOTTOM_RIGHT");
		this.type = type;
		this.in = in;
	}

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		AffineTransform transform;
		if(in) {
			if(type==TOP_LEFT) {
				transform = AffineTransform.getRotateInstance( (float)(-(1-progress)*Math.PI/2f), 0, 0);
			} else if(type==TOP_RIGHT) {
				transform = AffineTransform.getRotateInstance( (float)((1-progress)*Math.PI/2f), size.width, 0);
			} else if(type==BOTTOM_LEFT) {
				transform = AffineTransform.getRotateInstance( (float)((1-progress)*Math.PI/2f), 0, size.height);
			} else {
				transform = AffineTransform.getRotateInstance( (float)((1-progress)*Math.PI/2f), size.width, size.height);
			}
			return new Transition2DInstruction[] {
					new ImageInstruction(true),
					new ImageInstruction(false, transform, null)
			};
		}

		//pivot out:
		if(type==TOP_LEFT) {
			transform = AffineTransform.getRotateInstance( (float)((progress)*Math.PI/2f), 0, 0);
		} else if(type==TOP_RIGHT) {
			transform = AffineTransform.getRotateInstance( (float)(-(progress)*Math.PI/2f), size.width, 0);
		} else if(type==BOTTOM_LEFT) {
			transform = AffineTransform.getRotateInstance( (float)(-(progress)*Math.PI/2f), 0, size.height);
		} else {
			transform = AffineTransform.getRotateInstance( (float)(-(progress)*Math.PI/2f), size.width, size.height);
		}
		return new Transition2DInstruction[] {
				new ImageInstruction(false),
				new ImageInstruction(true, transform, null)
		};
	}
	
	public String toString() {
		String s;
		if(in) {
			s = "Pivot In ";
		} else {
			s = "Pivot Out ";
		}
		if(type==TOP_LEFT) {
			return s+"Top Left";
		} else if(type==TOP_RIGHT) {
			return s+"Top Right";
		} else if(type==BOTTOM_LEFT) {
			return s+"Bottom Left";
		} else {
			return s+"Bottom Right";
		}
	}

}
