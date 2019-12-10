/*
 * @(#)MeshShuffleTransition2D.java
 *
 * $Date: 2010-03-19 19:07:56 -0500 (Fri, 19 Mar 2010) $
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
package com.bric.image.transition.spunk;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import com.bric.image.transition.ImageInstruction;
import com.bric.image.transition.Transition2D;
import com.bric.image.transition.Transition2DInstruction;

/** The concept here was to resemble a deck of cards being shuffled.
 * It doesn't really remind me of cards, though, but that may be the
 * best description I can think of.
 *
 */
public class MeshShuffleTransition2D extends Transition2D {

	/** Creates a new mesh shuffle transition. */
	public MeshShuffleTransition2D() {
	}
	
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		progress = (float)Math.pow(progress,.45);
		float stripHeight = size.height*10/200;
		
		Vector v = new Vector();
		for(int y = size.height; y>-stripHeight; y-=stripHeight) {
			v.add(new Rectangle2D.Float(0,y,size.width,stripHeight));
		}
		Transition2DInstruction[] instr = new Transition2DInstruction[v.size()];
		instr[0] = new ImageInstruction(true);
		for(int a = 1; a<v.size(); a++) {
			Rectangle2D r = (Rectangle2D)v.get(a);
			AffineTransform transform = new AffineTransform();
			float k = (1-progress)*((float)a)/((float)v.size());
			float theta = (float)(Math.PI*k/2+(1-progress)*Math.PI/2);
			if(theta>Math.PI/2) theta = (float)(Math.PI/2);
			if(a%2==0) {
				transform.rotate(-theta,-size.width*(1-progress)/2,size.height*progress);
			} else {
				transform.rotate(theta,size.width+(1-progress)*size.width/2,size.height*progress);
			}
			instr[a] = new ImageInstruction(false,transform,transform.createTransformedShape(r));
		}
		return instr;
	}
	
	public String toString() {
		return "Mesh Shuffle";
	}
	
}
