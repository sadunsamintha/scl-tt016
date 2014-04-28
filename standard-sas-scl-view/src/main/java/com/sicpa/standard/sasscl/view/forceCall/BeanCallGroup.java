package com.sicpa.standard.sasscl.view.forceCall;

import java.util.List;

public class BeanCallGroup implements IBeanCall {

	protected String description;
	protected List<Runnable> calls;

	@Override
	public void run() {
		for (Runnable call : calls) {
			call.run();
		}
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setCalls(List<Runnable> calls) {
		this.calls = calls;
	}

}
