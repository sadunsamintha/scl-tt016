package com.sicpa.standard.gui.components.dnd;

import java.awt.Point;
import java.awt.datatransfer.Transferable;

public interface DnDStartStopCallback {

	void dragStart(Transferable transferable, Point location);

	void dragStop(Transferable transferable, Point location);
}
