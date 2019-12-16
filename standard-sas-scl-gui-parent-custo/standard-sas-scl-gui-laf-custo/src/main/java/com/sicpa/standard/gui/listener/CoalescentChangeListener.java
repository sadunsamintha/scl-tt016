package com.sicpa.standard.gui.listener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class CoalescentChangeListener extends CoalescentListener implements ChangeListener {

	public CoalescentChangeListener() {
		super();
	}

	public CoalescentChangeListener(final int delta) {
		super(delta);
	}

	@Override
	public void stateChanged(final ChangeEvent e) {
		eventReceived();
	}

	@Override
	public abstract void doAction();
}
