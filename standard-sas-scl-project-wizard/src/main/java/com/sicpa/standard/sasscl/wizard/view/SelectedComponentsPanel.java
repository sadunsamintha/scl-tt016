package com.sicpa.standard.sasscl.wizard.view;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetContext;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.pushingpixels.trident.Timeline;

import com.sicpa.standard.common.log.StdLogger;
import com.sicpa.standard.gui.components.dnd.DnDFailureCallBack;
import com.sicpa.standard.gui.components.dnd.DnDGhostManager;
import com.sicpa.standard.gui.components.dnd.DnDStartStopCallback;
import com.sicpa.standard.gui.components.dnd.DnDSuccessCallBack;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.utils.PaintUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.context.IContextChangeListener;
import com.sicpa.standard.sasscl.wizard.context.ProjectContext;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.CameraCognexPanel;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.CustomMessageMappingPanel;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.IReplaceableComponent;
import com.sicpa.standard.sasscl.wizard.view.actionPanel.PlcJBeckPanel;
import com.sicpa.standard.sasscl.wizard.view.dnd.ActionDataFlavor;
import com.sicpa.standard.sasscl.wizard.view.dnd.TransferableAction;

public class SelectedComponentsPanel extends JPanel implements DnDSuccessCallBack {

	public static enum FeedbackInfoDisplay {
		none(""), dropin("Drop components here\nto add them to your project"), droupout(
				"Drop components outside\nto remove them");

		String text;

		private FeedbackInfoDisplay(String text) {
			this.text = text;
		}
	}

	private static final long serialVersionUID = 1L;
	private static StdLogger logger = new StdLogger(SelectedComponentsPanel.class);

	private JPanel panel;

	private JXLayer<JComponent> xlayer;
	private AbstractLayerUI<JComponent> layerUI;

	private FeedbackInfoDisplay info;
	private String textInfo;

	public SelectedComponentsPanel() {
		initGUI();
		ProjectContext.addListener(new IContextChangeListener() {
			@Override
			public void contextChanged(PropertyChangeEvent evt) {
				ThreadUtils.invokeLater(new Runnable() {
					@Override
					public void run() {
						removeUnavailableComponents();
					}
				});
			}
		});
	}

	private void removeUnavailableComponents() {
		for (Component comp : getPanel().getComponents()) {
			if (comp instanceof IActionProvider) {
				if (!((IActionProvider) comp).isAvailable(ProjectContext.getApplicationType())) {
					getPanel().remove(comp);
				}
			}
		}
		getPanel().revalidate();
		getPanel().repaint();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill"));
		add(getXlayer(), "grow");

		DnDGhostManager.enableDrop(this, new ActionDataFlavor(), this);

		setBorder(BorderFactory.createTitledBorder("Selected components"));

		addDefaultComponents();

	}

	public JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new MigLayout("wrap 2"));
		}
		return panel;
	}

	@Override
	public void dropSuccess(final DataFlavor flavor, final DropTargetContext context, final Transferable transferable,
			final Point location) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					Class<?> clazz = (Class<?>) transferable.getTransferData(flavor);

					for (Component comp : getPanel().getComponents()) {
						if (clazz.equals(comp.getClass())) {
							// do not add duplicate
							return;
						}
					}

					// remove any component that can be replace by the dropped one
					JComponent comp = (JComponent) clazz.newInstance();
					if (comp instanceof IReplaceableComponent) {
						for (Class<?> c : ((IReplaceableComponent) comp).getReplaceableClasses()) {
							for (Component replaceComp : getPanel().getComponents()) {
								if (replaceComp.getClass().equals(c)) {
									getPanel().remove(replaceComp);
								}
							}
						}
					}
					addComponentWithDropCapability(comp);
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		});
	}

	private void addDefaultComponents() {
		addComponentWithDropCapability(new CameraCognexPanel());
		addComponentWithDropCapability(new PlcJBeckPanel());
		addComponentWithDropCapability(new CustomMessageMappingPanel());
	}

	public void addComponentWithDropCapability(JComponent comp) {
		getPanel().add(comp);
		createDrop(comp);
		getPanel().revalidate();
		getPanel().repaint();
	}

	public void createDrop(final JComponent comp) {
		DnDGhostManager.enableDrag(comp, new TransferableAction(comp.getClass()), new DnDStartStopCallback() {

			@Override
			public void dragStart(Transferable transferable, Point location) {
				setInfo(FeedbackInfoDisplay.droupout);
			}

			@Override
			public void dragStop(Transferable transferable, Point location) {
				setInfo(FeedbackInfoDisplay.none);
			}
		}, new DnDFailureCallBack() {
			@Override
			public void dropFailed(Transferable transferable, Point location) {
				getPanel().remove(comp);
				getPanel().revalidate();
				getPanel().repaint();
			}
		});
	}

	public List<IConfigAction> getActions() {
		List<IConfigAction> res = new ArrayList<IConfigAction>();
		for (Component comp : getPanel().getComponents()) {
			if (comp instanceof IActionProvider) {
				res.add(((IActionProvider) comp).provide());
			}
		}
		return res;
	}

	public void setInfo(FeedbackInfoDisplay info) {
		this.info = info;
		if (info == FeedbackInfoDisplay.none) {
			startHideAnim();
		} else {
			startShowAnim();
			textInfo = info.text;
		}
	}

	private float animProgress = 0;

	public void setAnimProgress(float animProgress) {
		this.animProgress = animProgress;
		repaint();
	}

	public float getAnimProgress() {
		return animProgress;
	}

	private void startShowAnim() {
		startAnim(1f);
	}

	private void startHideAnim() {
		startAnim(0f);
	}

	private void startAnim(float to) {
		if (animation != null) {
			animation.abort();
		}
		animation = new Timeline(this);
		animation.addPropertyToInterpolate("animProgress", getAnimProgress(), to);
		animation.setDuration(200);
		animation.play();

	}

	private Timeline animation;

	public JXLayer<JComponent> getXlayer() {
		if (xlayer == null) {
			JScrollPane scroll = new JScrollPane(getPanel());
			scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
			xlayer = new JXLayer<JComponent>(scroll);
			layerUI = new AbstractLayerUI<JComponent>() {
				protected void paintLayer(Graphics2D g, JXLayer<? extends JComponent> layer) {
					super.paintLayer(g, layer);
					if (info != null) {
						PaintUtils.turnOnQualityRendering(g);
						g.setComposite(AlphaComposite.SrcOver.derive(0.3f * animProgress));
						g.setColor(SicpaColor.BLUE_ULTRA_LIGHT.darker());
						g.fillRect(0, 0, layer.getWidth(), layer.getHeight());
						PaintUtils.drawMultiLineText(g, textInfo, layer.getWidth(), true, 0,
								layer.getHeight() / 2 - 10, SicpaColor.BLUE_DARK);
					}
				};

			};
			xlayer.setUI(layerUI);
		}
		return xlayer;
	}
}
