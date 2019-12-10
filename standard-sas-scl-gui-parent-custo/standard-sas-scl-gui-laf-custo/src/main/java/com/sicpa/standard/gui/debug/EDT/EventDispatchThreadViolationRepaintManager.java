package com.sicpa.standard.gui.debug.EDT;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

public class EventDispatchThreadViolationRepaintManager extends RepaintManager {
	// it is recommended to pass the complete check
	private boolean completeCheck = true;
	private WeakReference<JComponent> lastComponent;

	public EventDispatchThreadViolationRepaintManager(final boolean completeCheck) {
		this.completeCheck = completeCheck;
	}

	public EventDispatchThreadViolationRepaintManager() {
		this(true);
	}

	public boolean isCompleteCheck() {
		return this.completeCheck;
	}

	public void setCompleteCheck(final boolean completeCheck) {
		this.completeCheck = completeCheck;
	}

	@Override
	public synchronized void addInvalidComponent(final JComponent component) {
		checkThreadViolations(component);
		super.addInvalidComponent(component);
	}

	@Override
	public void addDirtyRegion(final JComponent component, final int x, final int y, final int w, final int h) {
		checkThreadViolations(component);
		super.addDirtyRegion(component, x, y, w, h);
	}

	private void checkThreadViolations(final JComponent c) {
		if (!SwingUtilities.isEventDispatchThread() && (this.completeCheck || c.isShowing())) {
			boolean repaint = false;
			boolean fromSwing = false;
			boolean imageUpdate = false;
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			for (StackTraceElement st : stackTrace) {
				if (repaint && st.getClassName().startsWith("javax.swing.") &&
				// for details see
						// https://swinghelper.dev.java.net/issues/show_bug.cgi?id=1
						!st.getClassName().startsWith("javax.swing.SwingWorker")) {
					fromSwing = true;
				}
				if (repaint && "imageUpdate".equals(st.getMethodName())) {
					imageUpdate = true;
				}
				if ("repaint".equals(st.getMethodName())) {
					repaint = true;
					fromSwing = false;
				}
			}
			if (imageUpdate) {
				// assuming it is java.awt.image.ImageObserver.imageUpdate(...)
				// image was asynchronously updated, that's ok
				return;
			}
			if (repaint && !fromSwing) {
				// no problems here, since repaint() is thread safe
				return;
			}
			// ignore the last processed component
			if (this.lastComponent != null && c == this.lastComponent.get()) {
				return;
			}
			this.lastComponent = new WeakReference<JComponent>(c);
			violationFound(c, stackTrace);
		}
	}

	protected void violationFound(final JComponent c, final StackTraceElement[] stackTrace) {
		System.out.println();
		System.out.println("EDT violation detected");
		System.out.println(c);
		for (StackTraceElement st : stackTrace) {
			System.out.println("\tat " + st);
		}
	}

	public static void install() {
		RepaintManager.setCurrentManager(new EventDispatchThreadViolationRepaintManager());
	}

	public static void main(final String[] args) {
		install();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(800, 600);
				f.getContentPane().setLayout(new MigLayout());
				JButton b = new JButton("switch foreground color");
				final JLabel label = new JLabel("EDT TEST");
				b.addActionListener(new ActionListener() {
					int cpt = 0;

					@Override
					public void actionPerformed(final ActionEvent arg0) {
						// we are in the EDT
						// Start a new thread to run the code outside the EDT
						// it could simulate a signal from a machine
						new Thread(new Runnable() {
							@Override
							public void run() {
								if (cpt % 2 == 0) {
									label.setForeground(Color.red);

								} else {
									label.setForeground(Color.green);
								}
								cpt++;
							}
						}).start();

					}
				});
				f.getContentPane().add(label);
				f.getContentPane().add(b);
				f.setVisible(true);
			}
		});
	}
}
