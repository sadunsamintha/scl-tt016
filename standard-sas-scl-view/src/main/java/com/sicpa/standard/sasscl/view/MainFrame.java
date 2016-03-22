package com.sicpa.standard.sasscl.view;

import static com.sicpa.standard.client.common.security.SecurityService.hasPermission;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.client.common.security.ILoginListener;
import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.DefaultSelectionFlowView;
import com.sicpa.standard.gui.screen.machine.component.lineId.AbstractLineIdPanel;
import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.sasscl.security.SasSclPermission;
import com.sicpa.standard.sasscl.view.licence.LicencePanel;
import com.sicpa.standard.sasscl.view.lineid.LineIdWithAuthenticateButton;
import com.sicpa.standard.sasscl.view.messages.I18nableLockingErrorModel;

@SuppressWarnings("serial")
public class MainFrame extends AbstractMachineFrame {

	private final static String PREFIX_NOT_MULTI_LANG = "#";

	private JComponent startStopView;
	private JComponent changeSelectionView;
	private JComponent exitView;
	private JComponent optionsView;
	private JComponent messagesView;
	private JComponent snapshotView;

	private JPanel[] configs;
	private String[] configsTitle;
	private AbstractAction exitAction;

	public MainFrame(MainFrameController controller, JComponent startStopView, JComponent changeSelectionView,
			JComponent exitView, JComponent optionsView, JComponent messagesView, JComponent mainPanel,
			JComponent snapshotView) {
		super(controller);
		this.snapshotView = snapshotView;
		this.startStopView = startStopView;
		this.changeSelectionView = changeSelectionView;
		this.exitView = exitView;
		this.optionsView = optionsView;
		this.messagesView = messagesView;

		initGUI();
		I18nableLockingErrorModel lockModel = new I18nableLockingErrorModel();
		lockModel.setLockedComponent(getInternalCenterPanel());
		getProdPanel().setModel(lockModel);
		getController().setLockingErrorModel(getProdPanel().getModel());
		updateLineId();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addLoginListener();
		fireUserChanged();

		// not using the default button
		getStartButton().setVisible(false);
		getStopButton().setVisible(false);

		replaceMainPanel(mainPanel);
	}

	private void addLoginListener() {
		SecurityService.addLoginListener(new ILoginListener() {
			@Override
			public void logoutCompleted() {
				fireUserChanged();
			}

			@Override
			public void loginSucceeded() {
				fireUserChanged();
			}
		});
	}

	private MainFrameController getMainFrameController() {
		return (MainFrameController) controller;
	}

	private void updateLineId() {
		String lineId = MainFrameController.LINE_LABEL_ID + MainFrameController.LINE_LABEL_SEPARATOR
				+ getMainFrameController().getLineId();
		((LineIdWithAuthenticateButton) getLineIdPanel()).getLabelLineId().setText(lineId);
	}

	private void fireUserChanged() {
		SwingUtilities.invokeLater(() -> userChanged());
	}

	private void userChanged() {

		resetAndRebuildAccessiblePanel();

		// set the button visible according to the user credentials
		changeSelectionView.setVisible(hasPermission(SasSclPermission.PRODUCTION_CHANGE_PARAMETERS));
		exitView.setVisible(hasPermission(SasSclPermission.EXIT));
		snapshotView.setVisible(hasPermission(SasSclPermission.SCREENSHOT));
		startStopView.setVisible(hasPermission(SasSclPermission.PRODUCTION_START)
				&& hasPermission(SasSclPermission.PRODUCTION_STOP));
	}

	private void resetAndRebuildAccessiblePanel() {
		configs = null;

		JPanel[] comps = new JPanel[getConfigPanels().length + 1];

		for (int i = 0; i < comps.length - 1; i++) {
			comps[1 + i] = getConfigPanels()[i];
		}
		comps[0] = getProdPanel();

		String[] titles = new String[getConfigPanelsTitle().length + 1];
		for (int i = 0; i < titles.length - 1; i++) {
			titles[1 + i] = getConfigPanelsTitle()[i];
		}
		titles[0] = " ";

		configPanel.setPanels(comps, titles);
	}

