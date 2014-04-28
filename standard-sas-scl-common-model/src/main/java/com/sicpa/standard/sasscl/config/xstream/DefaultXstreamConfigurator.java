package com.sicpa.standard.sasscl.config.xstream;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public class DefaultXstreamConfigurator implements IXStreamConfigurator {

	protected final List<IXStreamConfigurator> delegates = new ArrayList<IXStreamConfigurator>();

	@Override
	public void configure(XStream xstream) {
		xstream.setMode(XStream.NO_REFERENCES);
		for (IXStreamConfigurator configurator : delegates) {
			configurator.configure(xstream);
		}
	}

	public void setDelegates(List<IXStreamConfigurator> delegates) {
		for (IXStreamConfigurator delegate : delegates) {
			addDelegate(delegate);
		}
	}

	public void addDelegate(IXStreamConfigurator delegate) {
		delegates.add(delegate);
	}
}
