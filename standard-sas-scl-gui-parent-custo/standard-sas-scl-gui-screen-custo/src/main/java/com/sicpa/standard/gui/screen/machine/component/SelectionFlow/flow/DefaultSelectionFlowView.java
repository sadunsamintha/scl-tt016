package com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.jidesoft.swing.DefaultOverlayable;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton.Direction;
import com.sicpa.standard.gui.components.panels.transition.SlideTransitionUI;
import com.sicpa.standard.gui.components.panels.transition.TransitionUI;
import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.DefaultSelectionFlowViewFactory;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectionEvent;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.AbstractSelectionButtonView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.AbstractSelectionView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.SelectionListener;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DefaultSelectionFlowView extends AbstractSelectionFlowView {

	private static final long serialVersionUID = 1L;

	private JLabel title;

	private JButton buttonBack;

	protected PreviousSelectionPanel previousSelection;
	protected JXLayer<JScrollPane> layerScrollPrevious;
	protected JXLayer<JComponent> panelView;
	protected TransitionUI nextUI;
	protected boolean first;
	protected boolean backButtonVisibleForFirstScreen;
	protected boolean usePreviousPanel;
	protected boolean withAnimation;

	public DefaultSelectionFlowView() {
		this(null);
		this.backButtonVisibleForFirstScreen = true;
	}

	public DefaultSelectionFlowView(final AbstractSelectionFlowModel model) {
		super(model, new DefaultSelectionFlowViewFactory());
		this.first = true;
		this.usePreviousPanel = true;
		initGUI();
		this.withAnimation = true;

		addPropertyChangeListener("componentOrientation", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				componentOrientationChanged();
			}
		});
	}

	private void componentOrientationChanged() {
		((SlideTransitionUI) nextUI).setComponentOrientation(getComponentOrientation());
		over.setOverlayLocation(getButtonBack(), DefaultOverlayable.SOUTH_EAST);
		revalidate();
	}

	DefaultOverlayable over;

	private void initGUI() {
		setLayout(new MigLayout("fill,inset 0 0 0 0,hidemode 3"));

		add(getLayerScrollPrevious(), "grow,left,span, h 70!");

		add(getTitle(), "gap left 20,gap top 5,span,split 2");
		JSeparator sepa = new JSeparator();
		sepa.setBackground(SicpaColor.BLUE_DARK);
		add(sepa, "growx,h 2!, gap right 5");

		over = new DefaultOverlayable(getPanelView(), getButtonBack());
		over.setOverlayLocation(getButtonBack(), DefaultOverlayable.SOUTH_WEST);
		over.setOverlayLocationInsets(new Insets(0, -5, 0, 0));
		add(over, "grow,push,span, id view");
	}

	public JXLayer<JScrollPane> getLayerScrollPrevious() {
		if (layerScrollPrevious == null) {
			layerScrollPrevious = SmallScrollBar.createLayerSmallScrollBar(new JScrollPane(getPrevisousSelection()),
					true, false, true,true);
		}
		return layerScrollPrevious;
	}

	public PreviousSelectionPanel getPrevisousSelection() {
		if (this.previousSelection == null) {
			this.previousSelection = new PreviousSelectionPanel();
			this.previousSelection.setName("previousPanel");
			this.previousSelection.getButtonSelectOld().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonSelectOldActionPerformed();
				}
			});
			this.previousSelection.setVisible(false);
		}
		return this.previousSelection;
	}

	private void buttonSelectOldActionPerformed() {
		getModel().restorePreviousSelection();
	}

	public JXLayer<JComponent> getPanelView() {
		if (this.panelView == null) {
			this.nextUI = new SlideTransitionUI();
			this.panelView = new JXLayer<JComponent>();
			this.panelView.setUI(this.nextUI);
			this.panelView.setOpaque(false);
		}
		return this.panelView;
	}

	// move to next
	@Override
	protected void modelPopulateNextComplete(final SelectionFlowEvent evt) {
		if (getModel() == null) {
			return;
		}
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (getModel().getSelectedValues().length == 0) {
					getButtonBack().setVisible(isBackButtonVisibleForFirstScreen());
					getLayerScrollPrevious().setVisible(usePreviousPanel);
				} else {
					getButtonBack().setVisible(true);
					getLayerScrollPrevious().setVisible(false);
				}
				final AbstractSelectionView nextView = createView(evt.getItems());
				nextView.addSelectionListener(new SelectionListener() {
					@Override
					public void selectionChanged(final SelectionEvent evt) {
						getModel().addSelectedItems(evt.getItem());
					}
				});
				boolean shouldstartAnim = getPanelView().getWidth() > 0 && getPanelView().getHeight() > 0
						&& getPanelView().isShowing();
				if (isWithAnimation() && shouldstartAnim && !DefaultSelectionFlowView.this.first) {
					// draw current/previous view
					{
						BufferedImage previousImage = GraphicsUtilities.createCompatibleTranslucentImage(getPanelView()
								.getWidth(), getPanelView().getHeight());
						Graphics2D g = previousImage.createGraphics();
						getPanelView().paint(g);
						g.dispose();
						DefaultSelectionFlowView.this.nextUI.setPreviousImage(previousImage);
					}

					// prepare the next panel
					JComponent oldView = getPanelView().getView();
					getPanelView().setView(nextView);
					// paint image
					{
						BufferedImage nextImage = GraphicsUtilities.createCompatibleTranslucentImage(getPanelView()
								.getWidth(), getPanelView().getHeight());
						Graphics2D g = nextImage.createGraphics();
						getPanelView().validate();
						nextView.beforePreviewInit();
						getPanelView().paint(g);
						g.dispose();
						DefaultSelectionFlowView.this.nextUI.setNextImage(nextImage);
					}

					getPanelView().setView(oldView);
					DefaultSelectionFlowView.this.nextUI.startAnim(evt.isMovingNext());

					new Thread(new Runnable() {
						@Override
						public void run() {
							if (DefaultSelectionFlowView.this.nextUI.getAnimDuration() > 100) {
								ThreadUtils.sleepQuietly(DefaultSelectionFlowView.this.nextUI.getAnimDuration() - 100);
							}
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									getPanelView().setView(nextView);
									getTitle().setText(evt.getTitle());

									getPanelView().validate();
									getPanelView().repaint();
								}
							});
						}
					}).start();

				} else {
					DefaultSelectionFlowView.this.first = false;
					getPanelView().setView(nextView);
					getTitle().setText(evt.getTitle());
					getPanelView().validate();
					getPanelView().repaint();
				}
			}
		});
	}

	@Override
	protected void modelSelectionChanged(final SelectionFlowEvent evt) {
		// if (this.usePreviousPanel) {
		// ThreadUtils.invokeLater(new Runnable() {
		// @Override
		// public void run() {
		// if (getModel().getSelectedValues().length == 0) {
		// getButtonBack().setVisible(isBackButtonVisibleForFirstScreen());
		// }
		// }
		// });
		// }
	}

	public JLabel getTitle() {
		if (this.title == null) {
			this.title = new JLabel();
			this.title.setFont(this.title.getFont().deriveFont(20f));
			this.title.setForeground(SicpaColor.BLUE_DARK);
		}
		return this.title;
	}

	public JButton getButtonBack() {
		if (this.buttonBack == null) {
			this.buttonBack = new DirectionButton(Direction.LEFT);
			this.buttonBack.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonBackActionPerformed();
				}
			});
			this.buttonBack.setMinimumSize(new Dimension(100, 40));
			this.buttonBack.setPreferredSize(new Dimension(100, 40));
			this.buttonBack.setMaximumSize(new Dimension(100, 40));
			this.buttonBack.setVisible(isBackButtonVisibleForFirstScreen());
			this.buttonBack.setName("buttonBack");
		}
		return this.buttonBack;
	}

	private void buttonBackActionPerformed() {
		if (getPanelView().getView() instanceof AbstractSelectionButtonView) {
			if (((AbstractSelectionButtonView) getPanelView().getView()).isAlreadySelected()) {
				return;
			}
		}
		getModel().removeLastSelection();
	}

	@Override
	public void resetSelections() {
		this.first = true;
		super.resetSelections();
	}

	public boolean isBackButtonVisibleForFirstScreen() {
		return this.backButtonVisibleForFirstScreen;
	}

	public void setBackButtonVisibleForFirstScreen(final boolean backVisibleForFirstScreen) {
		this.backButtonVisibleForFirstScreen = backVisibleForFirstScreen;
	}

	@Override
	protected void modelOldSelectionChanged(final SelectionFlowEvent evt) {
		if (this.usePreviousPanel) {
			getPrevisousSelection().populate(evt.getItems());
		}
	}

	public void setUsePreviousPanel(final boolean usePreviousPanel) {
		this.usePreviousPanel = usePreviousPanel;
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getLayerScrollPrevious().setVisible(false);
			}
		});
	}

	public boolean isUsePreviousPanel() {
		return this.usePreviousPanel;
	}

	public TransitionUI getNextUI() {
		return this.nextUI;
	}

	public void setWithAnimation(final boolean withAnimation) {
		this.withAnimation = withAnimation;
	}

	public boolean isWithAnimation() {
		Object o = System.getProperty("selectionFlowView.withAnimation");
		if (o != null) {
			return (Boolean.parseBoolean(o + ""));
		}
		o = UIManager.get("selectionFlowView.withAnimation");
		if (o != null) {
			if (o instanceof Boolean) {
				return (Boolean) o;
			} else if (o instanceof String) {
				return (Boolean.parseBoolean(o + ""));
			}
		}
		return this.withAnimation;
	}
}
