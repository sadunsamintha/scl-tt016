package com.sicpa.standard.sasscl.benchmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class BenchmarkLogParser {

	private BenchmarkData data;

	public static void main(final String[] args) {

		BenchmarkLogParser parser = new BenchmarkLogParser(null);
		parser.parse("C:/projet/sicpa-gui/standard-scl-app/log/benchmark.log");
	}

	public BenchmarkLogParser(BenchmarkFrame frame) {
		this.data = new BenchmarkData();
		this.frame = frame;
	}

	private BenchmarkFrame frame;

	public void parse(final String file) {

		String line;
		FileReader fr = null;
		try {
			fr = new FileReader(new File(file));
			BufferedReader in = new BufferedReader(fr);
			while ((line = in.readLine()) != null) {
				parserOneLine(line);
			}
			fr.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void parserOneLine(final String line) {

		if (frame != null) {
			frame.setProgress(line.length() + 1);
		}

		String[] lineData = line.split(":");
		long time = Long.parseLong(lineData[0]);
		String type = lineData[1];
		String signature = lineData[2];

		if (type.startsWith("after")) {
			long timeRef = Long.parseLong(type.substring("after-".length(), type.length()));
			this.data.addLogAfter(signature, time, timeRef);
		} else {
			this.data.addLogBefore(signature, time);
		}
	}

	public BenchmarkData getData() {
		return this.data;
	}
}
