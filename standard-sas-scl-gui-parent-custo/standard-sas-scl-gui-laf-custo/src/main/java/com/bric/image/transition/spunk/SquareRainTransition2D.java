/*
 * @(#)SquareRainTransition2D.java
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
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;
import java.util.Vector;

import com.bric.geom.TransformUtils;

/** In this transition geometric shapes (squares and circles)
 * trickle downwards revealing the next frame.
 *
 */
public class SquareRainTransition2D extends AbstractClippedTransition2D {

	float[] offset;
	float[] accel;
	
	/** Creates a new square rain transition with 12 columns that is not randomized.
	 * 
	 */
	public SquareRainTransition2D() {
		this(12,false);
	}

	/** Creates a new square rain transition.
	 * 
	 * @param columns how many columns to use
	 * @param randomize whether the transition should be randomly generated or not.
	 * If not, a constant random seed is used.
	 */
	public SquareRainTransition2D(int columns,boolean randomize) {
		Random r = new Random();
		offset = new float[columns];
		accel = new float[columns];
		boolean ok = false;
        
        long seed = 1196622174915L;
        if(randomize) {
            seed = System.currentTimeMillis();
        }
		while(!ok) {
			seed++;
			r.setSeed(seed);
			ok = true;
			for(int a = 0; a<columns && ok; a++) {
				float m = ((float)a)/((float)columns-1f);
				if(m<.5f) {
					m = m/.5f;
				} else {
					m = (1-m)/.5f;
				}
				offset[a] = -m;
				accel[a] = 10*r.nextFloat();
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
		Vector v = new Vector();
		Rectangle2D rect;
		float columnWidth = ((float)size.width)/((float)offset.length);
		int rows = (int)(size.height/columnWidth+.5);
		float rowHeight = ((float)size.height)/rows;
		for(int a = 0; a<offset.length; a++) {
			float x = a*columnWidth;
			float centerX = x+columnWidth/2;
			float w = size.width/offset.length;
			float y = size.height*(offset[a]+progress+progress*progress*accel[a]);
			
			int row = (int)((y-2*rowHeight)/rowHeight);
			
			
			rect = new Rectangle2D.Float(x-1,0,w+2,row*rowHeight);
			v.add(rect);
			float centerY = row*rowHeight+rowHeight/2;
			float k = ((float)(y-rowHeight*row))/((float)(rowHeight));
			
			float k1 = k/3f;
			float k2 = (k-1f)/3f;
			float k3 = (k-2f)/3f;
			if(k1<0) k1 = 0;
			if(k2<0) k2 = 0;
			if(k3<0) k3 = 0;
			if(k1>1) k1 = 1;
			if(k2>1) k2 = 1;
			if(k3>1) k3 = 1;

			if(true) {
				if(k1>0) {
					Shape shape = new RoundRectangle2D.Float(centerX-k1*columnWidth/2,centerY-k1*rowHeight/2,k1*columnWidth,k1*rowHeight,columnWidth/4*(1-k1),rowHeight/4*(1-k1));
					v.add(shape);
				}
				if(k2>0) {
					Shape shape = new RoundRectangle2D.Float(centerX-k2*columnWidth/2,centerY-k2*rowHeight/2+rowHeight,k2*columnWidth,k2*rowHeight,columnWidth*(1-k2),rowHeight*(1-k2));
					v.add(shape);
				}
				if(k3>0) {
					Shape shape = new RoundRectangle2D.Float(centerX-k3*columnWidth/2,centerY-k3*rowHeight/2+2*rowHeight,k3*columnWidth,k3*rowHeight,columnWidth*(1-k3),rowHeight*(1-k3));
					v.add(shape);
				}
			} else {
				if(k1>0) {
					Shape shape = new Rectangle2D.Float(centerX-k1*columnWidth/2,centerY-k1*rowHeight/2,k1*columnWidth,k1*rowHeight);
					v.add(shape);
				}
				if(k2>0) {
					Shape shape = new Rectangle2D.Float(centerX-k2*columnWidth/2,centerY-k2*rowHeight/2+rowHeight,k2*columnWidth,k2*rowHeight);
					v.add(shape);
				}
				if(k3>0) {
					Shape shape = new Rectangle2D.Float(centerX-k3*columnWidth/2,centerY-k3*rowHeight/2+2*rowHeight,k3*columnWidth,k3*rowHeight);
					v.add(shape);
				}
			}
		}
		
		if(false) {
			AffineTransform transform = TransformUtils.createAffineTransform(
					0,0,
					size.width,0,
					0,size.height,
					0,size.height,
					size.width,size.height,
					0,0 );
			for(int a = 0; a<v.size(); a++) {
				Shape shape = (Shape)v.get(a);
				shape = transform.createTransformedShape(shape);
				v.set(a, shape);
			}
		}
		
		Shape[] shapes = (Shape[])v.toArray(new Shape[v.size()]);
        
        //make sure the stroke doesn't show:
        float k = getStrokeWidth(progress)+1;
        
        AffineTransform fit = TransformUtils.createAffineTransform(
                0,0,
                size.width,0,
                0,size.height,
                -k,-k,
                size.width+k,-k,
                -k,size.height+k
        );
        for(int a = 0; a<shapes.length; a++) {
            if(shapes[a] instanceof GeneralPath) {
                ((GeneralPath)shapes[a]).transform(fit);
            } else {
                shapes[a] = fit.createTransformedShape(shapes[a]);
            }
        }
        return shapes;
	}

	public float getStrokeWidth(float progress) {
		return 5;
	}
	
	public String toString() {
		return "Square Rain";
	}

}
