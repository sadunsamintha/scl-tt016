/*
 * @(#)DiamondsTransition2D.java
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
import java.awt.geom.GeneralPath;

import com.bric.image.transition.ImageInstruction;
import com.bric.image.transition.Transition2D;
import com.bric.image.transition.Transition2DInstruction;

/** This creates a pattern of growing diamonds.  (The new frame is clipped
 * to these diamonds.)
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 */
public class DiamondsTransition2D extends Transition2D {
	
	int diamondSize;
	
	/** Creates a new DiamondsTransition2D with a diamond size of 50.
	 * 
	 */
	public DiamondsTransition2D() {
		this(50);
	}
	
	/** Creates a new DiamondsTransition2D.
	 * 
	 * @param diamondSize the width of the diamonds.
	 * It is not recommended that this value is less than 40, as that
	 * can really hurt performance in some situations.
	 */
	public DiamondsTransition2D(int diamondSize) {
		if(diamondSize<=0)
			throw new IllegalArgumentException("size ("+diamondSize+") must be greater than 4");
		this.diamondSize = diamondSize;
	}
	
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		
		GeneralPath clipping = new GeneralPath(GeneralPath.WIND_NON_ZERO);
		
		float dx = size.width/2f;
		float dy = size.height/2f;
		while(dx>0+diamondSize) dx-=diamondSize;
		while(dy>0+diamondSize) dy-=diamondSize;
		
		int ctr = 0;
		progress = progress/2f;
		for(float y = -dy; y<size.height+diamondSize; y+=diamondSize/2) {
			float z = 0;
			if(ctr%2==0) 
				z = diamondSize/2f;
			
			for(float x = -dx; x<size.width+diamondSize; x+=diamondSize) {
				clipping.moveTo(x+z, y-diamondSize*progress);
				clipping.lineTo(x+diamondSize*progress+z, y);
				clipping.lineTo(x+z, y+diamondSize*progress);
				clipping.lineTo(x-diamondSize*progress+z, y);
				clipping.lineTo(x+z, y-diamondSize*progress);
				clipping.closePath();
			}
			ctr++;
		}

		return new Transition2DInstruction[] {
				new ImageInstruction(true),
				new ImageInstruction(false,null,clipping)
		};
	}
	
	
	public String toString() {
		return "Diamonds ("+diamondSize+")";
	}

}
