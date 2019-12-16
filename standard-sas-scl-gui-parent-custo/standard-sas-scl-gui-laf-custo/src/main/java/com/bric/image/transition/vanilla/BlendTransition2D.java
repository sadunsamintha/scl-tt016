/*
 * @(#)BlendTransition2D.java
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

import com.bric.image.transition.ImageInstruction;
import com.bric.image.transition.Transition2D;
import com.bric.image.transition.Transition2DInstruction;

/** Also know as the "Fade" transition, this simply fades in the opacity of the incoming
 * frame.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 *
 */
public class BlendTransition2D extends Transition2D {

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		return new Transition2DInstruction[] {
				new ImageInstruction(true),
				new ImageInstruction(false,progress,null,null)
		};
	}
	
	public String toString() {
		return "Blend";
	}

}
