/*
 * @(#)SplitTransition2D.java
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

/** This splits the frame either horizontally or vertically.
 * This is also known as the "barn-door effect".
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 */
public class SplitTransition2D extends Transition2D {
	int type;
	boolean in;
	
	/** Creates a new SplitTransition2D that uses horizontal
	 * strips that grow outwards from the middle.
	 */
	public SplitTransition2D() {
		this(HORIZONTAL,false);
	}
	
	/** Creates a new SplitTransition2D
	 * 
	 * @param type must be HORIZONTAL or VERTICAL
	 * @param in whether the halves in this transition grow in (true) or out (false)
	 */
	public SplitTransition2D(int type,boolean in) {
		if(!(type==HORIZONTAL || type==VERTICAL))
			throw new IllegalArgumentException("The type must be HORIZONTAL or VERTICAL.");
		this.type = type;
		this.in = in;
	}

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		Rectangle2D rect;
		if(in)
			progress = 1-progress;
		if(type==HORIZONTAL) {
			float k = size.height/2f*progress;
			rect = new Rectangle2D.Float(
					0, size.height/2f-k,
					size.width, 2*k );
		} else {
			float k = size.width/2f*progress;
			rect = new Rectangle2D.Float(
					size.width/2f-k, 0,
					2*k, size.height );
		}
		return new ImageInstruction[] {
				new ImageInstruction(!in),
				new ImageInstruction(in,null,rect)
		};
	}
	
	public String toString() {
		if(in && type==HORIZONTAL) {
			return "Split Horizontal In";
		} else if(type==HORIZONTAL) {
			return "Split Horizontal Out";
		} else if(in && type==VERTICAL) {
			return "Split Vertical In";
		} else {
			return "Split Vertical Out";
		}
	}

}
