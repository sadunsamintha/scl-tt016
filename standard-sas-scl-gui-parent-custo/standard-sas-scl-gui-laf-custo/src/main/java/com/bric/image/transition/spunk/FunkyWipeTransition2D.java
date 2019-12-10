/*
 * @(#)FunkyWipeTransition2D.java
 *
 * $Date: 2009-11-17 20:56:29 -0600 (Tue, 17 Nov 2009) $
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
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.bric.geom.Clipper;
import com.bric.geom.MeasuredShape;
import com.bric.geom.RectangularTransform;
import com.bric.image.transition.ImageInstruction;
import com.bric.image.transition.Transition2D;
import com.bric.image.transition.Transition2DInstruction;

/** This is a fun variation of a "Wipe" transition.  The line
 * that separates the two frames spins as it slides.  The
 * circular wipe is especially interesting because it begins and ends
 * on the same side of the frame, because because it has rotated 180
 * degrees a new frame is visible.
 * <P>(This is loosely based on a transition I saw while
 * watching Royal Pains on Hulu one weekend...)
 *
 */
public class FunkyWipeTransition2D extends Transition2D {
	
	private static final GeneralPath pathCyclic = createPathCyclic();
	private static final MeasuredShape measuredPathCyclic = new MeasuredShape(pathCyclic);
	private static final GeneralPath pathAcross = createPathAcross();
	private static final MeasuredShape measuredPathAcross = new MeasuredShape(pathAcross);
	
	private static GeneralPath createPathCyclic() {
		GeneralPath p = new GeneralPath();
		p.moveTo(99.936f, 51.019f);
		p.curveTo(99.936f, 51.019f, 78.316f, 86.931f, 51.019f, 89.745f);
		p.curveTo(23.721f, 92.559f, -2.012f, 75.843f, 11.082f, 61.21f);
		p.curveTo(4.178f, 46.576f, 34.931f, 39.565f, 62.229f, 36.751f);
		p.curveTo(89.526f, 33.937f, 99.936f, 51.019f, 99.936f, 51.019f);
		return p;
	}
	private static GeneralPath createPathAcross() {
		GeneralPath p = new GeneralPath();
		p.moveTo(99.936f, 21.019f);
		p.curveTo(99.936f, 51.019f, 78.316f, 86.931f, 51.019f, 89.745f);
		p.curveTo(23.721f, 92.559f, -2.012f, 75.843f, 0, 61.21f);
		return p;
	}
	
	boolean circular;
	
	public FunkyWipeTransition2D(boolean fullCircle) {
		circular = fullCircle;
	}
	

	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		Rectangle2D.Float frameRect = new Rectangle2D.Float(0, 0, size.width, size.height);
		Point2D p = new Point2D.Double();
		MeasuredShape path = circular ? measuredPathCyclic : measuredPathAcross;
		
		path.getPoint(progress*path.getOriginalDistance(), p );
		
		int m = circular ? 1 : 2;
		double angle = Math.PI/2+m*Math.PI*progress;
		
		float k = 10000;
		GeneralPath clip = new GeneralPath();
		clip.moveTo((float)p.getX(), (float)p.getY());
		clip.lineTo((float)(p.getX()+k*Math.cos(angle)), (float)(p.getY()+k*Math.sin(angle)));
		clip.lineTo((float)(p.getX()+k*Math.cos(angle)+k*Math.cos(angle-Math.PI/2)), 
				(float)(p.getY()+k*Math.sin(angle)+k*Math.sin(angle-Math.PI/2)) );
		clip.lineTo((float)(p.getX()-100*Math.cos(angle)+k*Math.cos(angle-Math.PI/2)), 
				(float)(p.getY()-k*Math.sin(angle)+k*Math.sin(angle-Math.PI/2)) );
		clip.lineTo((float)(p.getX()-k*Math.cos(angle)), 
				(float)(p.getY()-k*Math.sin(angle)) );
		clip.closePath();
		
		AffineTransform map = RectangularTransform.create(
				new Rectangle2D.Float(0, 0, 100, 100),
				frameRect
		);
		clip.transform( map );
		
		clip = Clipper.clipToRect(clip, frameRect);
		
		return new Transition2DInstruction[] {
				new ImageInstruction(true, 1, frameRect, size, null),
				new ImageInstruction(false, 1, frameRect, size, clip)
		};
	}
	
	public String toString() {
		String s = ( circular ? " Circular" : " Across");
		return "Funky Wipe "+s;
	}
	
}
