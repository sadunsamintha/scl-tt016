package com.sicpa.standard.gui.components.dnd;

import java.awt.Point;
import java.awt.datatransfer.Transferable;

public interface DnDFailureCallBack {

	public void dropFailed(Transferable transferable, Point location);

}
