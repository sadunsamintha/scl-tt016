package com.sicpa.tt084.sasscl.view;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JComponent;

import com.sicpa.standard.client.common.utils.AppUtils;
import com.sicpa.standard.sasscl.view.MainFrameGetter;

public class TT084MainFrameGetter extends MainFrameGetter{
    
    @Override
    public Component getComponent() {
        if (frame == null) {
            try {
                if (!AppUtils.isHeadless()) {
                    frame = new TT084MainFrame(viewController, startStopView.getComponent(),
                            selectionChangeView.getComponent(), exitView.getComponent(), optionsView.getComponent(),
                            messagesView, (JComponent) mainPanelGetter.getComponent(), snapshotView.getComponent(), skuListProvider, flowControl);
                }
            } catch (HeadlessException e) {
            }
        }
        return frame;
    }

}
