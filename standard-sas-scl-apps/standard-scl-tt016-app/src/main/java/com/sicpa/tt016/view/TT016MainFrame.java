package com.sicpa.tt016.view;

import static com.sicpa.standard.client.common.security.SecurityService.hasPermission;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_AWAITING_RESET;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_ERROR_STATE;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.SKU_SELECTION_VIEW_ACTIVE;

import com.google.common.eventbus.Subscribe;
import javax.swing.JComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.view.MainFrame;
import com.sicpa.standard.sasscl.view.MainFrameController;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.tt016.event.AutomatedBeamResetEvent;

@SuppressWarnings("serial")
public class TT016MainFrame extends MainFrame {

	private static final Logger logger = LoggerFactory.getLogger(TT016MainFrame.class);

	private static final Permission RESET_STATS = new Permission("RESET_STATS");

	private static final Permission RESET_BEAM_ERROR = new Permission("RESET_BEAM_ERROR");

	private JComponent resetStatsView;

	private JComponent resetBeamView;
	private boolean isBeamErrorState = false;

	public TT016MainFrame(MainFrameController controller, JComponent startStopView, JComponent changeSelectionView,
			JComponent exitView, JComponent optionsView, JComponent messagesView, JComponent mainPanel,
			JComponent snapshotView, JComponent resetBeamView, JComponent resetStatsView, SkuListProvider skuListProvider, IFlowControl flowControl) {
		super(controller, startStopView, changeSelectionView, exitView, optionsView, messagesView, mainPanel,
				snapshotView, skuListProvider, flowControl);
		this.resetBeamView = resetBeamView;
		this.resetStatsView = resetStatsView;
		footer.add(resetBeamView, "h 80!", 4);
		footer.add(resetStatsView, "h 80!", 5);
		resetStatsView.setVisible(hasPermission(RESET_STATS));
		resetBeamView.setVisible(false);
	}

	@Override
	protected void userChanged() {
		super.userChanged();
		resetStatsView.setVisible(hasPermission(RESET_STATS));
		if (isBeamErrorState) {
            resetBeamView.setVisible(hasPermission(RESET_BEAM_ERROR));
        }
	}

	@Override
	protected void buildFooter() {
		getFooter().add(startStopView, "gap 0 0 0 0, gap top 10, gap bottom 10");
		addFillerToFooter();
		footer.add(changeSelectionView, "h 80!");
		footer.add(optionsView, "h 80!");
		footer.add(exitView, "grow, gap 0 0 0 0");
	}

	@Subscribe
	public void handleBeamResetToHome(MessageEvent evt) {
		if (evt.getKey().equals(AUTOMATED_BEAM_AWAITING_RESET))
		resetBeamView.setVisible(true);
	}

	@Subscribe
	public void handleBeamResetSkuSelection(MessageEvent evt) {
		if (evt.getKey().equals(SKU_SELECTION_VIEW_ACTIVE)) {
			resetBeamView.setVisible(false);
		}
	}

    @Subscribe
    public void handleBeamErrorState(MessageEvent evt) {
        if (evt.getKey().equals(AUTOMATED_BEAM_ERROR_STATE))
            isBeamErrorState = true;
            resetBeamView.setVisible(hasPermission(RESET_BEAM_ERROR));
    }

    @Subscribe
	public void handleBeamReset(AutomatedBeamResetEvent evt) {
		logger.info(evt.message);
		isBeamErrorState = false;
		resetBeamView.setVisible(false);
	}
}