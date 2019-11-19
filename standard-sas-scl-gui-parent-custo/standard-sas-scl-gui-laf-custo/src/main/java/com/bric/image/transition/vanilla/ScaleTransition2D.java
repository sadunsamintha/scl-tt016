/*
 * @(#)ScaleTransition2D.java
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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.bric.geom.RectangularTransform;
import com.bric.image.transition.ImageInstruction;
import com.bric.image.transition.Transition2D;
import com.bric.image.transition.Transition2DInstruction;

/** This zooms one frame in/out from the center.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 *
 */
public class ScaleTransition2D extends Transition2D {
	int type;

	/** Creates a new ScaleTransition2D that scales out */
	public ScaleTransition2D() {
		this(OUT);
	}
	
	/** Creates a new ScaleTransition2D
	 * 
	 * @param type must be IN or OUT
	 */
	public ScaleTransition2D(int type) {
		if(!(type==IN || type==OUT))
			throw new IllegalArgumentException("type must be IN or OUT");
		this.type = type;
	}

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		Point2D center = new Point2D.Float(size.width/2f, size.height/2f);
		
		AffineTransform transform;
		if(type==OUT) {
			progress = 1-progress;
		}
		
		float w = size.width*progress;
		float h = size.height*progress;
		transform = RectangularTransform.create(
				new Rectangle2D.Float(0,0,size.width,size.height),
				new Rectangle2D.Double(center.getX()-w/2,center.getY()-h/2,w,h));

		return new ImageInstruction[] {
				new ImageInstruction(type==IN),
				new ImageInstruction(type!=IN,transform,null)
		};
	}

	public String toString() {
		if(type==IN) {
			return "Scale In";
		} else {
			return "Scale Out";
		}
	}
}
