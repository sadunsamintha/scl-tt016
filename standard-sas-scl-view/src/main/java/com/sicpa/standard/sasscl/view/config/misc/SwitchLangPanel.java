package com.sicpa.standard.sasscl.view.config.misc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.common.utils.LangUtils;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.MainFrame;

public class SwitchLangPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected JButton buttonSwitchLanguageToEnglish;
	protected JButton buttonResetLang;

	public SwitchLangPanel() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("ltr,fill"));
		add(new JLabel("Language"), "pushx,span,split 2");
		add(new JSeparator(), "growx");
		add(getButtonSwitchLanguageToEnglish(), "span,split2");
		add(getButtonResetLang(), "");

	}

	public JButton getButtonSwitchLanguageToEnglish() {
		if (this.buttonSwitchLanguageToEnglish == null) {
			this.buttonSwitchLanguageToEnglish = new JButton("Switch to English");
			this.buttonSwitchLanguageToEnglish.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					OperatorLogger.log("Switch languange to English");
					buttonSwitchLanguageToEnglishActionPerformed();
				}
			});
		}
		return this.buttonSwitchLanguageToEnglish;
	}

	protected void buttonSwitchLanguageToEnglishActionPerformed() {
		LangUtils.initLanguageFiles("en");
		applyLangToAllComponents();
		getButtonResetLang().setEnabled(true);
		getButtonSwitchLanguageToEnglish().setEnabled(false);
	}

	protected void applyLangToAllComponents() {
		EventBusService.post(new LanguageSwitchEvent());
	}

	public JButton getButtonResetLang() {
		if (this.buttonResetLang == null) {
			this.buttonResetLang = new JButton("Reset language");
			this.buttonResetLang.setEnabled(false);
			this.buttonResetLang.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					OperatorLogger.log("Reset language");
					buttonResetActionPerformed();
				}
			});
		}
		return this.buttonResetLang;
	}

	protected void buttonResetActionPerformed() {
		MainFrame frame = (MainFrame) SwingUtilities.getWindowAncestor(this);
		LangUtils.initLanguageFiles(frame.getController().getGlobalConfig().getLanguage());
		applyLangToAllComponents();
		getButtonResetLang().setEnabled(false);
		getButtonSwitchLanguageToEnglish().setEnabled(true);
	}
}
