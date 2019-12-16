package com.sicpa.standard.sasscl.view.selection.change;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class SelectionChangeView extends AbstractSelectionChangeView {

	protected PaddedButton buttonChangeContext;

	public SelectionChangeView() {
		initGUI();
	}

	public void initGUI() {
		setLayout(new MigLayout("fill ,inset 0 0 0 0, gap 0 0 0 0"));
		setOpaque(false);
		add(getButtonChangeContext(), "h 80,center");
	}

	@Override
	public void modelChanged() {
		getButtonChangeContext().setEnabled(model.isChangedContextEnabled());
	}

	public PaddedButton getButtonChangeContext() {
		if (buttonChangeContext == null) {
			JButton b = new JButton(Messages.get("selection.change.button"));
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonChangeContextActionPerformed();
				}
			});
			buttonChangeContext = new PaddedButton(b);
		}
		return buttonChangeContext;
	}

	protected void buttonChangeContextActionPerformed() {
		fireChangeContext();
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		getButtonChangeContext().getButton().setText((Messages.get("selection.change.button")));
	}
}
