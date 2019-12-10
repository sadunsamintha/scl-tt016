package com.sicpa.standard.gui.utils;

public class Pair<T, U> {

	private T value1;
	private U value2;

	public T getValue1() {
		return this.value1;
	}

	public U getValue2() {
		return this.value2;
	}

	public void setValue1(final T value1) {
		this.value1 = value1;
	}

	public void setValue2(final U value2) {
		this.value2 = value2;
	}

	public Pair(final T value1, final U value2) {
		super();
		this.value1 = value1;
		this.value2 = value2;
	}

	public Pair() {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value1 == null) ? 0 : value1.hashCode());
		result = prime * result + ((value2 == null) ? 0 : value2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (value1 == null) {
			if (other.value1 != null)
				return false;
		} else if (!value1.equals(other.value1))
			return false;
		if (value2 == null) {
			if (other.value2 != null)
				return false;
		} else if (!value2.equals(other.value2))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Pair [value1=" + value1 + ", value2=" + value2 + "]";
	}
}
