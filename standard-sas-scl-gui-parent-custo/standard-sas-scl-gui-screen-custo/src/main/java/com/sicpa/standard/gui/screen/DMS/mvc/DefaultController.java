package com.sicpa.standard.gui.screen.DMS.mvc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class DefaultController implements PropertyChangeListener {

	private ArrayList<AbstractView> mRegisteredViews;
	private ArrayList<DefaultModel> mRegisteredModels;

	public DefaultController() {
		this.mRegisteredViews = new ArrayList<AbstractView>();
		this.mRegisteredModels = new ArrayList<DefaultModel>();
	}

	public void addModel(final DefaultModel model) {
		this.mRegisteredModels.add(model);
		model.addPropertyChangeListener(this);
		model.initDefault();
	}

	public void removeModel(final DefaultModel model) {
		this.mRegisteredModels.remove(model);
		model.removePropertyChangeListener(this);
	}

	public void addView(final AbstractView view) {
		this.mRegisteredViews.add(view);
	}

	public void removeView(final AbstractView view) {
		this.mRegisteredViews.remove(view);
	}

	public void propertyChange(final PropertyChangeEvent evt) {
		for (AbstractView view : this.mRegisteredViews) {
			view.modelPropertyChange(evt);
		}
	}

	protected void setModelProperty(final String propertyName, final Object newValue) throws Exception {
		for (DefaultModel model : this.mRegisteredModels) {
			try {
				Method method = model.getClass().getMethod("set" + propertyName, new Class[] { newValue.getClass() });
				method.invoke(model, newValue);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

	public final boolean isDirty() {
		for (DefaultModel aModel : this.mRegisteredModels) {
			if (aModel.isDirty())
				return true;
		}
		return false;
	}

}
