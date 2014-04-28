package com.sicpa.standard.sasscl.devices.brs;

import java.util.HashMap;

/**
 * Collection to aggregate the replies generated from BRS devices
 * 
 */
public class BrsAggregateModel {

	// Bar code with its number of occurrences
	protected final HashMap<String, Integer> codes = new HashMap<String, Integer>();
	protected int totalcount;

	public void add(String code, int count) {
		Integer counter = codes.get(code);
		if (counter == null) {
			counter = new Integer(0);
		}
		counter += count;
		totalcount += count;
		codes.put(code, counter);
	}

	public void clear() {
		totalcount = 0;
		codes.clear();
	}

	public HashMap<String, Integer> getCodes() {
		return codes;
	}

	public void setCodes(HashMap<String, Integer> codes) {
		this.codes.putAll(codes);
	}

	public int getTotalcount() {
		return totalcount;
	}

	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}

}
