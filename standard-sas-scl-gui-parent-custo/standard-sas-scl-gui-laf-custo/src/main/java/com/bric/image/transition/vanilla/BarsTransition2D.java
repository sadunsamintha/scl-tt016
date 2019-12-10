/*
 * @(#)BarsTransition2D.java
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
import java.util.Random;
import java.util.Vector;

import com.bric.image.transition.ImageInstruction;
import com.bric.image.transition.Transition2D;
import com.bric.image.transition.Transition2DInstruction;

/** This is a series of random bars that increase in frequency, slowly revealing
 * the new frame.
 * 
 * <P>The concept for this transition is courtesy of Tech4Learning, Inc.
 *
 */
public class BarsTransition2D extends Transition2D {
	/** Keep a truly random seed; constantly creating a new
	 * Random object with the current time as its seed created
	 * several non-random frames generated in the same millisecond
	 * 
	 */
	static Random random = new Random(System.currentTimeMillis());
	
	int type;
	boolean isRandom;
	
	/** Creates a randomized horizontal BarsTransition2D
	 * 
	 */
	public BarsTransition2D() {
		this(HORIZONTAL,true);
	}
	
	/** Creates a BarsTransition2D.
	 * 
	 * @param type must be HORIZONTAL or VERTICAL
	 * @param random whether each frame is 100% random, or whether the
	 * bars are cumulative as the transition progresses.
	 */
	public BarsTransition2D(int type,boolean random) {
		if(!(type==HORIZONTAL || type==VERTICAL)) {
			throw new IllegalArgumentException("Type must be HORIZONTAL or VERTICAL.");
		}
		this.type = type;
		this.isRandom = random;
	}

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		boolean[] k;
		if(type==HORIZONTAL) {
			k = new boolean[size.height];
		} else {
			k = new boolean[size.width];
		}
		Random r;
		if(isRandom) {
			r = random;
		} else {
			r = new Random(0);
		}
		for(int a = 0; a<k.length; a++) {
			k[a] = r.nextFloat()>progress;
		}
		Vector v = new Vector();
		v.add(new ImageInstruction(false));
		if(type==HORIZONTAL) {
			int a = 0;
			while(a<k.length) {
				int run = 0;
				while(a+run<k.length && k[a+run]) {
					run++;
				}
				if(run!=0) {
					Rectangle2D r2 = new Rectangle2D.Float(0,a,size.width,run);
					v.add(new ImageInstruction(true, null, r2));
					a+=run;
				}
				a++;
			}
		} else {
			int a = 0;
			while(a<k.length) {
				int run = 0;
				while(a+run<k.length && k[a+run]) {
					run++;
				}
				if(run!=0) {
					Rectangle2D r2 = new Rectangle2D.Float(a,0,run,size.height);
					v.add(new ImageInstruction(true, null, r2));
					a+=run;
				}
				a++;
			}
		}
		return (Transition2DInstruction[])v.toArray(new Transition2DInstruction[v.size()]);
	}
	
	public String toString() {
		if(type==HORIZONTAL) {
			if(isRandom) {
				return "Bars Horizontal Random";
			}
			return "Bars Horizontal";
		}
		if(isRandom) {
			return "Bars Vertical Random";
		}
		return "Bars Vertical";
	}
}
