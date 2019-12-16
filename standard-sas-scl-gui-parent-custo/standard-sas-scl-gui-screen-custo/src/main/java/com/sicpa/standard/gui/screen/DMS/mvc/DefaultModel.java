package com.sicpa.standard.gui.screen.DMS.mvc;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class DefaultModel {

	protected PropertyChangeSupport mChangeSupport;

	public DefaultModel() {
		this.mChangeSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(final PropertyChangeListener changeListener) {
		this.mChangeSupport.addPropertyChangeListener(changeListener);
	}

	public void removePropertyChangeListener(final PropertyChangeListener changeListener) {
		this.mChangeSupport.removePropertyChangeListener(changeListener);
	}

	public void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
		this.mChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	public abstract void initDefault();

	public abstract int getId();

	public abstract boolean isDirty();

}
