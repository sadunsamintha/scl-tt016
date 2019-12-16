/*
 * @(#)Transition.java
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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** This renders a transition between two images.
 */
public interface Transition {
	
	public static final int RIGHT = 1;
	public static final int LEFT = 2;
	public static final int UP = 3;
	public static final int DOWN = 4;
	public static final int COUNTER_CLOCKWISE = 5;
	public static final int CLOCKWISE = 6;
	public static final int IN = 7;
	public static final int OUT = 8;
	public static final int HORIZONTAL = 9;
	public static final int VERTICAL = 10;
	public static final int BIG = 11;
	public static final int MEDIUM = 12;
	public static final int SMALL = 13;
	public static final int TOP_LEFT = 14;
	public static final int TOP_RIGHT = 15;
	public static final int BOTTOM_LEFT = 16;
	public static final int BOTTOM_RIGHT = 17;
	
	/**
	 * 
	 * @param g the Graphics2D to render to.
	 * @param frameA the first frame
	 * @param frameB the second frame
	 * @param progress a value between zero and one indicating how
	 * progressed this transition is.
	 * <P>At progress = 0, frameA should be shown.  At progress = 1, frameB should be shown.
	 */
	public void paint(Graphics2D g,BufferedImage frameA,BufferedImage frameB,float progress);
}
