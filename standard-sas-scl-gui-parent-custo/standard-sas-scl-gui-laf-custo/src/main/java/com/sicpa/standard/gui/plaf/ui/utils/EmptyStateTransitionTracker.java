/*
 * Copyright (c) 2005-2010 Substance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of Substance Kirill Grouchnikov nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package com.sicpa.standard.gui.plaf.ui.utils;

import javax.swing.ButtonModel;
import javax.swing.JComponent;

import org.pushingpixels.substance.api.ComponentStateFacet;
import org.pushingpixels.substance.internal.animation.IconGlowTracker;
import org.pushingpixels.substance.internal.animation.StateTransitionListener;
import org.pushingpixels.substance.internal.animation.StateTransitionTracker;

public class EmptyStateTransitionTracker extends StateTransitionTracker {

	public EmptyStateTransitionTracker(JComponent component, ButtonModel model) {
		super(component, model);
	}

	@Override
	public void addStateTransitionListener(StateTransitionListener stateTransitionListener) {
	}

	@Override
	public void endTransition() {

	}

	@Override
	public float getActiveStrength() {
		return 0f;
	}

	@Override
	public float getFacetStrength(ComponentStateFacet arg0) {
		return 0f;
	}

	@Override
	public float getFocusLoopPosition() {
		return 0f;
	}

	@Override
	public float getFocusStrength(boolean hasFocus) {
		return 0f;
	}

	@Override
	public float getIconGlowPosition() {
		return 0f;
	}

	@Override
	public IconGlowTracker getIconGlowTracker() {
		return super.getIconGlowTracker();
	}

	@Override
	public ButtonModel getModel() {
		return super.getModel();
	}

	@Override
	public ModelStateInfo getModelStateInfo() {
		return super.getModelStateInfo();
	}

	@Override
	public boolean hasRunningTimelines() {
		return false;
	}

	@Override
	public void onModelStateChanged() {
	}

	@Override
	public void registerFocusListeners() {
	}

	@Override
	public void registerModelListeners() {
	}

	@Override
	public void removeStateTransitionListener(StateTransitionListener stateTransitionListener) {
	}

	@Override
	public void setFocusState(boolean hasFocus) {
	}

	@Override
	public void setModel(ButtonModel model) {
	}

	@Override
	public void setRepaintCallback(RepaintCallback repaintCallback) {
	}

	@Override
	public void setTransitionPosition(float transitionPosition) {
	}

	@Override
	public void turnOffModelChangeTracking() {
	}

	@Override
	public void unregisterFocusListeners() {
	}

	@Override
	public void unregisterModelListeners() {
	}

}
