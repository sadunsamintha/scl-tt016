package com.sicpa.standard.gui.screen.machine.component.error;

import javax.swing.JPanel;
@Deprecated
public abstract class AbstractScrollingErrorPanel extends JPanel {

	private ScrollingErrorModel model;

	public AbstractScrollingErrorPanel() {
		this(new ScrollingErrorModel());
	}

	public AbstractScrollingErrorPanel(final ScrollingErrorModel model) {
		if (model == null) {
			setModel(new ScrollingErrorModel());
		} else {
			setModel(model);
		}
	}

	protected abstract void modelErrorAdded(ScrollingErrorEvent evt);

	protected abstract void modelErrorRemoved(ScrollingErrorEvent evt);

	public void setModel(final ScrollingErrorModel model) {
		if (this.model == model) {
			return;
		}
		this.model = model;
		model.addScrolingErrorListener(new ScrollingErrorlistener() {

			@Override
			public void errorRemoved(final ScrollingErrorEvent evt) {
				modelErrorRemoved(evt);
			}

			@Override
			public void errorAdded(final ScrollingErrorEvent evt) {
				modelErrorAdded(evt);
			}
		});
	}

	public ScrollingErrorModel getModel() {
		return this.model;
	}

}
