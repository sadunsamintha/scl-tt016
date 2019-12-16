package com.sicpa.standard.gui.screen.machine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.gui.components.buttons.StartStopButton;
import com.sicpa.standard.gui.components.buttons.StartStopButton.eStartStop;
import com.sicpa.standard.gui.components.layeredComponents.lock.lockablePanel.AbstractLockablePanel;
import com.sicpa.standard.gui.components.layeredComponents.lock.lockablePanel.DefaultLockablePanel;
import com.sicpa.standard.gui.components.layeredComponents.lock.lockingError.AbstractLockingErrorPanel;
import com.sicpa.standard.gui.components.layeredComponents.lock.lockingError.DefaultLockingErrorPanel;
import com.sicpa.standard.gui.components.layeredComponents.oldValue.OldValueUI;
import com.sicpa.standard.gui.components.layeredComponents.oldValue.OldValueWrapper;
import com.sicpa.standard.gui.components.panels.LayerPanel.SizeSynchroLayerPanel;
import com.sicpa.standard.gui.components.panels.LayerPanel.SizeSynchronizer;
import com.sicpa.standard.gui.components.transition.impl.RectanglesTransition;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelConfig;
import com.sicpa.standard.gui.plaf.ui.SicpaButtonUI;
import com.sicpa.standard.gui.screen.machine.component.IdInput.AbstractIdInputView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.DefaultSelectionFlowView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.SelectionFlowAdapter;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.SelectionFlowEvent;
import com.sicpa.standard.gui.screen.machine.component.applicationStatus.AbstractApplicationStatusPanel;
import com.sicpa.standard.gui.screen.machine.component.applicationStatus.DefaultApplicationStatusPanel;
import com.sicpa.standard.gui.screen.machine.component.config.ConfigPanel;
import com.sicpa.standard.gui.screen.machine.component.config.SaveTask;
import com.sicpa.standard.gui.screen.machine.component.configPassword.AbstractConfigPasswordPanel;
import com.sicpa.standard.gui.screen.machine.component.configPassword.ConfigPasswordEvent;
import com.sicpa.standard.gui.screen.machine.component.configPassword.ConfigPasswordListener;
import com.sicpa.standard.gui.screen.machine.component.configPassword.DefaultConfigPasswordPanel;
import com.sicpa.standard.gui.screen.machine.component.configPassword.IConfigPasswordChecker;
import com.sicpa.standard.gui.screen.machine.component.confirmation.AbstractConfirmationPanel;
import com.sicpa.standard.gui.screen.machine.component.confirmation.ConfirmationCallback;
import com.sicpa.standard.gui.screen.machine.component.confirmation.ConfirmationEvent;
import com.sicpa.standard.gui.screen.machine.component.confirmation.DefaultConfirmationPanel;
import com.sicpa.standard.gui.screen.machine.component.emergency.AbstractEmergencyPanel;
import com.sicpa.standard.gui.screen.machine.component.emergency.DefaultEmergencyPanel;
import com.sicpa.standard.gui.screen.machine.component.error.AbstractScrollingErrorPanel;
import com.sicpa.standard.gui.screen.machine.component.error.DefaultScrollingErrorPanel;
import com.sicpa.standard.gui.screen.machine.component.infoHeader.AbstractHeaderInfoPanel;
import com.sicpa.standard.gui.screen.machine.component.infoHeader.DefaultHeaderInfoPanel;
import com.sicpa.standard.gui.screen.machine.component.lineId.AbstractLineIdPanel;
import com.sicpa.standard.gui.screen.machine.component.lineId.DefaultLineIdPanel;
import com.sicpa.standard.gui.screen.machine.component.progress.AbstractProgressView;
import com.sicpa.standard.gui.screen.machine.component.progress.DefaultProgressView;
import com.sicpa.standard.gui.screen.machine.component.selectionSummary.AbstractSummaryPanel;
import com.sicpa.standard.gui.screen.machine.component.selectionSummary.DefaultSummaryPanel;
import com.sicpa.standard.gui.screen.machine.component.warning.AbstractMessagesPanel;
import com.sicpa.standard.gui.screen.machine.component.warning.DefaultMessagesPanel;
import com.sicpa.standard.gui.screen.machine.component.warning.MessagesListener;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.MessageEvent;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.MessageEventUpdate;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.gui.utils.WindowsUtils;

/**
 * base frame for StS - RtR - SAS - SCL application
 * 
 * @author DIelsch
 */
