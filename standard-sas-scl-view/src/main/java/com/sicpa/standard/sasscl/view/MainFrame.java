package com.sicpa.standard.sasscl.view;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.descriptor.IPropertyDescriptor;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.security.ILoginListener;
import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.DefaultSelectionFlowView;
import com.sicpa.standard.gui.screen.machine.component.lineId.AbstractLineIdPanel;
import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.security.SasSclPermission;
import com.sicpa.standard.sasscl.view.config.GenericConfigPanel;
import com.sicpa.standard.sasscl.view.config.plc.MultiEditablePlcVariablesSet;
import com.sicpa.standard.sasscl.view.lineid.LineIdWithAuthenticateButton;
import com.sicpa.standard.sasscl.view.messages.I18nableLockingErrorModel;

@SuppressWarnings("serial")
public class MainFrame extends AbstractMachineFrame {

	protected JComponent startStopView;
	protected JComponent changeSelectionView;
	protected JComponent exitView;
	protected JComponent optionsView;
	protected JComponent messagesView;
	protected JComponent mainPanel;
	protected JComponent snapshotView;

	public MainFrame(final MainFrameController controller, JComponent startStopView, JComponent changeSelectionView,
			JComponent exitView, JComponent optionsView, JComponent messagesView, JComponent mainPanel,
			JComponent snapshotView, boolean fingerPrintLogin) {
		super(controller);
		this.snapshotView = snapshotView;
		this.startStopView = startStopView;
		this.changeSelectionView = changeSelectionView;
		this.exitView = exitView;
		this.optionsView = optionsView;
		this.messagesView = messagesView;
		this.mainPanel = mainPanel;
		
		((LineIdWithAuthenticateButton)getLineIdPanel()).setFingerPrintLogin(fingerPrintLogin);
		
		initGUI();
		I18nableLockingErrorModel lockModel = new I18nableLockingErrorModel();
		lockModel.setLockedComponent(getInternalCenterPanel());
		getProdPanel().setModel(lockModel);
		getController().setLockingErrorModel(getProdPanel().getModel());
		getController().setLineId(Messages.get("lineId") + ":" + controller.getGlobalConfig().getLineId());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
		fireUserChanged();

		// not using the default button
		getStartButton().setVisible(false);
		getStopButton().setVisible(false);

		replaceMainPanel(mainPanel);
	}
	
	private void fireUserChanged() {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				userChanged();
			}
		});
	}

	protected void userChanged() {

		// set the correct panel visible according to the user credentials
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

		this.configPanel.setPanels(comps, titles);

		// set the button visible according to the user credentials
		changeSelectionView.setVisible(SecurityService.hasPermission(SasSclPermission.PRODUCTION_CHANGE_PARAMETERS));
		exitView.setVisible(SecurityService.hasPermission(SasSclPermission.EXIT));
		snapshotView.setVisible(SecurityService.hasPermission(SasSclPermission.SCREENSHOT));
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

	public void exitButtonActionPerformed() {
		super.exitButtonActionPerformed();
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

	protected JPanel[] configs;
	protected String[] configsTitle;

	@Override
	protected JPanel[] getConfigPanels() {
		if (configs == null) {

			List<Pair<JPanel, String>> listConfigPanel = new ArrayList<Pair<JPanel, String>>();

			if (SecurityService.hasPermission(SasSclPermission.EDIT_MODEL_PROPERTY)) {
				for (Entry<Object, Map<String, List<IPropertyDescriptor>>> beanEntry : getController()
						.getEditablePropertyDescriptors().getDescriptors().entrySet()) {
					for (Entry<String, List<IPropertyDescriptor>> screenEntry : beanEntry.getValue().entrySet()) {
						String screenTitle = screenEntry.getKey();
						GenericConfigPanel config = new GenericConfigPanel(beanEntry.getKey(), screenEntry.getValue());
						EventBusService.register(config);
						listConfigPanel.add(new Pair<JPanel, String>(config, screenTitle));
					}
				}
			}

			for (SecuredComponentGetter getter : getController().getSecuredPanels()) {
				if (SecurityService.hasPermission(getter.getPermission())) {
					listConfigPanel.add(new Pair<JPanel, String>((JPanel) getter.getComponent(), getter.getTitle()));
				}
			}

			// convert to array
			configs = new JPanel[listConfigPanel.size()];
			configsTitle = new String[configs.length];

			for (int i = 0; i < listConfigPanel.size(); i++) {
				configs[i] = listConfigPanel.get(i).getValue1();
				if (!listConfigPanel.get(i).getValue2().startsWith("#")) {
					configsTitle[i] = Messages.get(listConfigPanel.get(i).getValue2());
				} else {
					configsTitle[i] = listConfigPanel.get(i).getValue2().substring(1);
				}
			}

			optionsView.setVisible(configs.length > 0);
		}

		return configs;
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

	protected AbstractAction exitAction;

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

	public void displayOptionsPreviewScreen() {
		if (getConfigPanel().getSelectedIndex() != 0) {
			getConfigPanel().showPanel(0);
		} else {
			getConfigPanel().getPreviewButton().doClick();
		}
	}
	
	@Subscribe
	public void processStateChanged(final ApplicationFlowStateChangedEvent evt) {
		ThreadUtils.invokeLater(new Runnable() 
		{
			@Override
			public void run() 
			{	
				if (evt.getCurrentState() == ApplicationFlowState.STT_NO_SELECTION ||
				    evt.getCurrentState() == ApplicationFlowState.STT_STARTING ||
				    evt.getCurrentState() == ApplicationFlowState.STT_STARTED) 
				{
					LineIdWithAuthenticateButton lineIdWithAuthenticateButton = (LineIdWithAuthenticateButton)getLineIdPanel();
					lineIdWithAuthenticateButton.getButtonLogout().setVisible(false);
					
					for (JPanel configPanel : getConfigPanels())
					{
						if(configPanel instanceof MultiEditablePlcVariablesSet)
						{
							configPanel.setEnabled(false);
							setVisibleAll(((MultiEditablePlcVariablesSet) configPanel).getComponents(), false);
						}
					}
				}
				else if (evt.getCurrentState() == ApplicationFlowState.STT_STOPPING ||
					    evt.getCurrentState() == ApplicationFlowState.STT_DISCONNECTING_ON_PARAM_CHANGED)
				{
					LineIdWithAuthenticateButton lineIdWithAuthenticateButton = (LineIdWithAuthenticateButton)getLineIdPanel();
					lineIdWithAuthenticateButton.getButtonLogout().setVisible(true);
					
					for (JPanel configPanel : getConfigPanels())
					{
						if(configPanel instanceof MultiEditablePlcVariablesSet)
						{
							configPanel.setEnabled(true);
							setVisibleAll(((MultiEditablePlcVariablesSet) configPanel).getComponents(), true);
						}
					}
				}
			}
		});
	}
	
	protected void setVisibleAll(Component[] components, boolean visible)
	{
		if(components == null || components.length == 0)
		{
			return;
		}
		for(Component subComponent2 : components)
		{
			if(!(subComponent2 instanceof JButton))
			{
				subComponent2.setEnabled(visible);
				if(subComponent2 instanceof JComponent)
				{
					setVisibleAll(((JComponent)subComponent2).getComponents(), visible);
				}
			}
		}
	}
	
	@Subscribe
	public void setProductionParameters(final ProductionParametersEvent evt) 
	{
		// Must be called in EDT
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for (SecuredComponentGetter getter : getController().getSecuredPanels()) {		
					getter.getComponent().repaint();
				}
			}
		});
	}

}
