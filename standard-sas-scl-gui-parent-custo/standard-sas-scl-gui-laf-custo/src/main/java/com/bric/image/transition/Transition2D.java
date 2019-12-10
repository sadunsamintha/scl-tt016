/*
 * @(#)Transition2D.java
 *
 * $Date: 2010-03-19 19:04:19 -0500 (Fri, 19 Mar 2010) $
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
package com.bric.image.transition;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


/** This uses Java2D operations to transition between two images.
 * Each operation is wrapped up in a Transition2DInstruction object.
 * 
 */
public abstract class Transition2D implements Transition {

	/** This determines how to transition from A to B.
	 * @param progress a float from [0,1].  When this is zero, these instructions should
	 * render the initial frame.  When this is one, these instructions should render
	 * the final frame.
	 *
	 */
	public abstract Transition2DInstruction[] getInstructions(float progress,Dimension size);

	/** This calls Transition2DInstruction.paint() for each instruction
	 * in this transition.
	 * <P>It is made final to reinforce that the instructions should be
	 * all that is needed to implement these transitions.
	 */
	public final void paint(Graphics2D g,BufferedImage frameA,BufferedImage frameB,float progress) {
		Transition2DInstruction[] i = getInstructions(progress,new Dimension(frameA.getWidth(),frameA.getHeight()));
		for(int a = 0; a<i.length; a++) {
			i[a].paint(g, frameA, frameB);
		}
	}
}
