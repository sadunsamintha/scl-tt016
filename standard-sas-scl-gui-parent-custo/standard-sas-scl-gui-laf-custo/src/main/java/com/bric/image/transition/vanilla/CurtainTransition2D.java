/*
 * @(#)CurtainTransition2D.java
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
import java.awt.geom.Rectangle2D;

import com.bric.geom.RectangularTransform;
import com.bric.image.transition.ImageInstruction;
import com.bric.image.transition.Transition2D;
import com.bric.image.transition.Transition2DInstruction;

/** This splits a frame down the middle and squishes the two left and
 * right halves, as if a curtain is being opened.
 * 
 * <P>This is basically a "Split Vertical", except the two halves are squished.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 *
 */
public class CurtainTransition2D extends Transition2D {

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		
		progress = 1-progress;
		
		Rectangle2D rect1 = new Rectangle2D.Double(
			0,0,
			size.width/2f*progress, size.height
		);
		
		Rectangle2D rect2 = new Rectangle2D.Double(
				size.width-rect1.getWidth(), 0,
				rect1.getWidth(), rect1.getHeight()
			);
		
		AffineTransform transform1 = RectangularTransform.create(
			new Rectangle2D.Float(0,0, size.width/2f, size.height),
			rect1
		);

		AffineTransform transform2 = RectangularTransform.create(
			new Rectangle2D.Float(size.width/2f,0, size.width/2f, size.height),
			rect2
		);
		
		return new Transition2DInstruction[] {
				new ImageInstruction(false),
				new ImageInstruction(true,transform1,rect1),
				new ImageInstruction(true,transform2,rect2)
		};
	}
	
	public String toString() {
		return "Curtain";
	}

}
