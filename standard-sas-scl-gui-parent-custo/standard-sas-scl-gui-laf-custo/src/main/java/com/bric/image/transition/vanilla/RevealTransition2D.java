/*
 * @(#)RevealTransition2D.java
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

/** This takes the current frame and slides it away to reveal the new frame
 * underneath.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 */
public class RevealTransition2D extends Transition2D {
	
	int direction;
	
	/** Creates a new RevealTransition2D that slides to the left.
	 * 
	 */
	public RevealTransition2D() {
		this(LEFT);
	}
	
	/** Creates a new RevealTransition2D
	 * 
	 * @param direction must be LEFT, RIGHT, UP or DOWN
	 */
	public RevealTransition2D(int direction) {
		if(!(direction==LEFT ||direction==RIGHT || direction==UP || direction==DOWN))
			throw new IllegalArgumentException("Direction must be LEFT, UP, RIGHT or DOWN");
		this.direction = direction;
	}

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		AffineTransform transform;
		
		if(direction==LEFT) {
			transform = AffineTransform.getTranslateInstance( -progress*size.width, 0);
		} else if(direction==RIGHT) {
			transform = AffineTransform.getTranslateInstance( progress*size.width, 0);
		} else if(direction==UP) {
			transform = AffineTransform.getTranslateInstance( 0, -progress*size.height);
		} else {
			transform = AffineTransform.getTranslateInstance( 0, progress*size.height);
		}
		
		return new ImageInstruction[] {
				new ImageInstruction(false),
				new ImageInstruction(true,transform,null)
		};
	}

	public String toString() {
		if(direction==UP) {
			return "Reveal Up";
		} else if(direction==LEFT) {
			return "Reveal Left";
		} else if(direction==RIGHT) {
			return "Reveal Right";
		} else {
			return "Reveal Down";
		}
	}
}
