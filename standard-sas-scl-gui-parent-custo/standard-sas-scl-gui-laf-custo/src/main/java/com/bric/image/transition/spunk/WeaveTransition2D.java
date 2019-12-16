/*
 * @(#)WeaveTransition2D.java
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

/** This paints the incoming frame in thin horizontal strips that slide
 * into place.
 * 
 */
public class WeaveTransition2D extends Transition2D {

	/** Creates a new weave transition */
	public WeaveTransition2D() {
	}
	
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		float stripHeight = 5;
		progress = (float)(-1.6666666666666186*progress*progress+2.6666666666666203*progress);
		
		
		float progress2 = (float)Math.pow(1-progress,3)*.5f+(1-progress)*.5f;
		if(progress>1)
			progress2 = (float)Math.pow(1-progress,2);
		float dip = -(2*progress-1)*(2*progress-1)+1;
		Vector v = new Vector();
		for(int y = size.height; y>-stripHeight; y-=stripHeight) {
			v.add(new Rectangle2D.Float(0,y,size.width,stripHeight));
		}
		Transition2DInstruction[] instr = new Transition2DInstruction[v.size()+1];
		instr[0] = new ImageInstruction(true);
		for(int a = 0; a<v.size(); a++) {
			Rectangle2D r = (Rectangle2D)v.get(a);
			AffineTransform transform = new AffineTransform();
			float dx = (float)(Math.sin(.5*Math.PI*(1-progress))*size.width);
			float k = (progress2)*(1000*dip)*((float)a)/((float)v.size());
			dx = dx+k;
			if(a%2==0) {
				transform.translate(dx,0);
			} else {
				transform.translate(-dx,0);
			}
			instr[a+1] = new ImageInstruction(false,transform,transform.createTransformedShape(r));
		}
		return instr;
	}
	
	public String toString() {
		return "Weave";
	}
	
}