	@Override
	protected AbstractAction getLogoutExtraAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				getExitButton().setVisible(false);
			}
		};
	}

	public JPanel getPanelEast() {
		if (panelEast == null) {
			panelEast = new JPanel(new MigLayout("fill,inset 0 0 0 0"));
			panelEast.add(messagesView, "grow,push,span");
			panelEast.add(new JSeparator(JSeparator.VERTICAL), "west");

		}
		return panelEast;
	}

	@Override
	protected void initGUI() {
		super.initGUI();
		getController().setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.RED, "view.status.stopped"));
		setupScreenShotTaker();

		if (getSelectionFlowView() instanceof DefaultSelectionFlowView) {
			((DefaultSelectionFlowView) getSelectionFlowView()).setBackButtonVisibleForFirstScreen(false);
		}
		getConfigPanel().setNavigBarVisible(false);

	}

	@Override
	protected JPanel[] getConfigPanels() {
		if (configs == null) {
			buildConfigPanelsList();
			optionsView.setVisible(configs.length > 0);
		}

		return configs;
	}

	private void buildConfigPanelsList() {
		List<Pair<JPanel, String>> listConfigPanel = getAvailableConfigPanels();

		// convert to array
		configs = new JPanel[listConfigPanel.size()];
		configsTitle = new String[configs.length];

		for (int i = 0; i < listConfigPanel.size(); i++) {
			configs[i] = listConfigPanel.get(i).getValue1();
		}
		buildPanelConfigTitles(listConfigPanel);
	}

	private void buildPanelConfigTitles(List<Pair<JPanel, String>> listConfigPanel) {
		configsTitle = new String[configs.length];
		for (int i = 0; i < listConfigPanel.size(); i++) {
			String title = listConfigPanel.get(i).getValue2();
			if (isTitleNotMultiLang(title)) {
				configsTitle[i] = title.substring(PREFIX_NOT_MULTI_LANG.length());
			} else {
				configsTitle[i] = Messages.get(title);
			}
		}
	}

	private boolean isTitleNotMultiLang(String text) {
		return text.startsWith(PREFIX_NOT_MULTI_LANG);
	}

	private List<Pair<JPanel, String>> getAvailableConfigPanels() {
		List<Pair<JPanel, String>> listConfigPanel = new ArrayList<>();

		for (SecuredComponentGetter getter : getController().getSecuredPanels()) {
			if (SecurityService.hasPermission(getter.getPermission())) {
				listConfigPanel.add(new Pair<JPanel, String>((JPanel) getter.getComponent(), getter.getTitle()));
			}
		}
		return listConfigPanel;
	}

	@Override
	protected String[] getConfigPanelsTitle() {
		// make sure the titles are created
		getConfigPanels();
		return configsTitle;
	}

	@Override
	protected AbstractAction getStopAction() {
		return null;
	}

	@Override
	protected AbstractAction getStartAction() {
		return null;
	}

	@Override
	protected void buildRightPanel() {
		setRightPanel(getSummaryPanel());
	}

	@Override
	protected int getEastPanelWidth() {
		return 300;
	}

	@Override
	protected int getExtraHeaderHeight() {
		return 10;
	}

	@Override
	public MainFrameController getController() {
		return (MainFrameController) super.getController();
	}

	@Override
	@Deprecated
	public AbstractAction getSelectProductAction() {
		return null;
	}

	@Override
	public void setVisible(final boolean b) {
		super.setVisible(b);
		if (b) {
			positionProgressPanel();
		}
	}

	private void positionProgressPanel() {
		// position the progres panel
		// later coz we need location of the footer (=> after the layout is
		// done) and when the frame is visible
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Point p;
				if (getFooter().isShowing()) {
					p = getFooter().getLocationOnScreen();
					SwingUtilities.convertPointFromScreen(p, MainFrame.this);
					p.y -= 100;
					p.x = 5;
				} else {
					p = new Point(5, 800);
				}
				getProgresPanel().setLocation(p);
			}
		});
	}

	@Override
	protected void buildLayeredLeftPanel() {
	}

	@Override
	protected boolean isConfigCancellable(final int index) {
		return false;
	}

	@Override
	protected boolean isConfigValidateable(final int index) {
		return false;
	}

	@Override
	protected void buildAlwaysHereFooterButton() {
	}

	@Override
	protected void buildFooter() {
		getFooter().add(startStopView, "gap 0 0 0 0 ");
		addFillerToFooter();
		footer.add(changeSelectionView, "h 80!");
		footer.add(optionsView, "h 80!");
		footer.add(exitView, "grow, gap 0 0 0 0");
	}

	public void setExitAction(final AbstractAction exitAction) {
		this.exitAction = exitAction;
	}

	@Override
	protected AbstractAction getExitCleanUpAction() {
		if (exitAction == null) {
			return super.getExitCleanUpAction();
		} else {
			return exitAction;
		}
	}

	// override to remove the listener that is added by default
	@Override
	public AbstractLineIdPanel getLineIdPanel() {
		if (lineIdPanel == null) {
			lineIdPanel = (AbstractLineIdPanel) createPanel(KEY_LINE_ID);
		}
		return lineIdPanel;
	}

	// on button exit of the config panel
	@Override
	public void logoutActionPerformed() {
		super.logoutActionPerformed();
		SecurityService.logout();
	}

	protected void setupScreenShotTaker() {
		getLayeredPane().add(snapshotView);
		getHeader().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				snapshotView.setBounds(-30, getHeader().getHeight() - 70, 100, 100);
			}
		});
	}

	@Override
	public JComponent getHeader() {
		if (header == null) {
			header = new JPanel(new MigLayout(",fill,inset 0 0 0 0, hidemode 3,gap 0 0 0 0"));
			header.add(getLineIdPanel(), "growx");
			header.add(new LicencePanel(), "growx, gapright 20, wrap");
			header.add(getConfigPasswordPanel(), "wrap,growx");
			header.add(getApplicationStatusPanel(), "grow");
			getConfigPasswordPanel().setVisible(false);
			header.add(getHeaderInfoPanel(), "south");
		}
		return header;
	}

	public void displayOptionsPreviewScreen() {
		if (getConfigPanel().getSelectedIndex() != 0) {
			getConfigPanel().showPanel(0);
		} else {
			getConfigPanel().getPreviewButton().doClick();
		}
	}
}
