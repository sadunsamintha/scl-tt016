package com.sicpa.standard.sasscl;

import java.util.ArrayList;
import java.util.List;

public class TestBootstrap extends Bootstrap {

	public static final List<Runnable> tasks = new ArrayList<>();

	@Override
	public void executeSpringInitTasks() {
		super.executeSpringInitTasks();
		tasks.forEach(t -> t.run());
	}

}
