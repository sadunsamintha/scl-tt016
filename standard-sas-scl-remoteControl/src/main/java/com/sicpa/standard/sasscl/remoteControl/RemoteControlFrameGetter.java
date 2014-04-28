package com.sicpa.standard.sasscl.remoteControl;

import java.awt.Component;

import javax.swing.JFrame;

import com.sicpa.standard.client.common.view.IGUIComponentGetter;

public class RemoteControlFrameGetter implements IGUIComponentGetter {

	protected JFrame frame;

	@Override
	public Component getComponent() {
		if (this.frame == null) {
			this.frame = new RemoteControlFrame();
		}
		return this.frame;
	}

}
