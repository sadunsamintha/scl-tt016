package com.sicpa.standard.gui.components.dnd;

class DnDLock {

	private static boolean lock = false;

	public static void get() {
		lock = true;
	}

	public static boolean isLocked() {
		return lock;
	}

	public static void release() {
		lock = false;
	}
}
