package com.sicpa.standard.gui.listener;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Draggable extends MouseAdapter {

	public static enum DragDirection {
		horizontal, vertical, both
	};

	private Point lastPoint;
	private Component compGetEvent;
	private Component toMove;
	private DragDirection direction;

	private Draggable(final Component compGetEvent, final Component toMove, final DragDirection direction) {
		boolean alreadyDraggable = false;
		this.direction = direction;
		for (MouseListener ml : compGetEvent.getMouseListeners()) {
			if (ml instanceof Draggable) {
				alreadyDraggable = true;
			}
		}

		if (!alreadyDraggable) {
			compGetEvent.addMouseMotionListener(this);
			compGetEvent.addMouseListener(this);
			this.toMove = toMove;
			this.compGetEvent = compGetEvent;
		}
	}

	private Draggable(final Component c) {
		this(c, c, DragDirection.both);
	}

	public static void makeDraggable(final Component compGetEvent) {
		makeDraggable(compGetEvent, compGetEvent);
	}

	public static void makeDraggable(final Component compGetEvent, final Component toMove) {
		makeDraggable(compGetEvent, toMove, DragDirection.both);
	}

	public static void makeDraggable(final Component compGetEvent, final Component toMove, final DragDirection direction) {
		new Draggable(compGetEvent, toMove, direction);
	}

	@Override
	public void mousePressed(final MouseEvent me) {
		// else if can t be rezize
		if (this.compGetEvent.getCursor().equals(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))) {
			this.lastPoint = me.getPoint();
		} else {
			this.lastPoint = null;
		}
	}

	@Override
	public void mouseReleased(final MouseEvent me) {
		this.lastPoint = null;
	}

	@Override
	public void mouseMoved(final MouseEvent me) {
	}

	@Override
	public void mouseDragged(final MouseEvent me) {
		int x, y;
		if (this.lastPoint != null) {
			if (this.direction == DragDirection.horizontal || this.direction == DragDirection.both) {
				x = this.toMove.getX() + (me.getX() - (int) this.lastPoint.getX());
			} else {
				x = this.toMove.getX();
			}
			if (this.direction == DragDirection.vertical || this.direction == DragDirection.both) {
				y = this.toMove.getY() + (me.getY() - (int) this.lastPoint.getY());
			} else {
				y = this.toMove.getY();
			}
			this.toMove.setLocation(x, y);
		}
	}
}
