/*
 * @(#)BlindsTransition2D.java
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
import java.util.Vector;

import com.bric.image.transition.ImageInstruction;
import com.bric.image.transition.Transition2D;
import com.bric.image.transition.Transition2DInstruction;

/** Also known as "Venetian Blinds", this creates several horizontal/vertical
 * strips that grow in width/height respectively to reveal the new frame.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 *
 */
public class BlindsTransition2D extends Transition2D {
	int type;
	int blinds;
	
	/** Creates a BlindsTransition2D that moves to the right with 10 blinds.
	 * 
	 */
	public BlindsTransition2D() {
		this(RIGHT);
	}
	
	/** Creates a new BlindsTransition2D with 10 blinds.
	 * 
	 * @param type must be LEFT, RIGHT, UP or DOWN.
	 */
	public BlindsTransition2D(int type) {
		this(type,10);
	}
	
	/** Creates a BlindsTransition2D.
	 * 
	 * @param type must be LEFT, RIGHT, UP or DOWN
	 * @param numberOfBlinds the number of blinds.  Must be 4 or greater.
	 */
	public BlindsTransition2D(int type,int numberOfBlinds) {
		if(!(type==LEFT || type==RIGHT || type==UP || type==DOWN)) {
			throw new IllegalArgumentException("The type must be LEFT, RIGHT, UP or DOWN");
		}
		if(numberOfBlinds<4)
			throw new IllegalArgumentException("The number of blinds ("+numberOfBlinds+") must be greater than 3.");
		this.type = type;
		blinds = numberOfBlinds;
	}

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		Vector v = new Vector();
		v.add(new ImageInstruction(type==RIGHT || type==DOWN));
		float k;
		if(type==LEFT || type==RIGHT) {
			k = ((float)size.width)/((float)blinds);
		} else {
			k = ((float)size.height)/((float)blinds);
		}
		for(int a = 0; a<blinds; a++) {
			Rectangle2D r;
			if(type==DOWN) {
				r = new Rectangle2D.Float(0,a*k,size.width,progress*k);
			} else if(type==UP) {
				r = new Rectangle2D.Float(0,a*k,size.width,k-progress*k);
			} else if(type==RIGHT) {
				r = new Rectangle2D.Float(a*k,0,progress*k,size.height);
			} else {
				r = new Rectangle2D.Float(a*k,0,k-progress*k,size.height);
			}
			v.add(new ImageInstruction(type==UP || type==LEFT,null,r));
		}
		return (Transition2DInstruction[])v.toArray(new Transition2DInstruction[v.size()]);
	}

	public String toString() {
		if(type==LEFT) {
			return "Blinds Left";
		} else if(type==RIGHT) {
			return "Blinds Right";
		} else if(type==UP) {
			return "Blinds Up";
		} else {
			return "Blinds Down";
		}
	}
}
