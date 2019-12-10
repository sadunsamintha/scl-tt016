package com.sicpa.standard.gui.screen.loader;

import java.beans.PropertyChangeListener;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import com.sicpa.standard.gui.utils.ThreadUtils;

public abstract class AbstractApplicationLoader {
	private boolean withProgress;

	private class MyWorker extends SwingWorker<Object, Object> {
		@Override
		protected Object doInBackground() {
			try {
				loadApplication();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void done() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// give time for the close animation
					ThreadUtils.sleepQuietly(500);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							AbstractApplicationLoader.this.done();
						}
					});
				}
			}).start();
		}

		public void setProgressInternal(final int i) {
			setProgress(i);
		}
	}

	private MyWorker worker = new MyWorker();

	public AbstractApplicationLoader(final boolean withProgress) {
		this.withProgress = withProgress;
	}

	public AbstractApplicationLoader() {
		this(true);
	}

	protected abstract void done();

	protected abstract void loadApplication();

	public void setProgressText(final String text) {
		this.worker.firePropertyChange("text", "", text);
	}

	public boolean isWithProgress() {
		return this.withProgress;
	}

	public void setProgress(final int progress) {
		this.worker.setProgressInternal(progress);
	}

	public final void execute() {
		this.worker.execute();
	}

	public final void addPropertyChangeListener(final PropertyChangeListener listener) {
		this.worker.addPropertyChangeListener(listener);
	}

	public final boolean cancel(final boolean mayInterruptIfRunning) {
		return this.worker.cancel(mayInterruptIfRunning);
	}

	public final int getProgress() {
		return this.worker.getProgress();
	}

	public final StateValue getState() {
		return this.worker.getState();
	}

	public final boolean isCancelled() {
		return this.worker.isCancelled();
	}

	public final boolean isDone() {
		return this.worker.isDone();
	}

	public final void removePropertyChangeListener(final PropertyChangeListener listener) {
		this.worker.removePropertyChangeListener(listener);
	}
}
