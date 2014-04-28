package com.sicpa.standard.sasscl.view.selection.change;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.MainFrame;

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
		getButtonChangeContext().getButton().setText((Messages.get(GUIi18nManager.get(MainFrame.I18N_CHANGE_TYPE))));
	}
}
