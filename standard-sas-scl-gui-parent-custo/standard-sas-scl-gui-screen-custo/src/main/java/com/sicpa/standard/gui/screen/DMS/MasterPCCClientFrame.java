package com.sicpa.standard.gui.screen.DMS;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.divxdede.swing.busy.JBusyComponent;
import org.jdesktop.animation.transitions.ScreenTransition;
import org.jdesktop.animation.transitions.TransitionTarget;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.joptionPane.AutoCloseOptionPane;
import com.sicpa.standard.gui.components.transition.impl.RectanglesTransition;
import com.sicpa.standard.gui.listener.Draggable;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelConfig;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.DMS.log.LogScreen;
import com.sicpa.standard.gui.screen.DMS.log.UserLog;
import com.sicpa.standard.gui.screen.DMS.log.UserLog.EStatusLog;
import com.sicpa.standard.gui.screen.DMS.mvc.AbstractView;
import com.sicpa.standard.gui.screen.DMS.mvc.utils.JOptionPaneMessageWrapper;
import com.sicpa.standard.gui.screen.DMS.mvc.utils.JoptionPaneMessageAutoCloseWrapper;
import com.sicpa.standard.gui.utils.TextUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.gui.utils.WindowsUtils;

public abstract class MasterPCCClientFrame extends javax.swing.JFrame implements TransitionTarget, AbstractView {

	private static final long serialVersionUID = 1L;
	public static final String I18N_EXIT_CONFIRM_MESSAGE = GUIi18nManager.SUFFIX
			+ "DMS.MPCCClientFrame.exitConfirmMessage";
	public static final String I18N_EXIT_CONFIRM_TITLE = GUIi18nManager.SUFFIX + "DMS.MPCCClientFrame.exitComfirmTitle";

	public static final String PROPERTY_BUSY = "busy";
	public static final String PROPERTY_MESSAGE = "joptionPaneMessage";

	private JPanel header;
	private JLabel labelTitle;
	private JBusyComponent<JComponent> busyMainPanel;
	private JPanel mainPanel;
	private JPanel menuPanel;
	private JProgressBar progressBar;
	private JPanel rootPanel;
	private JXLayer<JComponent> layerLockRoot;
	private LockableUI lockUI;
	private Footer footer;

