package com.sicpa.tt018.scl.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//only "advantage" of this map is it forces you to implement the populate map 
public abstract class Mapping<KeyType, ValueType> {
	private Map<KeyType, ValueType> map;

	public Mapping() {
		super();
		populateMap();
	}

	protected abstract void populateMap();

	protected void addEntry(final KeyType key, final ValueType value) {
		getMap().put(key, value);
	}

	public ValueType getValue(final KeyType key) {
		return getMap().get(key);
	}

	public boolean hasKey(final KeyType key) {
		return getMap().containsKey(key);
	}

	public List<KeyType> getKeyList() {
		return new ArrayList<KeyType>(getMap().keySet());
	}

	protected Map<KeyType, ValueType> getMap() {
		if (map == null) {
			map = new HashMap<KeyType, ValueType>();
		}
		return map;
	}

}
