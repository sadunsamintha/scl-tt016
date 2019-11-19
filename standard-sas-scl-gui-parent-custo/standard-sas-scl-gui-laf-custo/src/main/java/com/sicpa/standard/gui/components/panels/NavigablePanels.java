package com.sicpa.standard.gui.components.panels;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.divxdede.swing.busy.JBusyComponent;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton.Direction;
import com.sicpa.standard.gui.components.layeredComponents.lock.lockablePanel.DefaultLockablePanel;
import com.sicpa.standard.gui.components.preview.PreviewCallback;
import com.sicpa.standard.gui.components.preview.PreviewDialog;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class NavigablePanels extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final String I18N_PREVIEW = GUIi18nManager.SUFFIX + "machine.previewButton";

	protected JButton previousButton;
	protected JButton nextButton;

	protected JPanel panelButton;

	protected DefaultLockablePanel[] panels;
	protected String[] titles;

	private int selectedIndex;

	private DefaultLockablePanel currentSelectedPanel;

	private JLabel title;

	private JBusyComponent<JPanel> busyPanel;
	private JPanel mainPanel;

	private Map<Integer, NavigationAction> nextActionMap;
	private Map<Integer, NavigationAction> previousActionMap;

	private JButton previewButton;

	public NavigablePanels() {
		this.nextActionMap = new HashMap<Integer, NavigationAction>();
		this.previousActionMap = new HashMap<Integer, NavigationAction>();
		this.selectedIndex = -1;
		initGUI();
	}

	public void initGUI() {
		setLayout(new BorderLayout());
		add(getBusyPanel(), BorderLayout.CENTER);
		// DropShadowBorder border = new DropShadowBorder();
		// setBorder(border);
	}

	public JButton getPreviousButton() {
		if (this.previousButton == null) {
			this.previousButton = new DirectionButton(Direction.LEFT) {
				@Override
				public void applyComponentOrientation(final java.awt.ComponentOrientation o) {
				};
			};
			this.previousButton.setName("showPreviousConfigPanelButton");
			this.previousButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					previousButtonActionPerformed();
				}
			});
		}
		return this.previousButton;
	}

	private void previousButtonActionPerformed() {
		boolean showPrevious = true;
		NavigationAction action = this.previousActionMap.get(this.selectedIndex);
		if (action != null) {
			showPrevious = action.actionPerformed();
		}

		if (showPrevious) {
			showPanel(this.selectedIndex - 1);
		}
	}

	public JButton getNextButton() {
		if (this.nextButton == null) {
			this.nextButton = new DirectionButton(Direction.RIGHT) {
				@Override
				public void applyComponentOrientation(final java.awt.ComponentOrientation o) {
				};
			};
			this.nextButton.setName("showNextConfigPanelButton");
			this.nextButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					nextButtonActionPerformed();
				}
			});
		}
		return this.nextButton;
	}

	private void nextButtonActionPerformed() {
		NavigationAction action = this.nextActionMap.get(this.selectedIndex);
		boolean showNext = true;
		if (action != null) {
			showNext = action.actionPerformed();
		}
		if (showNext) {
			showPanel(this.selectedIndex + 1);
		}
	}

	public JPanel getPanelButton() {
		if (this.panelButton == null) {
			this.panelButton = new JPanel(new MigLayout("ltr,inset 0 0 0 0", "", ""));
			this.panelButton.add(getPreviousButton(), "id previous,gapleft 3, w 100!, h 40!");
			this.panelButton.add(getNextButton(), "id next,w 100!,h 40!");

			// this.panelButton.add(Box.createGlue(), "push");
			this.panelButton.add(getTitle(), "id title, pushx");
			// this.panelButton.add(Box.createGlue(), "push");

			this.panelButton.add(getPreviewButton(), "id preview, gaptop 10, growx");

			SicpaLookAndFeelCusto.flagAsHeaderOrFooter(this.panelButton);
		}
		return this.panelButton;
	}

	public void setPanels(final JPanel[] panelsIn, final String[] titles) {
		if (panelsIn == null) {
			throw new IllegalArgumentException("The JPanel[] cannot be null");
		}

		this.panels = new DefaultLockablePanel[panelsIn.length];

		DefaultLockablePanel jx;

		try {
			// HACK to have the correct preview--
			JWindow dialog = new JWindow();
			dialog.setLocation(-5000, -5000);
			dialog.setSize(700, 350);
			SicpaLookAndFeelCusto.flagAsWorkArea((JComponent) dialog.getContentPane());
			dialog.getContentPane().setLayout(new BorderLayout());
			// -----------

			for (int i = 0; i < this.panels.length; i++) {
				jx = new DefaultLockablePanel();
				jx.getModel().setLockableComponent(panelsIn[i]);
				this.panels[i] = jx;

				// HACK to have the correct preview
				dialog.getContentPane().add(jx, BorderLayout.CENTER);
				dialog.setVisible(true);
				dialog.getContentPane().doLayout();
				dialog.setVisible(false);
				SicpaLookAndFeelCusto.flagAsWorkArea(panelsIn[i]);
			}
			dialog.removeAll();
			dialog.dispose();
		} catch (HeadlessException e) {

		}

		this.titles = titles;
		showPanel(0);
	}

	public void showPanel(final JComponent panel) {
		int i = 0;
		for (JComponent p : this.panels) {
			if (panel == p) {
				showPanel(i);
				return;
			}
			i++;
		}
	}

	public void showPanel(final int i) {
		if (i < this.panels.length && i >= 0) {
			if (this.currentSelectedPanel != null) {
				this.mainPanel.remove(this.currentSelectedPanel);
			}
			int old = this.selectedIndex;

			this.currentSelectedPanel = this.panels[i];

			this.mainPanel.add(this.currentSelectedPanel, "grow ,span,push");
			if (this.titles != null && i < this.titles.length) {
				this.title.setText(this.titles[i]);
			}

			this.previousButton.setVisible(i != 0);
			this.nextButton.setVisible(i != this.panels.length - 1);
			repaint();
			validate();

			this.selectedIndex = i;
			if (i != old) {
				firePanelIndexListener(old, i);

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						revalidate();
					}
				});
			}
		}

		if (this.currentSelectedPanel.getComponentOrientation() != getComponentOrientation()) {
			this.currentSelectedPanel.applyComponentOrientation(getComponentOrientation());
		}
	}

	private List<PropertyChangeListener> listenersIndexPanel;

	public void addPanelIndexListener(final PropertyChangeListener listener) {
		if (this.listenersIndexPanel == null) {
			this.listenersIndexPanel = new ArrayList<PropertyChangeListener>();
		}
		this.listenersIndexPanel.add(listener);
	}

	private void firePanelIndexListener(final int oldp, final int newp) {
		if (this.listenersIndexPanel != null) {
			for (PropertyChangeListener l : this.listenersIndexPanel) {
				l.propertyChange(new PropertyChangeEvent(this, "panelIndex", oldp, newp));
			}
		}
	}

	public JLabel getTitle() {
		if (this.title == null) {
			this.title = new JLabel();
		}
		return this.title;
	}

	public JPanel getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = new JPanel(new MigLayout("inset 0 0 0 0,hidemode 3,fill,gap 0px 0px"));
			this.mainPanel.add(getPanelButton(), "south,h 1.4cm!");
		}
		return this.mainPanel;
	}

	public JBusyComponent<JPanel> getBusyPanel() {
		if (this.busyPanel == null) {
			this.busyPanel = new JBusyComponent<JPanel>(getMainPanel());
		}
		return this.busyPanel;
	}

	public void setBusy(final boolean busy) {
		this.busyPanel.setBusy(busy);
	}

	public void setNextActionAt(final int i, final NavigationAction action) {
		if (action == null) {
			throw new IllegalArgumentException("The action cannot be null");
		}
		if (i < 0 || i >= this.panels.length) {
			throw new IllegalArgumentException("The index must be between 0 and " + (this.panels.length - 1));
		}
		this.nextActionMap.put(i, action);
	}

	public void setPreviousActionAt(final int i, final NavigationAction action) {
		if (action == null) {
			throw new IllegalArgumentException("The action cannot be null");
		}
		if (i < 0 || i >= this.panels.length) {
			throw new IllegalArgumentException("The index must be between 0 and " + (this.panels.length - 1));
		}
		this.previousActionMap.put(i, action);
	}

	public boolean isEnabledAt(final int index) {
		return this.panels[index].isEnabled();
	}

	public JComponent getPanelAt(final int index) {
		return this.panels[index];
	}

	public int getSelectedIndex() {
		return this.selectedIndex;
	}

	public void setSelectedIndex(final int index) {
		showPanel(index);
	}

	public int getPanelsCount() {
		return this.panels.length;
	}

	public String getTitleAt(final int index) {
		return this.titles[index];
	}

	public void disablePanelAt(final int index) {
		this.panels[index].getModel().lock(true, " ");
	}

	public void setPanelButtonsVisible(final boolean flag) {
		getPanelButton().setVisible(flag);
	}

	public boolean isPanelButtonVisible() {
		return getPanelButton().isVisible();
	}

	public void setComponentAt(final int index, final JComponent comp, final String title) {
		this.panels[index].getModel().setLockableComponent(comp);
		this.titles[index] = title;
	}

	public void setPreviewButtonText(final String text) {
		getPreviousButton().setText(text);
	}

	public JButton getPreviewButton() {
		if (this.previewButton == null) {
			this.previewButton = new JButton(GUIi18nManager.get(I18N_PREVIEW));
			this.previewButton.setName(I18N_PREVIEW);
			this.previewButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {

					ArrayList<JComponent> list = new ArrayList<JComponent>();
					for (JComponent c : NavigablePanels.this.panels) {
						list.add(c);
					}

					ArrayList<String> listS = new ArrayList<String>();
					for (String s : NavigablePanels.this.titles) {
						listS.add(s);
					}
					PreviewDialog.showPreviewDialog(NavigablePanels.this, list, listS, new PreviewCallback() {
						@Override
						public void callback(final JComponent comp) {
							showPanel(comp);
						}
					});
				}
			});
		}
		return this.previewButton;
	}
}