public abstract class AbstractMachineFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public static final String I18N_EXIT = GUIi18nManager.SUFFIX + "machine.exitButton";
	public static final String I18N_CHANGE_TYPE = GUIi18nManager.SUFFIX + "machine.changeTypeButton";
	public static final String I18N_EXIT_LABEL = GUIi18nManager.SUFFIX + "machine.exit.Question";
	public static final String I18N_EXIT_YES = GUIi18nManager.SUFFIX + "machine.exit.yes";
	public static final String I18N_EXIT_NO = GUIi18nManager.SUFFIX + "machine.exit.no";
	public static final String I18N_START = GUIi18nManager.SUFFIX + "machine.start";
	public static final String I18N_STOP = GUIi18nManager.SUFFIX + "machine.stop";

	public static final PanelKey KEY_LINE_ID = new PanelKey(AbstractLineIdPanel.class);
	public static final PanelKey KEY_WARNING = new PanelKey(AbstractMessagesPanel.class);
	public static final PanelKey KEY_EMERGENCY = new PanelKey(AbstractEmergencyPanel.class);
	public static final PanelKey KEY_CONFIRMATION = new PanelKey(AbstractConfirmationPanel.class);
	public static final PanelKey KEY_FATAL_ERROR = new PanelKey(AbstractScrollingErrorPanel.class);
	public static final PanelKey KEY_MINOR_ERROR = new PanelKey(AbstractScrollingErrorPanel.class);
	public static final PanelKey KEY_APPLICATION_STATUS = new PanelKey(AbstractApplicationStatusPanel.class);
	public static final PanelKey KEY_SELECTION_FLOW = new PanelKey(AbstractSelectionFlowView.class);
	public static final PanelKey KEY_SUMMARY = new PanelKey(AbstractSummaryPanel.class);
	public static final PanelKey KEY_INFO_HEADER_INFO_PANEL = new PanelKey(AbstractHeaderInfoPanel.class);
	public static final PanelKey KEY_PROGRESS_PANEL = new PanelKey(AbstractProgressView.class);
	public static final PanelKey KEY_BARCODE_PANEL = new PanelKey(AbstractIdInputView.class);

	public static OldValueWrapper getOldValueWrapper(final JComponent comp) {

		OldValueWrapper wrapper = new OldValueWrapper(comp);
		wrapper.setOldValue();
		return wrapper;
	}

	protected JPanel header;
	protected AbstractLineIdPanel lineIdPanel;
	private JPanel wrappingHeader;

	protected JPanel footer;
	protected AbstractLockablePanel layerFooter;
	protected JButton startButton;
	protected JButton stopButton;
	protected JButton selectProductButton;

	protected JScrollPane scrollRightPanel;

	protected ConfigPanel configPanel;

	protected JLabel powerByLabel;

	protected PaddedButton exitButton;
	protected JComponent exitBackgroundButton;

	// ----warning
	protected AbstractMessagesPanel warningPanel;

	// password
	protected AbstractConfigPasswordPanel configPasswordPanel;

	// view=config panel , to be able to show the old value
	protected JXLayer<JComponent> layerOldValue;
	protected OldValueUI oldValueUI;

	protected JPanel centerPanel;
	protected JPanel internalCenterPanel;

	// left panel = stack of layers
	protected SizeSynchroLayerPanel layereredLeftPanel;

	// #1 panel in config panel, contains the error ui
	protected AbstractLockingErrorPanel prodPanel;

	// panel error that are put inside the left panel layer
	protected AbstractScrollingErrorPanel scrollingErrorFatal;
	protected AbstractScrollingErrorPanel scrollingErrorMinor;

	protected AbstractEmergencyPanel emergencyPanel;
	protected AbstractConfirmationPanel confirmationPanel;
	protected AbstractConfirmationPanel confirmationUnblockablePanel;

	protected MachineViewController controller;

	protected AbstractApplicationStatusPanel applicationStatusPanel;

	protected AbstractSelectionFlowView selectionFlowView;

	protected AbstractSummaryPanel summaryPanel;

	protected JSeparator separatorSummary;
	protected JSeparator separatorWarning;
	protected JSeparator separatorCenterPanel;

	protected AbstractHeaderInfoPanel headerInfoPanel;

	protected AbstractProgressView progressPanel;

	protected AbstractIdInputView barcodePanel;

	// containt summary panel + warning panel
	protected JPanel panelEast;

	// to be able to lock the whole frame
	protected JPanel panelToLockFrame;
	protected AbstractLockablePanel layerLockLockAll;

	public static final MapExtPanel mapPanelClasses = new MapExtPanel();
	static {
		mapPanelClasses.put(KEY_LINE_ID, DefaultLineIdPanel.class);
		mapPanelClasses.put(KEY_APPLICATION_STATUS, DefaultApplicationStatusPanel.class);
		mapPanelClasses.put(KEY_CONFIRMATION, DefaultConfirmationPanel.class);
		mapPanelClasses.put(KEY_FATAL_ERROR, DefaultScrollingErrorPanel.class);
		mapPanelClasses.put(KEY_MINOR_ERROR, DefaultScrollingErrorPanel.class);
		mapPanelClasses.put(KEY_WARNING, DefaultMessagesPanel.class);
		mapPanelClasses.put(KEY_SELECTION_FLOW, DefaultSelectionFlowView.class);
		mapPanelClasses.put(KEY_SUMMARY, DefaultSummaryPanel.class);
		mapPanelClasses.put(KEY_INFO_HEADER_INFO_PANEL, DefaultHeaderInfoPanel.class);
		mapPanelClasses.put(KEY_PROGRESS_PANEL, DefaultProgressView.class);
	}

	public AbstractMachineFrame(final MachineViewController controller) {
		UIManager.put(SicpaButtonUI.ROLLOVER_EFFECT, Boolean.FALSE);
		SicpaLookAndFeelCusto.turnOffBasicComponentsAnimation();
		setIconImage(GraphicsUtilities.createCompatibleTranslucentImage(1, 1));
		setController(controller);
		initController();

		getConfigPasswordPanel().getModel().setChecker(new IConfigPasswordChecker() {
			@Override
			public boolean checkPassword(final String password) {
				return true;
			}
		});
	}

	protected void setController(final MachineViewController controller) {
		this.controller = controller;
	}

	protected void initController() {
		this.controller.setMessagesModel(getWarningPanel().getModel());
		this.controller.setApplicationStatusModel(getApplicationStatusPanel().getModel());
		this.controller.setScrollingMinorErrorModel(getScrollingErrorMinor().getModel());
		this.controller.setScrollingFatalErrorModel(getScrollingErrorFatal().getModel());
		this.controller.setConfirmationModel(getConfirmationPanel().getModel());
		this.controller.setUnblockableConfirmationModel(getConfirmationUnblockablePanel().getModel());
		this.controller.setEmergencyModel(getEmergencyPanel().getModel());
		this.controller.setLockingErrorModel(getProdPanel().getModel());
		this.controller.setLockableFooterModel(getLayerFooter().getModel());
		this.controller.setConfigPasswordModel(getConfigPasswordPanel().getModel());
		this.controller.setLineIdModel(getLineIdPanel().getModel());
		this.controller.setHeaderInfoModel(getHeaderInfoPanel().getModel());
		this.controller.setRunningModel(getProgresPanel().getModel());
		this.controller.setLockableFrameModel(getLayerLockLockAll().getModel());
	}

	public MachineViewController getController() {
		return this.controller;
	}

	/**
	 * add an eager gap that will take as much space as possible, => push miglayout constraint
	 */
	public void addFillerToFooter() {
		this.footer.add(Box.createGlue(), "push,grow");
	}

	/**
	 * add a gap of the given size
	 * 
	 * @param size
	 *            of the gap
	 */
	public void addFillerToFooter(final int size) {
		this.footer.add(Box.createGlue(), "grow, w " + size + "!");
	}

	/**
	 * add a component as a top layer in the layeredLeftPanel
	 * 
	 * @param comp
	 */
	public void addLayerToLeftPanel(final JComponent comp) {
		getLayereredLeftPanel().addLayer(comp);
	}

	/**
	 * add a component as a layer in the layeredLeftPanel at the given index
	 * 
	 * @param comp
	 * @param index
	 */
	public void addLayerToLeftPanel(final JComponent comp, final int index) {
		getLayereredLeftPanel().addLayer(comp, index);
	}

	/**
	 * add a component as a layer in the layeredLeftPanel at the given index<br>
	 * 
	 * @param comp
	 * @param position
	 * @param halfSize
	 *            if true the component only take the bottom half of the layeredLeftPanel
	 */
	public void addLayerToLeftPanel(final JComponent comp, final int position, final boolean halfSize) {
		getLayereredLeftPanel().addLayer(comp, position, halfSize);
	}

	/**
	 * Add a component to the footer
	 * 
	 * @param comp
	 *            the component to add to the footer
	 */
	public void addToFooter(final JComponent comp) {
		if (comp == null) {
			throw new IllegalArgumentException("The JComponent can not be null");
		}
		if (comp instanceof AbstractButton && !(comp instanceof StartStopButton)) {
			this.addToFooter(new PaddedButton((AbstractButton) comp));
		} else {
			this.footer.add(comp, "growx, h 80!");
		}
	}

	protected void buildAlwaysHereFooterButton() {
		this.footer.add(getStartButton(), "gap top 5,gap bottom 5");
		this.footer.add(getStopButton(), "gap top 5,gap bottom 5");
		this.footer.add(getExitBackgroundButton(), "growx,w 175,h 80!");
		// addFillerToFooter();
	}

	/**
	 * add all the components to the footer:<br>
	 * addToFooter(JComponent);<br>
	 * addFillerToFooter();<br>
	 * addFillerToFooter(int width);
	 */
	protected abstract void buildFooter();

	/**
	 * add the layers to the left panel here using the addLayerToLeftPanel methods<br>
	 * do not forget to add the scrollingErrorMinor and scrollingErrorFatal<br>
	 * ex:<br>
	 * int index=0;<br>
	 * addLayerToLeftPanel(myLayer1,index++);<br>
	 * addLayerToLeftPanel(myLayer2,index++);<br>
	 * addLayerToLeftPanel(myLayer3,index++);<br>
	 * addLayerToLeftPanel(getScrollingErrorMinor(), index++,true);<br>
	 * addLayerToLeftPanel(getScrollingErrorFatal(), index++, true);<br>
	 * addLayerToLeftPanel(getConfirmationPanel(), index++); <br>
	 * addLayerToLeftPanel(getEmergencyPanel(), index++);<br>
	 */
	protected abstract void buildLayeredLeftPanel();

	private void cancelPasswordActionPerformed() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getConfigPasswordPanel().setVisible(false);
				getLineIdPanel().setVisible(true);
				getConfigPanel().setPanelButtonsVisible(false);
			}
		});
	}

	protected JPanel getCenterPanel() {
		if (this.centerPanel == null) {
			this.centerPanel = new JPanel(new BorderLayout());

			this.centerPanel.add(getLayerOldValue());

			SicpaLookAndFeelCusto.flagAsWorkArea(this.centerPanel);
		}
		return this.centerPanel;
	}

	public ConfigPanel getConfigPanel() {
		if (this.configPanel == null) {
			this.configPanel = new ConfigPanel() {
				@Override
				public void showPanel(final int i) {
					super.showPanel(i);
					if (i != 0) {
						setCancellable(isConfigCancellable(i));
						setValidadeable(isConfigValidateable(i));
					}
				}
			};

			if (getConfigPanels().length != getConfigPanelsTitle().length) {
				throw new IllegalArgumentException("the number of config panel does not match the number of title:"
						+ getConfigPanels().length + " : " + getConfigPanelsTitle().length);
			}

			this.configPanel.setSaveTask(getSaveConfigTask());

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

			this.configPanel.setPanelButtonsVisible(false);

			getConfigPanel().getExitButton().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					logoutActionPerformed();
				}
			});
		}
		return this.configPanel;
	}

	/**
	 * return the array of panel that composed the config screen this method is called everytime the config panel is set
	 * visible
	 * 
	 * @return the array of panel that composed the config screen
	 */
	protected abstract JPanel[] getConfigPanels();

	/**
	 * 
	 * @return a string array containing the title of the config panels
	 */
	protected abstract String[] getConfigPanelsTitle();

	/**
	 * 
	 * @return the extra height for the header
	 */
	protected int getExtraHeaderHeight() {
		return 0;
	}

	protected AbstractLockablePanel getLayerFooter() {
		if (this.layerFooter == null) {
			this.layerFooter = new DefaultLockablePanel();
			this.layerFooter.setName("lockableFooter");
			this.layerFooter.getModel().setLockableComponent(getFooter());
		}
		return this.layerFooter;
	}

	public JPanel getFooter() {
		if (this.footer == null) {
			this.footer = new JPanel(new MigLayout("fill,hidemode 3, inset 0 0 0 0 , gap 5 0 0 0", "", ""));

			SicpaLookAndFeelCusto.flagAsHeaderOrFooter(this.footer);
			this.footer.add(new JSeparator(), "north , h 2!");
			this.footer.add(getPowerByLabel(), "east, gapright 0.3cm,gap left 0.8cm,gap bottom 10,gap top 10");
		}
		return this.footer;
	}

	public void setFooter(final JPanel footer) {
		this.layerFooter.getModel().setLockableComponent(footer);
	}

	/**
	 * @return the component that will be added as a header
	 */

	public JComponent getHeader() {
		if (this.header == null) {
			this.header = new JPanel(new MigLayout("fill,inset 0 0 0 0, hidemode 3,gap 0 0 0 0"));
			this.header.add(getLineIdPanel(), "wrap");
			this.header.add(getConfigPasswordPanel(), "wrap,growx");
			this.header.add(getApplicationStatusPanel(), "grow");
			getConfigPasswordPanel().setVisible(false);
			this.header.add(getHeaderInfoPanel(), "south");
		}
		return this.header;
	}

	// left panel + right panel
	protected JPanel getInternalCenterPanel() {
		if (this.internalCenterPanel == null) {
			this.internalCenterPanel = new JPanel(new MigLayout("fill,insets 0 0 0 0, gap 0 0"));
			this.internalCenterPanel.add(getLayereredLeftPanel(), "grow,push");
			this.internalCenterPanel.add(getSeparatorCenterPanel(), "growy , w 2!");
			this.internalCenterPanel.add(getScrollRightPanel(), "grow");
		}
		return this.internalCenterPanel;
	}

	public JSeparator getSeparatorCenterPanel() {
		if (this.separatorCenterPanel == null) {
			this.separatorCenterPanel = new JSeparator(JSeparator.HORIZONTAL);
		}
		return this.separatorCenterPanel;
	}

	/**
	 * the layeredLeftPanel => stack of panels
	 * 
	 * @return
	 */
	public SizeSynchroLayerPanel getLayereredLeftPanel() {
		if (this.layereredLeftPanel == null) {
			this.layereredLeftPanel = new SizeSynchroLayerPanel();
		}
		return this.layereredLeftPanel;
	}

	// wrap the config panel, to old the ability to show old value on components
	private JXLayer<JComponent> getLayerOldValue() {
		if (this.layerOldValue == null) {
			this.layerOldValue = new JXLayer<JComponent>();
			this.oldValueUI = new OldValueUI();
			this.layerOldValue.setUI(this.oldValueUI);
			this.layerOldValue.setView(getConfigPanel());
		}
		return this.layerOldValue;
	}

	/**
	 * @return the action triggered after a maintenance mode logout
	 */
	protected AbstractAction getLogoutExtraAction() {
		return null;
	}

	public AbstractConfigPasswordPanel getConfigPasswordPanel() {
		if (this.configPasswordPanel == null) {
			this.configPasswordPanel = new DefaultConfigPasswordPanel();
			this.configPasswordPanel.setName("configPasswordPanel");
			this.configPasswordPanel.getModel().addConfigPasswordListener(new ConfigPasswordListener() {

				@Override
				public void passwordChecked(final ConfigPasswordEvent evt) {
					configPasswordChecked(evt);
				}

				@Override
				public void canceled() {
					cancelPasswordActionPerformed();
				}
			});
		}

		return this.configPasswordPanel;
	}

	private void configPasswordChecked(final ConfigPasswordEvent evt) {
		if (evt.isValid()) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					getLineIdPanel().setVisible(true);
					getConfigPasswordPanel().setVisible(false);
					cancelPasswordActionPerformed();
					getConfigPanel().setPanelButtonsVisible(true);
					AbstractMachineFrame.this.oldValueUI.storeAllOldValue();
				}
			});
		}
	}

	public JLabel getPowerByLabel() {
		if (this.powerByLabel == null) {
			this.powerByLabel = new JLabel();
			this.powerByLabel.setIcon(new ImageIcon(SicpaLookAndFeelConfig.getPowerByImage()));
		}
		return this.powerByLabel;
	}

	// first page of the config panel
	// to enable the lock as error only on the first page and not all the config
	public AbstractLockingErrorPanel getProdPanel() {
		if (this.prodPanel == null) {
			this.prodPanel = new DefaultLockingErrorPanel();
			this.prodPanel.getModel().setLockedComponent(getInternalCenterPanel());
		}
		return this.prodPanel;
	}

	/**
	 * @return the code to execute when saving the config screen
	 */
	protected SaveTask getSaveConfigTask() {
		return new SaveTask() {
			@Override
			public void save(final int index) {
			}
		};
	}

	protected JScrollPane getScrollRightPanel() {
		if (this.scrollRightPanel == null) {
			this.scrollRightPanel = new JScrollPane();
			setRightPanelPreferredWidth(300);
			this.scrollRightPanel.setBorder(BorderFactory.createEmptyBorder());
		}
		return this.scrollRightPanel;
	}

	public void setRightPanel(final JComponent panel) {
		this.getScrollRightPanel().setViewportView(panel);
	}

	/**
	 * the panel where the fatal error are displayed
	 * 
	 * @return
	 */
	protected AbstractScrollingErrorPanel getScrollingErrorFatal() {
		if (this.scrollingErrorFatal == null) {
			this.scrollingErrorFatal = (AbstractScrollingErrorPanel) createPanel(KEY_FATAL_ERROR);
			this.scrollingErrorFatal.setVisible(false);
			this.scrollingErrorFatal.setName("scrollingErrorFatal");
		}
		return this.scrollingErrorFatal;
	}

	/**
	 * the panel where the fatal minor are displayed
	 * 
	 * @return
	 */
	protected AbstractScrollingErrorPanel getScrollingErrorMinor() {
		if (this.scrollingErrorMinor == null) {
			this.scrollingErrorMinor = (AbstractScrollingErrorPanel) createPanel(KEY_MINOR_ERROR);
			this.scrollingErrorMinor.setVisible(false);
		}
		return this.scrollingErrorMinor;
	}

	/**
	 * @return the action that is triggered on the change type button
	 */
	@SuppressWarnings("serial")
	protected AbstractAction getSelectProductAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getSelectionFlowView().resetSelections();
				getSelectionFlowView().init();
				replaceMainPanel(getSelectionFlowView());
				getSummaryPanel().getModel().setSummary(null);
			}
		};
	}

	public JButton getSelectProductButton() {
		if (this.selectProductButton == null) {
			this.selectProductButton = new JButton(GUIi18nManager.get(I18N_CHANGE_TYPE));
			this.selectProductButton.setName(I18N_CHANGE_TYPE);
			this.selectProductButton.setPreferredSize(new Dimension(175, 75));
			this.selectProductButton.addActionListener(getSelectProductAction());
		}
		return this.selectProductButton;
	}

	/**
	 * @return the sicpa logo displayed in the top right corner
	 */
	protected Image getSicpaLogo() {
		return SicpaLookAndFeelConfig.getSicpaLogoPccFrame();
	}

	/**
	 * @return the action that is triggered on the start button
	 */
	protected abstract AbstractAction getStartAction();

	public JButton getStartButton() {
		if (this.startButton == null) {
			Dimension bDimSTART = new Dimension(110, 110);
			this.startButton = new StartStopButton(eStartStop.START);
			this.startButton.setMinimumSize(bDimSTART);
			this.startButton.setPreferredSize(bDimSTART);
			this.startButton.setMaximumSize(bDimSTART);
			this.startButton.addActionListener(getStartAction());

			this.startButton.setText(GUIi18nManager.get(I18N_START));
			this.startButton.setName(I18N_START);
		}
		return this.startButton;
	}

	/**
	 * @return the action that is triggered on the stop button
	 */
	protected abstract AbstractAction getStopAction();

	public JButton getStopButton() {
		if (this.stopButton == null) {
			this.stopButton = new StartStopButton(eStartStop.STOP);
			Dimension bDimSTART = new Dimension(110, 110);

			this.stopButton.setMinimumSize(bDimSTART);
			this.stopButton.setPreferredSize(bDimSTART);
			this.stopButton.setMaximumSize(bDimSTART);
			this.stopButton.addActionListener(getStopAction());
			this.stopButton.setText(GUIi18nManager.get(I18N_STOP));
			this.stopButton.setName(I18N_STOP);
		}
		return this.stopButton;
	}

	public AbstractMessagesPanel getWarningPanel() {
		if (this.warningPanel == null) {
			this.warningPanel = (AbstractMessagesPanel) createPanel(KEY_WARNING);
			this.warningPanel.setName("warningPanel");
		}
		return this.warningPanel;
	}

	private JPanel getWrappingHeader() {
		if (this.wrappingHeader == null) {
			this.wrappingHeader = new JPanel();
			this.wrappingHeader.setLayout(new MigLayout("inset 0 0 0 0"));
			// Logo
			{
				JPanel logoHeader = new JPanel(new MigLayout("inset 0 0 0 0"));
				SicpaLookAndFeelCusto.flagAsHeaderOrFooter(logoHeader);
				Image img = getSicpaLogo();
				if (img != null) {
					logoHeader.add(new JLabel(new ImageIcon(getSicpaLogo())));
					this.wrappingHeader.add(logoHeader, "east");
				}
			}
			SicpaLookAndFeelCusto.flagAsHeaderOrFooter(this.wrappingHeader);
		}
		return this.wrappingHeader;
	}

	protected JPanel getPanelToLockFrame() {
		if (this.panelToLockFrame == null) {
			this.panelToLockFrame = new JPanel();
			this.panelToLockFrame.setLayout(new MigLayout("inset 0 0 0 0,fill,hidemode 3"));
			this.panelToLockFrame.add(getWrappingHeader(), "north, h " + (111 + getExtraHeaderHeight()) + "!");
			this.panelToLockFrame.add(new JSeparator(), "north, h 2!");
			this.panelToLockFrame.add(getLayerFooter(), "south");

			this.panelToLockFrame.add(getCenterPanel(), "push,grow");
			this.panelToLockFrame.add(getPanelEast(), "grow,east, w " + getEastPanelWidth() + "!");
		}
		return this.panelToLockFrame;
	}

	protected int getEastPanelWidth() {
		return 250;
	}

	protected AbstractLockablePanel getLayerLockLockAll() {
		if (this.layerLockLockAll == null) {
			this.layerLockLockAll = new DefaultLockablePanel();
		}
		return this.layerLockLockAll;
	}

	protected void initGUI() {
		WindowsUtils.hideDecoration(this);
		SicpaLookAndFeelCusto.turnOffScrollPreview();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().add(getLayerLockLockAll());
		getLayerLockLockAll().getModel().setLockableComponent(getPanelToLockFrame());

		buildAlwaysHereFooterButton();
		buildFooter();

		// allow to have the right side inset correct in the footer
		this.footer.add(Box.createHorizontalStrut(0));
		setHeader(getHeader());
		getConfigPanel();

		buildLayeredLeftPanel();
		buildRightPanel();
		if (this.scrollRightPanel.getViewport().getView() == null) {
			setRightPanelPreferredWidth(0);
		}

		// keep the exit button at the right place
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				thisComponentResized();
			}
		});
		getLayeredPane().add(getProgresPanel());
		getLayeredPane().setLayer(getProgresPanel(), JLayeredPane.DRAG_LAYER - 1);

		// put the exit panel above everything
		getLayeredPane().add(getConfirmationUnblockablePanel());
		getLayeredPane().setLayer(getConfirmationUnblockablePanel(), JLayeredPane.FRAME_CONTENT_LAYER + 50);
		getLayereredLeftPanel().addComponentListener(
				new SizeSynchronizer(getLayereredLeftPanel(), getConfirmationUnblockablePanel()) {

					@Override
					public void componentResized(final ComponentEvent e) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								// override because the srd and dst don 't have the same direct parent as normally
								// expected by the size synchronizer
								dst.setBounds(getConfirmationUnblockablePanel().getX(),
										getConfirmationUnblockablePanel().getY(), src.getWidth(), src.getHeight());
								if (getParent() != null) {
									dst.getParent().validate();
								}
							}
						});
					}

				});
		setSize(1024, 768);
	}

	protected abstract void buildRightPanel();

	/**
	 * @return true if the cancel button should be visible false otherwise for the specific index
	 */
	protected boolean isConfigCancellable(final int index) {
		return true;
	}

	public boolean isConfigModified() {
		return getConfigPanel().isModified();
	}

	/**
	 * @return true if the panel button should be visible false otherwise
	 */
	protected boolean isConfigPanelNavigBarVisible() {
		return true;
	}

	/**
	 * @return true if the validate button should be visible false otherwise for the specific index
	 */
	protected boolean isConfigValidateable(final int index) {
		return true;
	}

	/**
	 * @return true if the maintenance mode is currently in use
	 */
	public boolean isMaintenanceMode() {
		return getConfigPanel().isPanelButtonVisible();
	}

	/**
	 * @return true if the maintenance mode should be currently available
	 */
	protected boolean isMaintenanceModeAvailable() {
		return true;
	}

	protected void lineIDLabelMouseClicked() {
		if (!this.getConfigPanel().isPanelButtonVisible() && isMaintenanceModeAvailable()) {
			getLineIdPanel().setVisible(false);
			getConfigPasswordPanel().setVisible(true);
			getConfigPasswordPanel().grabFocus();
		}
	}

	protected void logoutActionPerformed() {
		getConfigPanel().setPanelButtonsVisible(false);
		getConfigPanel().showPanel(0);

		// revalidate later, even if we are already in the EDT
		// else it doesn t take into account the new size because of the
		// invisible button
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getLayereredLeftPanel().revalidate();
			}
		});

		AbstractAction logoutaction = getLogoutExtraAction();
		if (logoutaction != null) {
			logoutaction.actionPerformed(null);
		}
	}

	/**
	 * replace the main panel by the given one
	 * 
	 * @param panel
	 */
	public void replaceMainPanel(final JComponent panel) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				AbstractMachineFrame.this.prodPanel.getModel().setLockedComponent(panel);
				AbstractMachineFrame.this.configPanel.showPanel(0);
			}
		});
	}

	/**
	 * remove the current main panel and replace it by the original one
	 */
	public void resetMainPanel() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				AbstractMachineFrame.this.prodPanel.getModel().setLockedComponent(
						AbstractMachineFrame.this.internalCenterPanel);
				AbstractMachineFrame.this.configPanel.showPanel(0);
			}
		});
	};

	/**
	 * set a custom action after the standard rollback when hidding the config screen
	 * 
	 * @param rollbackExtraAction
	 */
	public void setConfigRollbackExtraAction(final AbstractAction rollbackExtraAction) {
		this.configPanel.setRollbackExtraAction(rollbackExtraAction);
	}

	/**
	 * Set the preferred width of the container of the group panels
	 * 
	 * @param width
	 */
	public void setRightPanelPreferredWidth(final int width) {
		this.scrollRightPanel.setPreferredSize(new Dimension(width, 0));
		this.scrollRightPanel.setMinimumSize(new Dimension(width, 0));
	}

	/**
	 * Set the component that will be display in the header panel
	 * 
	 * @param comp
	 *            the component display as the header
	 */
	public void setHeader(final JComponent comp) {
		if (comp == null) {
			throw new IllegalArgumentException("The JComponent can not be null");
		}
		this.wrappingHeader.add(comp, "gapleft 0.5cm,span,push,grow");
		SicpaLookAndFeelCusto.flagAsHeaderOrFooter(comp);
	}

	/**
	 * set if the config screen should be hidden after saving
	 * 
	 * @param hideAfterValidate
	 */
	protected void setHideConfigAfterValidate(final boolean hideAfterValidate) {
		this.configPanel.setHideAfterValidate(hideAfterValidate);
	}

	/**
	 * set the power by sicpa icon visible or not
	 * 
	 * @param flag
	 */
	public void setPowerByIconVisible(final boolean flag) {
		this.powerByLabel.setVisible(flag);
	}

	/**
	 * listen to value change in the config panel
	 * 
	 * @param comp
	 *            the JToggleButton/JCheckBox/JRadioButton/JSpinner/JSlider/ JComboBox
	 * @param rollback
	 *            true to reset the value when closing the config screen
	 */
	public void trackConfigModification(final JComponent comp, final boolean rollback) {
		if (comp instanceof JComboBox) {
			this.configPanel.trackModification((JComboBox) comp, rollback);
		} else if (comp instanceof AbstractButton) {
			this.configPanel.trackModification((AbstractButton) comp, rollback);
		} else if (comp instanceof JSlider) {
			this.configPanel.trackModification((JSlider) comp, rollback);
		} else if (comp instanceof JSpinner) {
			this.configPanel.trackModification((JSpinner) comp, rollback);
		} else {
			throw new IllegalArgumentException(comp.getClass() + " is not supported");
		}
	}

	/**
	 * track the old value of a given component<br>
	 * 
	 * @param wrapper
	 * @param xOffset
	 * @param yOffset
	 */
	public void trackOldValue(final OldValueWrapper wrapper, final int xOffset, final int yOffset) {
		this.oldValueUI.addTrackedComponent(wrapper, xOffset, yOffset, wrapper);
	}

	public void trackOldValue(final OldValueWrapper wrapper, final JComponent locationComp, final int xOffset,
			final int yOffset) {
		this.oldValueUI.addTrackedComponent(wrapper, xOffset, yOffset, locationComp);
	}

	public void trackOldValue(final OldValueWrapper wrapper, final JComponent locationComp, final int xOffset,
			final int yOffset, final Font font, final Color inner, final Color border) {
		this.oldValueUI.addTrackedComponent(wrapper, xOffset, yOffset, locationComp, font, inner, border);
	}

	public void unTrackConfigModification(final JComponent comp) {
		if (comp instanceof JComboBox) {
			this.configPanel.unTrackModification((JComboBox) comp);
		} else if (comp instanceof AbstractButton) {
			this.configPanel.unTrackModification((AbstractButton) comp);
		} else if (comp instanceof JSlider) {
			this.configPanel.unTrackModification((JSlider) comp);
		} else if (comp instanceof JSpinner) {
			this.configPanel.unTrackModification((JSpinner) comp);
		} else {
			throw new IllegalArgumentException(comp.getClass() + " is not supported");
		}
	}

	protected AbstractConfirmationPanel getConfirmationPanel() {
		if (this.confirmationPanel == null) {
			this.confirmationPanel = (AbstractConfirmationPanel) createPanel(KEY_CONFIRMATION);
		}
		return this.confirmationPanel;
	}

	protected AbstractConfirmationPanel getConfirmationUnblockablePanel() {
		if (this.confirmationUnblockablePanel == null) {
			this.confirmationUnblockablePanel = (AbstractConfirmationPanel) createPanel(KEY_CONFIRMATION);
			this.confirmationUnblockablePanel.setName("exitPanel");
			this.confirmationUnblockablePanel.setVisible(false);
		}
		return this.confirmationUnblockablePanel;
	}

	public AbstractEmergencyPanel getEmergencyPanel() {
		if (this.emergencyPanel == null) {
			this.emergencyPanel = new DefaultEmergencyPanel();
		}
		return this.emergencyPanel;
	}

	public PaddedButton getExitButton() {
		if (this.exitButton == null) {
			this.exitButton = new PaddedButton(new JButton(GUIi18nManager.get(I18N_EXIT))) {
				private static final long serialVersionUID = 1L;

				@Override
				public void setVisible(final boolean flag) {
					super.setVisible(flag);
					getExitBackgroundButton().setVisible(flag);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							thisComponentResized();
							getExitButton().revalidate();
						}
					});
				}
			};
			this.exitButton.getButton().setName(I18N_EXIT);
			this.exitButton.getButton().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					exitButtonActionPerformed();
				}
			});
			getLayeredPane().add(this.exitButton);
			getLayeredPane().setLayer(this.exitButton, JLayeredPane.DRAG_LAYER - 5);
			this.exitButton.setVisible(false);
		}
		return this.exitButton;
	}

	protected void exitButtonActionPerformed() {
		getController().lockFrame(true, "exit");
		getController().askExit(new ConfirmationCallback() {
			@Override
			public void confirmationTaken(final ConfirmationEvent evt) {
				if (evt.isConfirmed()) {
					closeFrame();
				} else {
					getController().lockFrame(false, "exit");
				}
			}
		});
	}

	// relocate the exit button as it is on the layered pane
	private void thisComponentResized() {
		Point p = SwingUtilities.convertPoint(getFooter(), getExitBackgroundButton().getLocation(),
				AbstractMachineFrame.this.getLayeredPane());
		getExitButton()
				.setBounds(p.x, p.y, getExitBackgroundButton().getWidth(), getExitBackgroundButton().getHeight());
	}

	/**
	 * by default system.exit(0)
	 * 
	 * @return
	 */
	protected AbstractAction getExitCleanUpAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				ThreadUtils.sleepQuietly(1000);
				System.exit(0);
			}
		};
	};

	protected JComponent getExitBackgroundButton() {
		if (this.exitBackgroundButton == null) {
			this.exitBackgroundButton = new JComponent() {
				private static final long serialVersionUID = 1L;

			};
			this.exitBackgroundButton.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentMoved(final ComponentEvent e) {
					exitBackgroundButtonComponentMoved();
				}

				@Override
				public void componentResized(final ComponentEvent e) {
					exitBackgroundButtonComponentResized();
				};

			});
			getExitButton();
		}
		return this.exitBackgroundButton;
	}

	private void exitBackgroundButtonComponentResized() {
		thisComponentResized();
	}

	private void exitBackgroundButtonComponentMoved() {
		thisComponentResized();
	}

	public void closeFrame() {
		RectanglesTransition close = new RectanglesTransition(this);
		close.setEndAction(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				AbstractAction action = getExitCleanUpAction();
				if (action != null) {
					action.actionPerformed(e);
				}
			}
		});
		close.startCloseTransition();
	}

	public AbstractApplicationStatusPanel getApplicationStatusPanel() {
		if (this.applicationStatusPanel == null) {
			this.applicationStatusPanel = (AbstractApplicationStatusPanel) createPanel(KEY_APPLICATION_STATUS);
			SicpaLookAndFeelCusto.flagAsHeaderOrFooter(this.applicationStatusPanel);
		}
		return this.applicationStatusPanel;
	}

	protected JComponent createPanel(final PanelKey key) {
		Class<? extends JComponent> clazz = mapPanelClasses.get(key);
		if (clazz == null) {
			throw new IllegalArgumentException("no class for this key:" + key);
		}
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public AbstractLineIdPanel getLineIdPanel() {
		if (this.lineIdPanel == null) {
			this.lineIdPanel = (AbstractLineIdPanel) createPanel(KEY_LINE_ID);
			this.lineIdPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(final MouseEvent e) {
					lineIDLabelMouseClicked();
				}
			});
		}
		return this.lineIdPanel;
	}

	public AbstractSelectionFlowView getSelectionFlowView() {

		if (this.selectionFlowView == null) {

			this.selectionFlowView = (AbstractSelectionFlowView) createPanel(KEY_SELECTION_FLOW);
		}
		return this.selectionFlowView;
	}

	public AbstractSummaryPanel getSummaryPanel() {
		if (this.summaryPanel == null) {
			this.summaryPanel = (AbstractSummaryPanel) createPanel(KEY_SUMMARY);
		}
		return this.summaryPanel;
	}

	private MessagesListener messagelistener;

	private MessagesListener getMessagelistener() {
		if (this.messagelistener == null) {
			this.messagelistener = new MessagesListener() {
				@Override
				public void reseted() {
					ThreadUtils.invokeLater(new Runnable() {
						@Override
						public void run() {
							getSeparatorSummary().setVisible(false);
						}
					});
				}

				@Override
				public void messageUpdated(final MessageEventUpdate evt) {
				}

				@Override
				public void messageRemoved(final MessageEvent evt) {
					ThreadUtils.invokeLater(new Runnable() {
						@Override
						public void run() {
							getSeparatorSummary().setVisible(getWarningPanel().getModel().getMessages().size() != 0);
						}
					});
				}

				@Override
				public void messageAdded(final MessageEvent evt) {
					ThreadUtils.invokeLater(new Runnable() {

						@Override
						public void run() {
							getSeparatorSummary().setVisible(true);
						}
					});
				}
			};
		}
		return this.messagelistener;
	}

	public void setProductionDataSelectionModel(final AbstractSelectionFlowModel model) {
		// add the summary panel here so it only get added if using the
		// selection flow screen
		if (getSummaryPanel().getParent() == null) {
			// if the summary has not been added somewhere else
			getPanelEast().add(getSummaryPanel(), "north");
			getPanelEast().add(getSeparatorSummary(), "north, h 2!");
			getSeparatorSummary().setVisible(false);
		}

		// remove the separator if no warning
		if (this.messagelistener != null) {
			getWarningPanel().getModel().removeMessageListener(getMessagelistener());
		}
		getWarningPanel().getModel().addMessageListener(getMessagelistener());

		getSelectionFlowView().setModel(model);
		model.addSelectionFlowListener(new SelectionFlowAdapter() {
			@Override
			public void selectionComplete(final SelectionFlowEvent evt) {
				productionDataSelectionComplete();
			}

			@Override
			public void cancelSelection() {
				productionDataSelectionCancel();
			}

			@Override
			public void selectionChanged(final SelectionFlowEvent evt) {
				productionSelectionChanged(evt);
			}
		});
		getSummaryPanel().setVisible(true);
	}

	private void productionSelectionChanged(final SelectionFlowEvent evt) {
		if (getSelectionFlowView().isShowing()) {
			getSummaryPanel().getModel().setSummary(getSelectionFlowView().getModel().getSelectedValues());
		}
	}

	private SelectableItem[] selections;

	private void productionDataSelectionComplete() {
		if (getSelectionFlowView().isShowing()) {
			resetMainPanel();
			this.selections = getSelectionFlowView().getModel().getSelectedValues();
			getSummaryPanel().getModel().setSummary(this.selections);
		}
	}

	private void productionDataSelectionCancel() {
		resetMainPanel();
		getSummaryPanel().getModel().setSummary(getSelectionFlowView().getModel().getPreviousSelection());
	}

	public JPanel getPanelEast() {
		if (this.panelEast == null) {
			this.panelEast = new JPanel(new MigLayout("fill,inset 0 0 0 0"));
			this.panelEast.add(getSeparatorWarning(), "west, w 2!");

			// they are set visible when setting the selection model
			getSummaryPanel().setVisible(false);
			getSeparatorSummary().setVisible(false);
			this.panelEast.add(getWarningPanel(), "grow,push,span");
			this.panelEast.setName("panelEast");
		}
		return this.panelEast;
	}

	protected JSeparator getSeparatorSummary() {

		if (this.separatorSummary == null) {
			this.separatorSummary = new JSeparator(JSeparator.HORIZONTAL);
		}
		return this.separatorSummary;
	}

	protected JSeparator getSeparatorWarning() {
		if (this.separatorWarning == null) {
			this.separatorWarning = new JSeparator(JSeparator.VERTICAL);
			this.separatorWarning.setName("separatorForWarningPanel");
		}
		return this.separatorWarning;
	}

	protected AbstractHeaderInfoPanel getHeaderInfoPanel() {
		if (this.headerInfoPanel == null) {
			this.headerInfoPanel = (AbstractHeaderInfoPanel) createPanel(KEY_INFO_HEADER_INFO_PANEL);
			SicpaLookAndFeelCusto.flagAsHeaderOrFooter(this.headerInfoPanel);
		}
		return this.headerInfoPanel;
	}

	public AbstractProgressView getProgresPanel() {

		if (this.progressPanel == null) {
			this.progressPanel = (AbstractProgressView) createPanel(KEY_PROGRESS_PANEL);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					getProgresPanel().setBounds(5, getHeader().getHeight(), 100, 100);
				}
			});
			this.progressPanel.setName("taskRunningPanel");
		}
		return this.progressPanel;
	}

	public AbstractIdInputView getBarcodePanel() {
		if (this.barcodePanel == null) {
			this.barcodePanel = (AbstractIdInputView) createPanel(KEY_BARCODE_PANEL);
		}
		return this.barcodePanel;
	}
}
