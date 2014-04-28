package com.sicpa.standard.sasscl.view.option;

import java.awt.Frame;

import javax.swing.JFrame;

import com.sicpa.standard.sasscl.view.MainFrame;

public class OptionsViewController implements IOptionsViewListeners {

	protected OptionsModel model;

	public OptionsViewController() {
		this(new OptionsModel());
	}

	public OptionsViewController(OptionsModel model) {
		this.model = model;
		model.setDisplayOptionEnabled(true);
	}

	@Override
	public void displayOptions() {
		getMainView().displayOptionsPreviewScreen();
	}

	protected MainFrame getMainView() {
		for (Frame f : JFrame.getFrames()) {
			if (f instanceof MainFrame) {
				return (MainFrame) f;
			}
		}
		return null;
	}

	public OptionsModel getModel() {
		return model;
	}
}
