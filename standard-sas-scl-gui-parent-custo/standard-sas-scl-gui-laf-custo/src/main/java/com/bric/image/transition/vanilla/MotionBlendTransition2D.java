/*
 * @(#)MotionBlendTransition2D.java
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
import java.util.Vector;

import com.bric.geom.RectangularTransform;
import com.bric.image.transition.ImageInstruction;
import com.bric.image.transition.Transition2D;
import com.bric.image.transition.Transition2DInstruction;

/** This is a blend transition with a subtle zoom in/out added.
* 
* <P>The concept for this transition is courtesy of Tech4Learning, Inc.
*/
public class MotionBlendTransition2D extends Transition2D {

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		//the logic here is copied & pasted from Tech4Learning code:
		Vector v = new Vector();
		
		int max_wchange = size.width/4;
		int max_hchange = size.height/4;

		int x2 = (int)(-(max_wchange*(1.0f-progress)));
		int y2 = (int)(-(max_hchange*(1.0f-progress)));
		int w2 = (int)(size.width+(max_wchange*(1.0f-progress)));
		int h2 = (int)(size.height+(max_hchange*(1.0f-progress)));
		AffineTransform transform = RectangularTransform.create(
				new Rectangle(0,0,size.width,size.height),
				new Rectangle(x2,y2,w2-x2,h2-y2)
		);
		v.add(new ImageInstruction(true,1,transform,null));

		x2 = (int)(-(max_wchange*progress));
		y2 = (int)(-(max_hchange*progress));
		w2 = (int)(size.width+(max_wchange*progress));
		h2 = (int)(size.height+(max_hchange*progress));
		transform = RectangularTransform.create(
				new Rectangle(0,0,size.width,size.height),
				new Rectangle(x2,y2,w2-x2,h2-y2)
		);
		v.add(new ImageInstruction(false,1-progress,transform,null));
		
		return (ImageInstruction[])v.toArray(new ImageInstruction[v.size()]);
	}
	
	public String toString() {
		return "Motion Blend";
	}
}
