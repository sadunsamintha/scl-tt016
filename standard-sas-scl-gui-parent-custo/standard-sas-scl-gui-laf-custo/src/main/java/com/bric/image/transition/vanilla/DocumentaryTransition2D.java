/*
 * @(#)DocumentaryTransition2D.java
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
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import com.bric.geom.RectangularTransform;
import com.bric.image.transition.ImageInstruction;
import com.bric.image.transition.Transition2D;
import com.bric.image.transition.Transition2DInstruction;

/** This is a combination of zooming and panning.  It is reminiscent of PBS documentaries.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 */
public class DocumentaryTransition2D extends Transition2D {
	int type;
	
	/** Creates a DocumentaryTransition2D that pans to the right.
	 * 
	 */
	public DocumentaryTransition2D() {
		this(RIGHT);
	}
	
	/** Creates a new DocumentaryTransition2D
	 * 
	 * @param type must be LEFT, RIGHT, UP or DOWN
	 */
	public DocumentaryTransition2D(int type) {
		if(!(type==RIGHT || type==LEFT || type==UP || type==DOWN)) {
			throw new IllegalArgumentException("Type must be LEFT, RIGHT, UP or DOWN.");
		}
		this.type = type;
	}

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {

		Rectangle r1 = new Rectangle(0,0,size.width,size.height);
		float k1 = .6f;
		float k2 = .95f-k1;
		float k3 = (1-k1)/2;
		

		float cutOff = .4f;
		Rectangle2D r2;
		if(type==RIGHT) {
			r2 = new Rectangle2D.Float(k2*r1.width,
					k3*r1.height,
					k1*r1.width,
					k1*r1.height);
		} else if(type==LEFT) {
			r2 = new Rectangle2D.Float(.05f*r1.width,
					k3*r1.height,
					k1*r1.width,
					k1*r1.height);
		} else if(type==DOWN) {
			r2 = new Rectangle2D.Float(k3*r1.width,
					k2*r1.height,
					k1*r1.width,
					k1*r1.height);
		} else {  //up
			r2 = new Rectangle2D.Float(k3*r1.width,
					.05f*r1.height,
					k1*r1.width,
					k1*r1.height);
		}
		float zoomProgress;
		float panProgress = (float)(.5+.5*Math.sin(Math.PI*(progress*progress-.5)));
		if(progress<cutOff) {
			//we're zooming in
			zoomProgress = progress/cutOff;
		} else {
			zoomProgress = 1;
		}

		Rectangle2D r3 = new Rectangle2D.Float( 
			(float)(r2.getX()*(1-panProgress)+r1.getX()*panProgress),
			(float)(r2.getY()*(1-panProgress)+r1.getY()*panProgress),
			(float)(r2.getWidth()*(1-panProgress)+r1.getWidth()*panProgress),
			(float)(r2.getHeight()*(1-panProgress)+r1.getHeight()*panProgress)
		);
		
		Vector v = new Vector();
		
		AffineTransform t = RectangularTransform.create(r3,r1);
		v.add(new ImageInstruction(false,t,r1));
		
		if(zoomProgress!=1) {
			v.add(new ImageInstruction(true,1-zoomProgress));
		}
		
		return (Transition2DInstruction[])v.toArray(new Transition2DInstruction[v.size()]);
	}
	
	public String toString() {
		if(type==LEFT)
			return "Documentary Left";
		if(type==RIGHT)
			return "Documentary Right";
		if(type==UP)
			return "Documentary Up";
		return "Documentary Down";
	}

}
