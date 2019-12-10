/*
 * @(#)PushTransition2D.java
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

/** This is the standard "push" transition.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 */
public class PushTransition2D extends Transition2D {
	int type;
	
	/** Creates a push-right transition.
	 */
	public PushTransition2D() {
		this(RIGHT);
	}
	
	/** Creates a new PushTransition2D
	 * 
	 * @param type must be LEFT, RIGHT, UP or DOWN
	 */
	public PushTransition2D(int type) {
		if(!(type==RIGHT || type==LEFT || type==UP || type==DOWN)) {
			throw new IllegalArgumentException("The type must be LEFT, RIGHT, UP or DOWN");
		}
		this.type = type;
	}
	
	public Transition2DInstruction[] getInstructions(float progress,Dimension size) {
		AffineTransform transform1 = new AffineTransform();
		AffineTransform transform2 = new AffineTransform();
		
		if(type==LEFT) {
			transform2.translate(size.width*(1-progress),0);
			transform1.translate(size.width*(1-progress)-size.width,0);
		} else if(type==RIGHT) {
			transform2.translate(size.width*(progress-1),0);
			transform1.translate(size.width*(progress-1)+size.width,0);
		} else if(type==UP) {
			transform2.translate(0,size.height*(1-progress));
			transform1.translate(0,size.height*(1-progress)-size.height);
		} else {
			transform2.translate(0,size.height*(progress-1));
			transform1.translate(0,size.height*(progress-1)+size.height);
		}
		
		return new Transition2DInstruction[] {
				new ImageInstruction(true,transform1,null),
				new ImageInstruction(false,transform2,null)
		};
	}

	public String toString() {
		if(type==RIGHT) {
			return "Push Right";
		} else if(type==LEFT) {
			return "Push Left";
		} else if(type==DOWN) {
			return "Push Down";
		} else {
			return "Push Up";
		}
	}
}
