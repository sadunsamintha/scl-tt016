package com.sicpa.standard.sasscl.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JComponent;

import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.sasscl.benchmark.chart.TimeChart;
import com.sicpa.standard.sasscl.benchmark.chart.TimeChart.TypeChart;

public class ChartFactory {

	public static JComponent getChart(final BenchmarkData data, final String key1, final String key2,
			final LogEventType type1, final LogEventType type2, final String markerLabel, final int markerDelta,
			final TypeChart typeChart) {
		int index = 1;
		resetStats();
		LogEvent evt1 = null;
		LogEvent evt2;

		final TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
		final List<Pair<Integer, String>> markers = new ArrayList<Pair<Integer, String>>();
		long markerTime = -1;
		for (LogEvent evt : data.getEvents()) {

			// add marker when needed
			if (evt.getKey().equals(key1) && evt.getRefTime() > 0) {
				if (markerTime == -1) {
					markerTime = evt.getTime();
				} else {
					long delta = (evt.getTime() - markerTime) / (1000000);
					if (delta > markerDelta) {// 1min
						markers.add(new Pair<Integer, String>(index, (markers.size() + 1) + markerLabel));
						markerTime = evt.getTime();
					}
				}
			}

			if (evt.getType() == type1 && evt.getKey().equals(key1)) {
				evt1 = evt;
			}
			if (evt.getType() == type2 && evt.getKey().equals(key2)) {
				evt2 = evt;
				if (evt1 != null) {

					Integer i = new Integer((int) (evt2.getTime() - evt1.getTime()) / 1000/* 000 */);// micro second
					computeStat(i);
					map.put(index, i);
					index++;
				}

				evt1 = null;
				evt2 = null;
			}
		}

		System.out.println("min: " + min);
		System.out.println("max: " + max);
		System.out.println("event: " + event);

		avg = total / event;
		System.out.println("avg: " + avg);

		System.out.println(">1ms: " + over1ms);
		System.out.println(">5ms: " + over5ms);
		System.out.println(">20ms: " + over20ms);
		System.out.println(">50ms: " + over50ms);
		System.out.println(">100ms: " + over100ms);
		System.out.println(">200ms: " + over200ms);
		System.out.println(">500ms: " + over500ms);

		return new TimeChart(map, "Execution time", "#", "time in micro seconds", markers, typeChart).getPanel();
	}

	static int min = -1;
	static int max = 0;
	static long event = 0;
	static long total = 0;
	static long avg = 0;

	static int over500ms = 0;
	static int over200ms = 0;
	static int over100ms = 0;
	static int over50ms = 0;
	static int over20ms = 0;
	static int over5ms = 0;
	static int over1ms = 0;

	private static void resetStats() {
		min = -1;
		max = 0;
		event = 0;
		total = 0;
		avg = 0;

		over500ms = 0;
		over200ms = 0;
		over100ms = 0;
		over50ms = 0;
		over20ms = 0;
		over5ms = 0;
		over1ms = 0;
	}

	private static void computeStat(int time) {
		if (min == -1) {
			min = time;
		} else {
			min = Math.min(time, min);
		}
		max = Math.max(time, max);
		total += time;
		event++;
		if (time >= 500000) {
			over500ms++;
			over200ms++;
			over100ms++;
			over50ms++;
			over20ms++;
			over5ms++;
			over1ms++;
		} else if (time >= 200000) {
			over200ms++;
			over100ms++;
			over50ms++;
			over20ms++;
			over5ms++;
			over1ms++;
		} else if (time >= 100000) {
			over100ms++;
			over50ms++;
			over20ms++;
			over5ms++;
			over1ms++;
		} else if (time >= 50000) {
			over50ms++;
			over20ms++;
			over5ms++;
			over1ms++;
		} else if (time >= 20000) {
			over20ms++;
			over5ms++;
			over1ms++;
		} else if (time >= 5000) {
			over5ms++;
			over1ms++;
		} else if (time >= 1000) {
			over1ms++;
		}
	}
}
