package com.sicpa.standard.sasscl;

import java.util.ArrayList;
import java.util.List;

public class ApplicationInitializationTasks {

	protected final List<Runnable> initTasks = new ArrayList<Runnable>();

	public ApplicationInitializationTasks() {

	}

	public void addInitTask(Runnable task) {
		initTasks.add(task);
	}

	public List<Runnable> getInitTasks() {
		return initTasks;
	}
}
