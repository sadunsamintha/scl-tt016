package com.sicpa.standard.gui.screen.machine.component.config;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.GeneralPath;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.ease.Spline;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.buttons.StartStopButton;
import com.sicpa.standard.gui.components.panels.NavigablePanels;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;
import com.sicpa.standard.gui.screen.machine.component.confirmation.ConfirmationCallback;
import com.sicpa.standard.gui.screen.machine.component.confirmation.ConfirmationEvent;
import com.sicpa.standard.gui.utils.PaintUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class ConfigPanel extends NavigablePanels {
	private static final long serialVersionUID = 1L;

	public static final String I18N_EXIT_BUTTON = GUIi18nManager.SUFFIX + "machine.ConfigPanel.ExitButton";
	public static final String I18N_CLOSE_MESSAGE_PARAM_MODIFIED = GUIi18nManager.SUFFIX
			+ "machine.ConfigPanel.closeMessage";
	public static final String I18N_YES_SAVE_BUTTON = GUIi18nManager.SUFFIX + "machine.ConfigPanel.YesButton";
	public static final String I18N_NO_DONT_SAVE_BUTTON = GUIi18nManager.SUFFIX + "machine.ConfigPanel.NoButton";

	private boolean modified;

	private ConfirmButton closeButton;
	private ConfirmButton validateButton;

	private Map<JComponent, Object> mapRollback;

	private SaveTask saveTask;
	private AbstractAction hideAction;

	private boolean hideAfterValidate;
	private boolean closePressed;

	private JButton exitButton;

	private AbstractAction rollbackExtraAction;

	public ConfigPanel() {
		super();
		this.modified = false;
		this.hideAfterValidate = false;
		this.closePressed = false;
		this.mapRollback = new HashMap<JComponent, Object>();
		initGUI();
	}

	@Override
	public void initGUI() {
		super.initGUI();
	}

	@Override
	public JPanel getPanelButton() {
		if (this.panelButton == null) {
			super.getPanelButton();
			this.panelButton.add(getExitButton(), "id exit,growx, gapright 10,hidemode 3");
			this.panelButton.add(getValidateButton(), "id validate,gapleft 10, grow,w 50! , hidemode 3");
			this.panelButton.add(getCloseButton(), "id cancel,grow,w 50!, h 50!, hidemode 3");
		}
		return this.panelButton;
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(600, 500);
				f.getContentPane().setLayout(new BorderLayout());
				JPanel[] panels = new JPanel[3];
				panels[0] = new JPanel();
				panels[1] = new JPanel();
				panels[2] = new JPanel();

				ConfigPanel config = new ConfigPanel();
				config.setPanels(panels, new String[] { "RED", "BLUE", "GREEN" });
				f.getContentPane().add(config, BorderLayout.CENTER);

				panels[0].setLayout(new MigLayout("fill,debug,wrap 2"));
				// panels[0].setLayout(new FlowLayout());
				panels[0].add(new JLabel("LABEL"), "");
				JPanel p = new JPanel(new BorderLayout());
				p.add(new JSlider());
				panels[0].add((p), "aligny baseline");

				JCheckBox box = new JCheckBox();
				config.trackModification(box, true);
				panels[1].add(box);

				config.setSaveTask(new SaveTask() {
					@Override
					public void save(final int index) {
						ThreadUtils.sleepQuietly(1000);
					}
				});

				f.setVisible(true);

			}
		});
	}

	private JButton getCloseButton() {
		if (this.closeButton == null) {
			this.closeButton = new CancelButton();
			this.closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					closeButtonActionPerformed();
				}
			});

		}
		return this.closeButton;
	}

	private void closeButtonActionPerformed() {
		if (this.modified) {

			if (!closeButtonActionPerformedIfInMachineFrame()) {
				int answer = JOptionPane.showConfirmDialog(this, GUIi18nManager.get(I18N_CLOSE_MESSAGE_PARAM_MODIFIED),
						"CONFIRM CLOSE CONFIG", JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION) {
					doYesSave();
				} else {
					doNoDontSave();
				}
				showPanel(0);
			}
		} else {
			if (this.hideAction != null) {
				ConfigPanel.this.hideAction.actionPerformed(null);
				showPanel(0);
			}
		}
	}

	private void doYesSave() {
		this.closePressed = true;
		validateButtonActionPerformed();
	}

	private void doNoDontSave() {
		rollback();
		if (this.hideAction != null) {
			ConfigPanel.this.hideAction.actionPerformed(null);
		}
	}

	private boolean closeButtonActionPerformedIfInMachineFrame() {
		Window root = SwingUtilities.getWindowAncestor(this);
		if (root instanceof AbstractMachineFrame) {
			AbstractMachineFrame frame = (AbstractMachineFrame) root;
			showPanel(0);
			frame.getController().askConfirmation(GUIi18nManager.get(I18N_CLOSE_MESSAGE_PARAM_MODIFIED),
					GUIi18nManager.get(I18N_YES_SAVE_BUTTON), GUIi18nManager.get(I18N_NO_DONT_SAVE_BUTTON),
					new ConfirmationCallback() {

						@Override
						public void confirmationTaken(final ConfirmationEvent evt) {
							if (evt.isConfirmed()) {
								doYesSave();
							} else {
								doNoDontSave();
							}
						}
					});

			return true;
		} else {
			return false;
		}
	}

	public void addCloseButtonActionListener(final ActionListener action) {
		getCloseButton().addActionListener(action);
	}

	private JButton getValidateButton() {
		if (this.validateButton == null) {
			this.validateButton = new ValidateButton();
			this.validateButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					validateButtonActionPerformed();

				};
			});
		}
		return this.validateButton;
	}

	private void validateButtonActionPerformed() {
		setBusy(true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				long t1 = System.currentTimeMillis();

				// do the actual saving
				ConfigPanel.this.saveTask.save(getSelectedIndex());

				saveComponentState();

				long t2 = System.currentTimeMillis();
				long delta = t2 - t1;
				// if the task is fast, sleep for 500ms so the user actually see
				// the busy panel
				if (delta < 500) {
					ThreadUtils.sleepQuietly(500);
				}

				setBusy(false);
				setModified(false);

				if (ConfigPanel.this.hideAfterValidate || ConfigPanel.this.closePressed) {
					// give time to the busy effect to fade out
					ThreadUtils.sleepQuietly(500);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							if (ConfigPanel.this.hideAction != null) {
								ConfigPanel.this.hideAction.actionPerformed(null);
							}
						}
					});
					ConfigPanel.this.closePressed = false;
				}
			}
		}).start();
	}

	private void saveComponentState() {
		// remember the config to be able to rollback
		Map<JComponent, Object> tmp = new HashMap<JComponent, Object>();
		for (JComponent comp : ConfigPanel.this.mapRollback.keySet()) {
			if (comp instanceof AbstractButton) {
				tmp.put(comp, ((AbstractButton) comp).isSelected());
			} else if (comp instanceof JSpinner) {
				tmp.put(comp, ((JSpinner) comp).getValue());
			} else if (comp instanceof JSlider) {
				tmp.put(comp, ((JSlider) comp).getValue());
			} else if (comp instanceof JComboBox) {
				tmp.put(comp, ((JComboBox) comp).getSelectedItem());
			}
		}
		ConfigPanel.this.mapRollback.clear();
		ConfigPanel.this.mapRollback = tmp;
	}

	public void addValidateButtonActionListener(final ActionListener action) {
		getValidateButton().addActionListener(action);
	}

	public void setModified(final boolean modified) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (modified) {
					validateButton.startBlink();
				} else {
					validateButton.stopBlink();
				}
			}
		});

		this.modified = modified;
	}

	public boolean isModified() {
		return this.modified;
	}

	public void trackModification(final JSpinner spinner, final boolean rollback) {
		if (spinner == null) {
			throw new IllegalArgumentException("The JSpinner can not be null");
		}
		spinner.addChangeListener(this.changeTracker);
		if (spinner.getEditor() instanceof DefaultEditor) {
			final JTextField text = ((DefaultEditor) spinner.getEditor()).getTextField();
			text.addCaretListener(new TextTracker(text));
		}
		if (rollback) {
			addRollbackTracker(spinner);
		}
	}

	public void unTrackModification(final JSpinner spinner) {
		if (spinner == null) {
			throw new IllegalArgumentException("The JSpinner can not be null");
		}
		spinner.removeChangeListener(this.changeTracker);
	}

	/***
	 * for JToggleButton/JCheckBox/JRadioButton
	 * 
	 * @param selectable
	 */
	public void trackModification(final AbstractButton selectable, final boolean rollback) {
		if (selectable == null) {
			throw new IllegalArgumentException("The AbstractButton can not be null");
		}
		selectable.addActionListener(this.actionTracker);
		if (rollback) {
			addRollbackTracker(selectable);
		}
	}

	public void unTrackModification(final AbstractButton selectable) {
		if (selectable == null) {
			throw new IllegalArgumentException("The AbstractButton can not be null");
		}
		selectable.removeActionListener(this.actionTracker);
	}

	public void trackModification(final JComboBox combo, final boolean rollback) {
		if (combo == null) {
			throw new IllegalArgumentException("The JComboBox can not be null");
		}
		combo.addItemListener(this.itemTracker);
		if (rollback) {
			addRollbackTracker(combo);
		}
	}

	public void unTrackModification(final JComboBox combo) {
		if (combo == null) {
			throw new IllegalArgumentException("The JComboBox can not be null");
		}
		combo.removeItemListener(this.itemTracker);
	}

	public void trackModification(final JSlider slider, final boolean rollback) {
		if (slider == null) {
			throw new IllegalArgumentException("The JSlider can not be null");
		}
		slider.addChangeListener(this.changeTracker);
		if (rollback) {
			addRollbackTracker(slider);
		}
	}

	public void unTrackModification(final JSlider slider) {
		if (slider == null) {
			throw new IllegalArgumentException("The JSlider can not be null");
		}
		slider.removeChangeListener(this.changeTracker);
	}

	private ChangeListener changeTracker = new ChangeListener() {
		@Override
		public void stateChanged(final ChangeEvent e) {
			setModified(true);
		}
	};
	private ActionListener actionTracker = new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			setModified(true);
		}
	};
	private ItemListener itemTracker = new ItemListener() {
		@Override
		public void itemStateChanged(final ItemEvent e) {
			setModified(true);
		}
	};

	private class TextTracker implements CaretListener {
		private String oldText;
		boolean init = true;
		JTextComponent comp;

		public TextTracker(final JTextComponent comp) {
			this.comp = comp;
			this.oldText = null;
		}

		@Override
		public void caretUpdate(final CaretEvent e) {
			String newText = this.comp.getText();
			if (!this.init) {
				if (this.oldText != null) {
					if (!this.oldText.equals(newText)) {
						setModified(true);
					}
				}
			} else {
				if (this.oldText != null && this.oldText.length() > 0) {// first
					// time
					// there
					// is a
					// text
					// with
					// size
					// >0
					this.init = false;
				}
			}
			this.oldText = newText;
		}
	}

	private void addRollbackTracker(final JComponent comp) {
		if (comp instanceof AbstractButton) {
			this.mapRollback.put(comp, ((AbstractButton) comp).isSelected());
		} else if (comp instanceof JSpinner) {
			this.mapRollback.put(comp, ((JSpinner) comp).getValue());
		} else if (comp instanceof JSlider) {
			this.mapRollback.put(comp, ((JSlider) comp).getValue());
		} else if (comp instanceof JComboBox) {
			this.mapRollback.put(comp, ((JComboBox) comp).getSelectedItem());
		}
	}

	private void rollback() {
		Object value;
		for (JComponent comp : this.mapRollback.keySet()) {
			value = this.mapRollback.get(comp);
			if (comp instanceof AbstractButton) {
				((AbstractButton) comp).setSelected((Boolean) value);
			} else if (comp instanceof JSpinner) {
				((JSpinner) comp).setValue(value);
			} else if (comp instanceof JSlider) {
				((JSlider) comp).setValue((Integer) value);
			} else if (comp instanceof JComboBox) {
				((JComboBox) comp).setSelectedItem(value);
			}
		}

		if (this.rollbackExtraAction != null) {
			this.rollbackExtraAction.actionPerformed(null);
		}

		setModified(false);
	}

	public void setSaveTask(final SaveTask saveTask) {
		if (saveTask == null) {
			throw new IllegalArgumentException("The save task can not be null");
		}
		this.saveTask = saveTask;
	}

	public void setHideAction(final AbstractAction hideAction) {
		if (hideAction == null) {
			throw new IllegalArgumentException("The AbstractAction can not be null");
		}
		this.hideAction = hideAction;
	}

	public void setHideAfterValidate(final boolean hideAfterValidate) {
		this.hideAfterValidate = hideAfterValidate;
	}

	public void setRollbackExtraAction(final AbstractAction rollbackExtraAction) {
		this.rollbackExtraAction = rollbackExtraAction;
	}

	public void setCancellable(final boolean flag) {
		this.closeButton.setVisible(flag);
	}

	public void setValidadeable(final boolean flag) {
		this.getValidateButton().setVisible(flag);
	}

	public void setNavigBarVisible(final boolean flag) {
		getPanelButton().setVisible(flag);
	}

	@Override
	public void showPanel(final int i) {
		super.showPanel(i);

		getCloseButton().setVisible(i != 0);
		getValidateButton().setVisible(i != 0);
	}

	public JButton getExitButton() {
		if (this.exitButton == null) {
			this.exitButton = new JButton(GUIi18nManager.get(I18N_EXIT_BUTTON));
			this.exitButton.setName(I18N_EXIT_BUTTON);
		}
		return this.exitButton;
	}

	public static abstract class ConfirmButton extends StartStopButton {

		private float alpha;

		public ConfirmButton(final eStartStop type) {
			super(type);
			this.alpha = 1f;
			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(final ComponentEvent e) {
					ConfirmButton.this.shape = null;
					repaint();
				}
			});
		}

		protected Shape shape;

		protected abstract Shape getShape();

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			PaintUtils.turnOnAntialias(g2);
			g2.setColor(Color.WHITE);
			if (this.timeline != null
					&& (this.timeline.getState() == TimelineState.PLAYING_FORWARD || this.timeline.getState() == TimelineState.PLAYING_REVERSE)) {
				g2.setComposite(AlphaComposite.SrcOver.derive(this.alpha));
			}
			g2.translate(getWidth() / 4, getHeight() / 4);
			g2.fill(getShape());
			g2.dispose();
		}

		private Timeline timeline;

		private void startBlink() {
			if (this.timeline != null) {
				if (this.timeline.getState() != TimelineState.IDLE) {
					return;
				}
			}
			this.timeline = new Timeline(this);
			this.timeline.setDuration(1000);
			this.timeline.addPropertyToInterpolate("alpha", 1f, 0.2f);
			this.timeline.setEase(new Spline(0.7f));
			this.timeline.addCallback(new TimelineCallbackAdapter() {
				@Override
				public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
						final float durationFraction, final float timelinePosition) {
					if (newState == TimelineState.IDLE || newState == TimelineState.CANCELLED) {
						setAlpha(1f);
					}
				}
			});
			this.timeline.playLoop(RepeatBehavior.REVERSE);
		}

		private void stopBlink() {
			if (this.timeline != null) {
				this.timeline.cancel();
			}
		}

		public float getAlpha() {
			return this.alpha;
		}

		public void setAlpha(final float alpha) {
			this.alpha = alpha;
			repaint();
		}
	}

	private class ValidateButton extends ConfirmButton {
		private static final long serialVersionUID = 1L;

		public ValidateButton() {
			super(eStartStop.START);
		}

		@Override
		protected Shape getShape() {
			if (this.shape == null) {
				float w = (getWidth() / 2) / 6;
				float h = getHeight() / 2 / 6;
				GeneralPath shape = new GeneralPath();
				shape.moveTo(0, 4 * h);
				shape.lineTo(0.5f * w, 3.5f * h);
				shape.lineTo(2 * w, 5 * h);
				shape.lineTo(5 * w, 1 * h);
				shape.lineTo(6 * w, 1.5f * h);
				shape.lineTo(2 * w, 6 * h);
				shape.closePath();

				this.shape = shape;
			}
			return this.shape;
		}
	}

	private class CancelButton extends ConfirmButton {
		public CancelButton() {
			super(eStartStop.STOP);
		}

		@Override
		protected Shape getShape() {
			if (this.shape == null) {
				float w = getWidth() / 2 / 6;
				float h = getHeight() / 2 / 6;

				GeneralPath shape = new GeneralPath();
				shape.moveTo(0, h);
				shape.lineTo(w, 0);
				shape.lineTo(3 * w, 2 * h);
				shape.lineTo(5 * w, 0);
				shape.lineTo(6 * w, h);
				shape.lineTo(4 * w, 3 * h);
				shape.lineTo(6 * w, 5 * h);
				shape.lineTo(5 * w, 6 * h);
				shape.lineTo(3 * w, 4 * h);
				shape.lineTo(w, 6 * h);
				shape.lineTo(0, 5 * h);
				shape.lineTo(2 * w, 3 * h);
				shape.closePath();
				this.shape = shape;
			}
			return this.shape;
		}
	}
}
