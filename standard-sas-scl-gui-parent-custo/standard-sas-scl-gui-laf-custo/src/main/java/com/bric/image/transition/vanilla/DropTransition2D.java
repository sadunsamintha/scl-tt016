/*
 * @(#)DropTransition2D.java
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

/** This is basically a "slide down" transition, but with a bounce at the bottom.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 */
public class DropTransition2D extends Transition2D {

	/** Creates a new DropTransition2D
	 * 
	 */
	public DropTransition2D() {}

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		float dy;
		if(progress<.8) {
			progress = progress/.8f;
			dy = -progress*progress+1;
			dy = 1-dy;
		} else {
			progress = (progress-.8f)/.2f;
			dy = -4*(progress-.5f)*(progress-.5f)+1;
			dy = 1-dy*.1f;
		}
		AffineTransform transform = AffineTransform.getTranslateInstance(0,dy*size.height-size.height);
		
		return new ImageInstruction[] {
				new ImageInstruction(true),
				new ImageInstruction(false,transform,null)
		};
	}
	
	public String toString() {
		return "Drop";
	}
}
