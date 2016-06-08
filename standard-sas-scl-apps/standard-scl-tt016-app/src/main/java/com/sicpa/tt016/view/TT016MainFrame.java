package com.sicpa.tt016.view;


import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.sasscl.security.SasSclPermission;
import com.sicpa.standard.sasscl.view.MainFrame;
import com.sicpa.standard.sasscl.view.MainFrameController;

import javax.swing.*;

import static com.sicpa.standard.client.common.security.SecurityService.hasPermission;

public class TT016MainFrame extends MainFrame {

    private static final Permission RESET_STATS = new Permission("RESET_STATS");


    public TT016MainFrame(MainFrameController controller, JComponent startStopView, JComponent changeSelectionView, JComponent exitView, JComponent optionsView, JComponent messagesView, JComponent mainPanel, JComponent snapshotView, JComponent resetStatsView) {
        super(controller, startStopView, changeSelectionView, exitView, optionsView, messagesView, mainPanel, snapshotView);

        footer.add(resetStatsView, "h 80!", 4);
        resetStatsView.setVisible(hasPermission(RESET_STATS));
    }

}