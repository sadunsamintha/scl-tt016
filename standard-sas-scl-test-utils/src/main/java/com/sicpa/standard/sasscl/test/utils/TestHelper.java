package com.sicpa.standard.sasscl.test.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class TestHelper {

	private static List<Runnable> tasks = Collections.synchronizedList(new ArrayList<Runnable>());

	/**
	 * init the TaskExecutor with a executor that can be control
	 */
	public static void initExecutor() {
		ExecutorService executor = Mockito.mock(ExecutorService.class);
		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				tasks.add((Runnable) invocation.getArguments()[0]);
				return null;
			}
		}).when(executor).execute(Mockito.<Runnable> anyObject());

		TaskExecutor.setExecutor(executor);

	}

	/**
	 * run all tasks that have are pending in the executor
	 */
	public static void runAllTasks() {
		ThreadUtils.invokeAndWait(new Runnable() {
			@Override
			public void run() {
			}
		});
		while (!tasks.isEmpty()) {
			System.out.println("executed TASK" + tasks.get(0).getClass());
			tasks.remove(0).run();
			// make sure to wait also for swing tasks
			ThreadUtils.invokeAndWait(new Runnable() {
				@Override
				public void run() {
				}
			});
		}
	}

	public static void runTasksUntil(String pattern, int time) {
		if (time <= 0) {
			return;
		}
		while (!tasks.isEmpty()) {
			System.out.println("executed TASK" + tasks.get(0).getClass() + " " + time);
			Runnable task = tasks.remove(0);
			task.run();
			if (task.getClass().getName().contains(pattern)) {
				runTasksUntil(pattern, --time);
				return;
			}
		}
	}
}