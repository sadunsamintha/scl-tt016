package com.sicpa.tt016.view;


import com.sicpa.standard.client.common.utils.AppUtils;
import com.sicpa.standard.client.common.view.mvc.IView;
import com.sicpa.standard.sasscl.view.MainFrameGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class TT016MainFrameGetter extends MainFrameGetter {

    private IView<?> resetView;

    public final static Logger logger = LoggerFactory.getLogger(TT016MainFrameGetter.class);


    @Override
    public Component getComponent() {
        if (frame == null) {
            try {
                if (!AppUtils.isHeadless()) {
                    frame = new TT016MainFrame(viewController, startStopView.getComponent(),
                            selectionChangeView.getComponent(), exitView.getComponent(), optionsView.getComponent(),
                            messagesView, (JComponent) mainPanelGetter.getComponent(), snapshotView.getComponent(), resetView.getComponent(), skuListProvider, flowControl);
                }
            } catch (HeadlessException ex) {
                logger.error("error creating the Main frame",ex);
            }
        }
        return frame;
    }

    public void setResetView(IView<?> resetView) {
        this.resetView = resetView;
    }

}
