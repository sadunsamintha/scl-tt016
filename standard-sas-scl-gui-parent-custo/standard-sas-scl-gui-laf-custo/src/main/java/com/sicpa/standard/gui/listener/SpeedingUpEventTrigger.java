package com.sicpa.standard.gui.listener;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.sicpa.standard.gui.utils.ThreadUtils;

public class SpeedingUpEventTrigger implements Runnable {

  private static final Executor GLOBAL_EXECUTORS = Executors.newSingleThreadExecutor();

  protected Runnable step;
  protected final AtomicBoolean running = new AtomicBoolean(false);
  protected Executor executor = GLOBAL_EXECUTORS;

  public SpeedingUpEventTrigger(Runnable step) {
    this.step = step;
  }

  @Override
  public void run() {
    long startTime = System.currentTimeMillis();
    while (running.get()) {
      step.run();
      ThreadUtils.sleepQuietly(getSleepTime(System.currentTimeMillis() - startTime));
    }
  }

  protected int getSleepTime(long timeSinceStart) {
    int sleepTime;
    if (timeSinceStart < 500) {
      sleepTime = 250;
    } else if (timeSinceStart < 750) {
      sleepTime = 60;
    } else if (timeSinceStart < 1000) {
      sleepTime = 50;
    } else if (timeSinceStart < 1500) {
      sleepTime = 40;
    } else if (timeSinceStart < 2000) {
      sleepTime = 50;
    } else if (timeSinceStart < 3000) {
      sleepTime = 30;
    } else if (timeSinceStart < 4000) {
      sleepTime = 10;
    } else if (timeSinceStart < 5000) {
      sleepTime = 5;
    } else {
      sleepTime = 1;
    }
    return sleepTime;
  }

  public void start() {
    if (!running.compareAndSet(false, true)) {
      return;
    }
    executor.execute(this);
  }

  public void stop() {
    running.set(false);
  }

  public void setExecutor(Executor executor) {
    this.executor = executor;
  }

  public void setStep(Runnable step) {
    this.step = step;
  }
}
