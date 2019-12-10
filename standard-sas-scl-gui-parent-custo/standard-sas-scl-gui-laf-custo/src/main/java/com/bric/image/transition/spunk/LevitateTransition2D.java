/*
 * @(#)LevitateTransition2D.java
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

/** This breaks the existing image into 6 horizontal strips, and then
 * (after a gently wobbly start) that levitate/accelerate up and out of
 * the frame, revealing the next image.
 * 
 */
public class LevitateTransition2D extends Transition2D {
	
	/** Creates a new levitate transtion */
	public LevitateTransition2D() {
	}
	
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		progress = (float)Math.pow(progress,2);
		float stripHeight = size.height/6f;
		
		Vector v = new Vector();
		for(int y = 0; y<size.height; y+=stripHeight) {
			v.add(new Rectangle2D.Float(0,y,size.width,stripHeight));
		}
		Transition2DInstruction[] instr = new Transition2DInstruction[v.size()+1];
		instr[0] = new ImageInstruction(false);
		for(int a = 0; a<v.size(); a++) {
			Rectangle2D r = (Rectangle2D)v.get(a);
			AffineTransform transform = new AffineTransform();
			float angleProgress = (float)Math.pow(progress, .6);
			float xProgress = 1.0f/(1.0f+progress);
			float k = (angleProgress)*((float)a)/((float)v.size());
			float theta = (float)(Math.PI*k/2+(progress)*Math.PI/2);
			if(theta>Math.PI/2) theta = (float)(Math.PI/2);
			float k2;
			theta = .2f+(float)(.2*Math.cos(-3*theta)); //theta*(.5f-progress);
			k2 = (1-progress);
			theta = theta*progress;
			if(a%2==0) {
				transform.rotate(theta,-size.width*(1-xProgress*xProgress*xProgress)/2,size.height*k2);
			} else {
				transform.rotate(-theta,size.width+(1-xProgress*xProgress*xProgress)*size.width/2,size.height*k2);
			}
			transform.translate(0,-progress*progress*1.5f*size.height);
			
			instr[a+1] = new ImageInstruction(true,transform,transform.createTransformedShape(r));
		}
		return instr;
	}
	
	public String toString() {
		return "Levitate";
	}
	
}
