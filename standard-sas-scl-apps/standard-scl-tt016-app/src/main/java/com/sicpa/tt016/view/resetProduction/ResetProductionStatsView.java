package com.sicpa.tt016.view.resetProduction;


import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResetProductionStatsView extends AbstractView<IResetProductionStatsViewListener, ResetProductionStatsViewModel> {


    private JButton resetButton;

    private Permission RESET_STATS = new Permission("RESET_STATS");


    public ResetProductionStatsView() {
        setOpaque(false);
        initGUI();
    }

    protected void initGUI() {
        setLayout(new MigLayout("fill, inset 0 0 0 0"));
        add(new PaddedButton(getResetButton()), "h 80! , w 120");
    }

    public JButton getResetButton() {
        if (resetButton == null) {
            resetButton = new JButton(Messages.get("mainframe.button.reset"));
            resetButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    resetProductionStatistics();
                }
            });
        }
        return resetButton;
    }

    private void resetProductionStatistics() {
        for (IResetProductionStatsViewListener l : listeners) {
            l.reset();
        }
    }

    @Override
    public void modelChanged() {
        getResetButton().setEnabled(model.isEnable());
    }

    @Subscribe
    public void handleLanguageSwitch(LanguageSwitchEvent evt) {
        getResetButton().setText(Messages.get("mainframe.button.reset"));
    }
}
