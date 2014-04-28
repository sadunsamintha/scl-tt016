package com.sicpa.standard.sasscl.benchmark;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;

public class BenchmarkAspect {

	private FileWriter out;

	public BenchmarkAspect(final String path) throws IOException {
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss");
		File file = new File(path+formatter.format(new Date())+".log");
		file.getParentFile().mkdirs();
		this.out = new FileWriter(file);
	}

	public Object log(final ProceedingJoinPoint call) throws Throwable {
		long time = System.nanoTime();
		logBefore(call.getSignature().toShortString(), time);
		Object res = call.proceed();
		logAfter(call.getSignature().toShortString(), time);
		return res;
	}

	private void logBefore(final String t, final long time) {
		logToFile(time, t, "before");
	}

	private void logAfter(final String t, final long firstTime) {
		logToFile(System.nanoTime(), t, "after-" + firstTime);
	}

	private void logToFile(final long time, final String t, final String prefix) {
		try {
			this.out.append(time + ":" + prefix + ":" + t + "\n");
			this.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		try {
			this.out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
