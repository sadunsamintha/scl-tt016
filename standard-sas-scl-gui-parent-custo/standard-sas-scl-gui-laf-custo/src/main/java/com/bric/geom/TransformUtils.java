/*
 * @(#)TransformUtils.java
 *
 * $Date: 2009-12-19 22:05:15 -0600 (Sat, 19 Dec 2009) $
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
package com.bric.geom;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import com.bric.math.Equations;

/** This is a collection of methods that deal with AffineTransforms.
 * Note the PerspectiveTransform class already has static methods that
 * perform similar functions.
 * 
 **/
public class TransformUtils {
		
		/** Given 3 points, this will return the <code>AffineTransform</code> that links each <code>initial</code>
		* to <code>final</code> point.
		* <P> This uses the <code>solve(matrix,true)</code> method.
		*/
		public static AffineTransform createAffineTransform(Point2D initialP1,Point2D initialP2,Point2D initialP3,
												    Point2D finalP1,Point2D finalP2,Point2D finalP3) {
		return createAffineTransform(initialP1.getX(), initialP1.getY(),
				initialP2.getX(),initialP2.getY(),
				initialP3.getX(),initialP3.getY(),
				finalP1.getX(),finalP1.getY(),
				finalP2.getX(),finalP2.getY(),
				finalP3.getX(),finalP3.getY() );
		}

		/** Given 3 points, this will return the <code>AffineTransform</code> that links each <code>initial</code>
		* to <code>final</code> point.
		* <P> This uses the <code>solve(matrix,true)</code> method.
		*/
		public static AffineTransform createAffineTransform(double oldX1,double oldY1,
				double oldX2,double oldY2,
				double oldX3,double oldY3,
				double newX1,double newY1,
				double newX2,double newY2,
				double newX3,double newY3) {
		
			double[][] matrix = { {oldX1, oldY1, 1, newX1},
					{oldX2, oldY2, 1, newX2},
					{oldX3, oldY3, 1, newX3} };
			Equations.solve(matrix,true);
			double m00 = matrix[0][3];
			double m01 = matrix[1][3];
			double m02 = matrix[2][3];
					
			matrix = new double[][] { { oldX1, oldY1, 1, newY1 }, 
					                      { oldX2, oldY2, 1, newY2 },
					                      { oldX3, oldY3, 1, newY3 } };
			Equations.solve(matrix,true);
			double m10 = matrix[0][3];
			double m11 = matrix[1][3];
			double m12 = matrix[2][3];
					
			return new AffineTransform(m00, m10, m01, m11, m02,m12);
		}
		
		/** Transitions from one AffineTransform to another.
		 * 
		 * @param a the initial AffineTransform
		 * @param b the final AffineTransform
		 * @param progress a float between zero and one, where zero
		 * represents <code>a</code> and one represents <code>b</code>.
		 * Values outside this range will not throw an exception, but they will
		 * make some funky results.
		 * @param createNewObject indicates whether a new AffineTransform
		 * should be constructed, or if one of the arguments can be
		 * used to store the results
		 * @return a transform that is somehow between <code>a</code> and <code>b</code>.
		 */
		public static AffineTransform tween(AffineTransform a,AffineTransform b,float progress,boolean createNewObject) {
			AffineTransform dest = (createNewObject) ? new AffineTransform() : a;
			dest.setTransform(
					a.getScaleX()*(1-progress)+b.getScaleX()*progress,
					a.getShearY()*(1-progress)+b.getShearY()*progress,
					a.getShearX()*(1-progress)+b.getShearX()*progress,
					a.getScaleY()*(1-progress)+b.getScaleY()*progress,
					a.getTranslateX()*(1-progress)+b.getTranslateX()*progress,
					a.getTranslateY()*(1-progress)+b.getTranslateY()*progress);
			return dest;
		}
}
