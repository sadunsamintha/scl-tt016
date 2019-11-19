/*
 * @(#)GooTransition2D.java
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
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.Random;

/** This resembles a bucket of paint being thrown at a wall.  The
 * clipping of the incoming shape is this liquid-dripping outline.
 *
 */
public class GooTransition2D extends AbstractClippedTransition2D {

	float[] offset;
	float[] accel;
	
	/** Creates a new goo transtion with 20 columns. */
	public GooTransition2D() {
		this(20);
	}
	
	/** Creates a new goo transition.
	 * 
	 * @param columns the number of columns to use.
	 */
	public GooTransition2D(int columns) {
		Random r = new Random();
		offset = new float[columns];
		accel = new float[columns];
		boolean ok = false;
		long seed = System.currentTimeMillis();
		while(!ok) {
			seed++;
			r.setSeed(seed);
			ok = true;
			for(int a = 0; a<columns && ok; a++) {
				offset[a] = -r.nextFloat();
				accel[a] = 4*r.nextFloat()+.2f;
				if(accel[a]+1+offset[a]<1.2)
					ok = false;
			}
			if(ok) {
				//make sure at least one strand takes up the whole time t=[0,1]
				boolean atLeastOneSlowOne = false;
				for(int a = 0; a<columns && atLeastOneSlowOne==false; a++) {
					atLeastOneSlowOne = accel[a]+1+offset[a]<1.3;
				}
				ok = atLeastOneSlowOne;
			}
		}
	}
	
	public Shape[] getShapes(float progress, Dimension size) {
		float[] f = new float[offset.length];
		for(int a = 0; a<f.length; a++) {
			f[a] = size.height*(offset[a]+progress+progress*progress*accel[a]);
		}
		float w = ((float)size.width)/((float)f.length);
		
        int k = 4; //padding to make the stroke doesn't show
        
		GeneralPath path = new GeneralPath();
		path.moveTo(-k, -k);
		
		path.lineTo(-k, f[0]);
		path.lineTo(w/2f, f[0]);
		
		for(int a = 1; a<f.length; a++) {
			float x1 = (a-1)*w+w/2f;
			float x2 = (a)*w+w/2f;
			path.curveTo(x1+w/2, f[a-1], x2-w/2, f[a], x2, f[a]);
		}
		path.lineTo(size.width+k, f[f.length-1]);
		
		path.lineTo(size.width+k, -k);
		path.lineTo(-k, -k);
		path.closePath();
		
		return new Shape[] {path};
	}

	public float getStrokeWidth(float progress) {
		return 1;
	}
	
	public String toString() {
		return "Goo";
	}

}