	public MasterPCCClientFrame() {
		super();
		initGUI();
		Draggable.makeDraggable(getLayerLockRoot(), this);
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		WindowsUtils.hideDecoration(this);
		setIconImage(GraphicsUtilities.createCompatibleTranslucentImage(1, 1));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				exit();
			}
		});

		SicpaLookAndFeelCusto.flagAsWorkArea((JPanel) getContentPane());

		setLayout(new MigLayout("fill,hidemode 3,inset 0 0 0 0, gapy 0"));
		add(getLayerLockRoot(), "grow,push,wrap");
		add(getFooter(), "grow, h 50!");

		setSize();
	}

	private void setSize() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		if (d.width > 1440 && d.height >= 900) {
			setSize(1440, 900);
		} else {
			setSize(d);
		}
	}

	// private void thisComponentRezise()
	// {
	// getStatusPanel().setSize(getWidth() - getStatusPanel().getGapLeft() * 2,
	// getHeight() / 2);
	// getStatusPanel().setLocation(0, getHeight() -
	// getStatusPanel().getHeight());
	// }

	public JPanel getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = new JPanel(new BorderLayout());
			this.mainPanel.add(getMenuPanel(), BorderLayout.CENTER);
		}
		return this.mainPanel;
	}

	public void exit() {
		if (JOptionPane.showConfirmDialog(this, GUIi18nManager.get(I18N_EXIT_CONFIRM_MESSAGE),
				GUIi18nManager.get(I18N_EXIT_CONFIRM_TITLE), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
			new RectanglesTransition(this).startCloseTransition();
		}
	}

	protected JLabel getLabelTitle() {
		if (this.labelTitle == null) {
			this.labelTitle = new JLabel("APPLICATION NAME");
			this.labelTitle.setFont(SicpaFont.getFont(40));
		}
		return this.labelTitle;
	}

	private JPanel getHeader() {
		if (this.header == null) {
			this.header = new JPanel(new MigLayout("fill,inset 0 0 0 0"));
			this.header.add(new JSeparator(), "south, h 2!");
			SicpaLookAndFeelCusto.flagAsHeaderOrFooter(this.header);
			this.header.add(getLabelTitle(), "gap left 5px , gap top 0.3cm,pushx");
			this.header.add(getProgressBar(), "wrap");
			BufferedImage logo = getSicpaLogo();
			if (logo != null) {
				this.header.add(new JLabel(new ImageIcon(logo)), "east");
			}
		}
		return this.header;
	}

	/**
	 * 
	 * @return the sicpa logo displayed in the top right corner
	 */
	protected BufferedImage getSicpaLogo() {
		return SicpaLookAndFeelConfig.getSicpaLogoMasterPccClientFrame();
	}

	@Override
	public void setJMenuBar(final JMenuBar menubar) {
		SicpaLookAndFeelCusto.flagAsHeaderOrFooter(menubar);
		getHeader().add(menubar, "span y, span x,bottom,wrap");
	}

	public JPanel getMenuPanel() {
		if (this.menuPanel == null) {
			this.menuPanel = new JPanel(new MigLayout("fill"));
		}
		return this.menuPanel;
	}

	public void showFirstScreen() {
		replaceMainPanel(this.menuPanel);
	}

	protected GroupActionPanel addDashboardGroup(final String name) {
		GroupActionPanel p = new GroupActionPanel(name);
		getMenuPanel().add(p, "top,push,growx, h 500");
		return p;
	}

	public JBusyComponent<JComponent> getBusyMainPanel() {
		if (this.busyMainPanel == null) {
			this.busyMainPanel = new JBusyComponent<JComponent>(getMainPanel());
		}
		return this.busyMainPanel;
	}

	public void setBusy(final boolean flag) {
		getBusyMainPanel().setBusy(flag);
	}

	public void replaceMainPanel(final JPanel panel) {
		this.nextPanel = panel;
		if (isShowing()) {
			ScreenTransition st = new ScreenTransition(this.mainPanel, this, 500);
			st.start();
		} else {
			setupNextScreen();
		}
	}

	private JPanel nextPanel;

	@Override
	public void setupNextScreen() {
		this.mainPanel.removeAll();
		if (this.nextPanel != null) {
			this.mainPanel.add(this.nextPanel, BorderLayout.CENTER);
		} else {
			this.mainPanel.add(getMenuPanel(), BorderLayout.CENTER);
		}
		this.mainPanel.validate();
		this.mainPanel.repaint();
		this.mainPanel.requestFocusInWindow();
	}

	public void setApplicationName(final String name) {
		this.labelTitle.setText(name);
		setTitle(name);
	}

	public JProgressBar getProgressBar() {
		if (this.progressBar == null) {
			this.progressBar = new JProgressBar();
			this.progressBar.setStringPainted(true);
			this.progressBar.setVisible(false);
		}
		return this.progressBar;
	}

	@Override
	public void modelPropertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PROPERTY_BUSY)) {
			if (evt.getNewValue() instanceof Boolean) {
				setBusy((Boolean) evt.getNewValue());
			}
		} else if (evt.getPropertyName().equals(PROPERTY_MESSAGE)) {
			if (evt.getNewValue() instanceof JoptionPaneMessageAutoCloseWrapper) {
				ThreadUtils.invokeLater(new Runnable() {
					@Override
					public void run() {
						JoptionPaneMessageAutoCloseWrapper m = (JoptionPaneMessageAutoCloseWrapper) evt.getNewValue();
						AutoCloseOptionPane.showMessage(MasterPCCClientFrame.this, m.getMessage(), m.getTitle(),
								m.getSeconds(), m.getType());
					}
				});
			} else if (evt.getNewValue() instanceof JOptionPaneMessageWrapper) {
				ThreadUtils.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPaneMessageWrapper m = (JOptionPaneMessageWrapper) evt.getNewValue();
						JOptionPane.showMessageDialog(MasterPCCClientFrame.this, m.getMessage(), m.getTitle(),
								m.getType());
					}
				});
			}
		} else if (evt.getPropertyName().equals(EStatusLog.failure.toString())
				|| evt.getPropertyName().equals(EStatusLog.success.toString())) {
			if (evt.getNewValue() instanceof UserLog) {
				setStatusBarLog((UserLog) evt.getNewValue());
			}
		}
	}

	public void setStatusBarLog(final UserLog log) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getFooter().setLog(log);
			}
		});
	}

	public abstract LogScreen getLogScreen();

	public abstract Properties getLanguageProperties();

	public abstract Properties getConfigProperties();

	public String getValue(final String key) {
		try {
			if (getLanguageProperties() == null) {
				return key;
			}
			String value = getLanguageProperties().getProperty(key);
			if (value != null) {
				return value;
			} else {
				return key;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
	}

	public JPanel getRootPanel() {
		if (this.rootPanel == null) {
			this.rootPanel = new JPanel();
			SicpaLookAndFeelCusto.flagAsWorkArea(this.rootPanel);
			this.rootPanel.setLayout(new MigLayout("fill,hidemode 3, inset 0 0 0 0"));
			this.rootPanel.add(getHeader(), "growx, h 111!, top, wrap");
			this.rootPanel.add(getBusyMainPanel(), "grow,push");
			this.rootPanel.add(new JSeparator(), "south , h 2!");
		}
		return this.rootPanel;
	}

	public JXLayer<JComponent> getLayerLockRoot() {
		if (this.layerLockRoot == null) {
			this.layerLockRoot = new JXLayer<JComponent>(getRootPanel());
			this.lockUI = new LockableUI() {
				@Override
				protected void paintLayer(final Graphics2D g, final JXLayer<? extends JComponent> c) {
					super.paintLayer(g, c);
					g.setColor(Color.LIGHT_GRAY);
					g.setComposite(AlphaComposite.SrcOver.derive(0.25f));
					g.fillRect(0, 0, c.getWidth(), c.getHeight());
				}
			};
			this.layerLockRoot.setUI(this.lockUI);
		}
		return this.layerLockRoot;
	}

	public void lock() {
		this.lockUI.setLocked(true);
	}

	public void unlock() {
		this.lockUI.setLocked(false);
	}

	public Footer getFooter() {
		if (this.footer == null) {
			this.footer = new Footer();
		}
		return this.footer;
	}

	public class Footer extends JPanel {
		private static final long serialVersionUID = 1L;

		private JLabel labelInfo;
		private JLabel labelDate;

		public Footer() {
			initGUI();
		}

		private void initGUI() {
			setLayout(new MigLayout("fill,inset 0 0 0 0"));
			add(getLabelDate(), "grow");
			add(getLabelInfo(), "grow");
			SicpaLookAndFeelCusto.flagAsHeaderOrFooter(this);
		}

		public JLabel getLabelInfo() {
			if (this.labelInfo == null) {
				this.labelInfo = new JLabel();
				TextUtils.registerLabelForOptimumFont(this.labelInfo, 800);
			}
			return this.labelInfo;
		}

		public void setLog(final UserLog log) {
			if (log.getStatus() == EStatusLog.failure) {
				getLabelInfo().setForeground(SicpaColor.RED);
			} else {
				getLabelInfo().setForeground(SicpaColor.BLUE_MEDIUM);
			}

			getLabelInfo().setIcon(log.getStatus().getIcon());

			String output;
			output = log.getItemCode() + " -- " + getValue(log.getOperation()) + " -- " + getValue(log.getResult());
			getLabelInfo().setText(output);

			String dateTimeFormat = null;
			if (getConfigProperties() != null) {
				dateTimeFormat = getConfigProperties().getProperty("dateTimeFormat");
			}
			SimpleDateFormat sdf;
			if (dateTimeFormat != null) {
				sdf = new SimpleDateFormat(dateTimeFormat);
			} else {
				sdf = new SimpleDateFormat();
			}
			getLabelDate().setText(sdf.format(new Date(System.currentTimeMillis())));
		}

		public JLabel getLabelDate() {
			if (this.labelDate == null) {
				this.labelDate = new JLabel();
			}
			return this.labelDate;
		}

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);

			Color c = SubstanceColorUtilities.getBackgroundFillColor(this);
			SubstanceColorScheme cs = SubstanceColorSchemeUtilities.getColorScheme(null,
					ColorSchemeAssociationKind.SEPARATOR, ComponentState.DEFAULT);
			Color sepaColor = cs.getMidColor();

			Graphics2D g2 = (Graphics2D) g.create();
			Point start = new Point(0, -getHeight() / 2);
			Point end = new Point(0, getHeight());
			Color[] colors = new Color[] { sepaColor, c, c };
			float[] fractions = new float[] { 0, 0.55f, 1 };
			LinearGradientPaint lgp = new LinearGradientPaint(start, end, fractions, colors);
			g2.setPaint(lgp);
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.dispose();
		}
	}
}
