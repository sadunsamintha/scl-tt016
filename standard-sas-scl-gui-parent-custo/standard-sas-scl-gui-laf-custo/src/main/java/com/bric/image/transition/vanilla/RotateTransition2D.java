/*
 * @(#)RotateTransition2D.java
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

/** This spins one frame in/out from the center.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 */
public class RotateTransition2D extends Transition2D {
	int type;
	
	/** Creates a new RotateTransition2D that rotates in.
	 * 
	 */
	public RotateTransition2D() {
		this(IN);
	}
	
	/** Creates a new RotateTransition2D
	 * 
	 * @param type must be IN or OUT
	 */
	public RotateTransition2D(int type) {
		if(!(type==IN || type==OUT)) {
			throw new IllegalArgumentException("type must be IN or OUT");
		}
		this.type = type;
	}

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		if(type==OUT) {
			progress = 1-progress;
		}
		AffineTransform transform = new AffineTransform();
		transform.translate(size.width/2,size.height/2);
		transform.scale(progress, progress);
		transform.rotate((1-progress)*6);
		transform.translate(-size.width/2,-size.height/2);
		
		return new ImageInstruction[] {
				new ImageInstruction(type==IN),
				new ImageInstruction(type!=IN,transform,null)
		};
	}

	public String toString() {
		if(type==IN) {
			return "Rotate In";
		} else {
			return "Roate Out";
		}
	}
}
