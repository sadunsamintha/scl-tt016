package com.sicpa.standard.gui.demo.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.AbstractPainter;

import com.jhlabs.image.GaussianFilter;
import com.sicpa.standard.gui.components.dialog.dropShadow.DialogWithDropShadow;
import com.sicpa.standard.gui.components.layeredComponents.oldValue.OldValueFormater;
import com.sicpa.standard.gui.components.layeredComponents.oldValue.OldValueWrapper;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardType;
import com.sicpa.standard.gui.debug.EDT.EventDispatchThreadViolationRepaintManager;
import com.sicpa.standard.gui.listener.Draggable;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;
import com.sicpa.standard.gui.screen.machine.MachineViewController;
import com.sicpa.standard.gui.screen.machine.component.applicationStatus.ApplicationStatus;
import com.sicpa.standard.gui.screen.machine.component.config.SaveTask;
import com.sicpa.standard.gui.screen.machine.component.confirmation.ConfirmationCallback;
import com.sicpa.standard.gui.screen.machine.component.confirmation.ConfirmationEvent;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class PreviewMachine extends AbstractMachineFrame {
	private static final long serialVersionUID = 1L;

	public PreviewMachine() {
		super(new MachineViewController());

		JTextField f = new JTextField(15);
		VirtualKeyboardPanel.attachKeyboardDialog(f, VirtualKeyboardPanel.getDefaultKeyboard(f));

		getFooter().add(f);

		// SicpaLookAndFeel.turnOnMemoryManagementWidget(this);
		initGUI();
		// getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
		setSize(1024, 768);

		setTitle("Sample app");

		// getConfigPanel().addPanelIndexListener(new PropertyChangeListener() {
		// @Override
		// public void propertyChange(final PropertyChangeEvent evt) {
		// System.out.println("old panel:" + evt.getOldValue() + " new panel:" + evt.getNewValue());
		// }
		// });
		//
		// createAndShowDialog();
		//
		// Timeline timeline = new Timeline(this);
		// timeline.addPropertyToInterpolate("offset", 0, 10);
		// timeline.setDuration(1000);
		// timeline.playLoop(RepeatBehavior.LOOP);

		// getHeader().add(new JLabel(new ImageIcon(ImageUtils.createRandomColorCirlceImage())), "east");

		// ((DefaultConfigPasswordPanel) getConfigPasswordPanel()).setKeyboard(VirtualKeyboardPanel
		// .getQWERTZKeyboard(null));

		// getConfigPanel().getPanelButton().add(new JButton("test"), "pos preview.x-70 preview.y");
		//
		// setProductionDataSelectionModel(new TestSelectionFlowCustomModel());

		// getStartButton().setEnabled(false);
		// getStopButton().setEnabled(false);
		// getSelectProductButton().setVisible(false);
		// getSelectProductAction().actionPerformed(null);
	}

	@Override
	protected void buildFooter() {
		addFillerToFooter();
		addToFooter(getSelectProductButton());
	}

	private void createAndShowDialog() {
		DialogWithDropShadow dialog = new DialogWithDropShadow(this, true, false);
		dialog.setTitle("actions available");
		dialog.getContentPane().setLayout(new MigLayout(""));

		// ---------------------------------------------------
		JButton b = new JButton("lock footer");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().lockFooter(true);
			}
		});
		dialog.getContentPane().add(b, "");
		b = new JButton("unlock footer");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().lockFooter(false);
			}
		});
		dialog.getContentPane().add(b, "wrap");
		// ---------------------------------------------------
		b = new JButton("add warning");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController()
						.addWarning(
								System.currentTimeMillis() + "",
								"message123132 132 132 132 132 132 132 1 32 132 132 132 132  132 132 132 1231 32132132132 132132",
								true);
			}
		});
		dialog.getContentPane().add(b, "");
		b = new JButton("remove all warning");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().removeAllWarnings();
			}
		});
		dialog.getContentPane().add(b, "wrap");
		// ---------------------------------------------------
		b = new JButton("add minor error");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().addMinorError(System.currentTimeMillis() + "", "title", "text");
			}
		});
		dialog.getContentPane().add(b, "");
		b = new JButton("remove all minor error");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().removeAllMinorError();
			}
		});
		dialog.getContentPane().add(b, "wrap");
		// ---------------------------------------------------
		b = new JButton("add fatal error");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().addFatalError(System.currentTimeMillis() + "", "title", "text");
			}
		});
		dialog.getContentPane().add(b, "");
		b = new JButton("remove all fatal error");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().removeAllFatalError();
			}
		});
		dialog.getContentPane().add(b, "wrap");
		// ---------------------------------------------------
		b = new JButton("show emergency");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().showEmergency("emergency error");
			}
		});
		dialog.getContentPane().add(b, "");
		b = new JButton("hide emergency");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().hideEmergency();
			}
		});
		dialog.getContentPane().add(b, "wrap");
		// ---------------------------------------------------
		b = new JButton("show center\npanel error");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController()
						.addErrorMainPanel("  ",
								"111Connection lost 222Connection lost 333Connection lost 444Connection lost 555Connection lost 666Connection lost");
			}
		});
		dialog.getContentPane().add(b, "");
		b = new JButton("hide center\npanel error");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().removeAllErrorMainPanel();
			}
		});
		dialog.getContentPane().add(b, "wrap");

		// ---------------------------------------------------
		b = new JButton("application status\nRUNNING");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().setApplicationStatus(ApplicationStatus.RUNNING);
			}
		});
		dialog.getContentPane().add(b, "");
		b = new JButton("application status\nSTOP");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().setApplicationStatus(ApplicationStatus.STOP);
			}
		});
		dialog.getContentPane().add(b, "wrap");
		// ---------------------------------------------------
		b = new JButton("replace main panel");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				JPanel p = new JPanel(new BorderLayout());
				p.add(new JScrollPane(new JTree()));
				replaceMainPanel(p);
			}
		});
		dialog.getContentPane().add(b, "");
		b = new JButton("reset main panel");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				resetMainPanel();
			}
		});
		dialog.getContentPane().add(b, "wrap");

		// ---------------------------------------------------
		b = new JButton("show exit button");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getExitButton().setVisible(true);
			}
		});
		dialog.getContentPane().add(b, "");
		b = new JButton("hide exit button");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getExitButton().setVisible(false);
				System.out.println(getExitButton().isVisible());
			}
		});
		dialog.getContentPane().add(b, "wrap");

		// ---------------------------------------------------
		b = new JButton("ask question");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().askConfirmation("Do you actually want to cancel the current coding job",
						"YES\ncancel the job", "NO\ncontinue coding", new ConfirmationCallback() {

							@Override
							public void confirmationTaken(final ConfirmationEvent evt) {

							}
						});
			}
		});
		dialog.getContentPane().add(b, "wrap");
		// ---------------------------------------------------
		b = new JButton("background task");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().executeTaskInBackground(new Runnable() {
					@Override
					public void run() {
						ThreadUtils.sleepQuietly(5000);
					}
				});
			}
		});
		dialog.getContentPane().add(b, "wrap");
		// --------------------------------------------
		b = new JButton("test");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {

				new Thread(new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < 10; i++) {
							getController().addWarning("key", "message" + i, false);
						}

						ThreadUtils.sleepQuietly(2000);

						for (int i = 0; i < 10; i++) {
							getController().removeWarning("key", "message" + i);
						}
					}
				}).start();

			}
		});
		dialog.getContentPane().add(b, "wrap");
		// ------------------------------------------------------
		dialog.setSize(50, 50);
		dialog.pack();
		dialog.setVisible(true);
	}

	JPanel leftPanel;
	int offset;

	public void setOffset(final int offset) {
		if (this.offset == offset) {
			return;
		}
		this.offset = offset;
		repaint();
	}

	protected JComponent getLeftPanel() {
		if (this.leftPanel == null) {
			this.leftPanel = new JPanel() {
				@Override
				protected void paintComponent(final Graphics g) {
					super.paintComponent(g);

					Point start = new Point(0, 0);
					Point end = new Point(10, 10);
					float[] fractions = new float[] { 0, 0.5f, 1 };
					Color[] colors = new Color[] { SicpaColor.BLUE_ULTRA_LIGHT, SicpaColor.GREEN_DARK,
							SicpaColor.BLUE_ULTRA_LIGHT };

					java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(start, end, fractions, colors,
							CycleMethod.REFLECT);

					Graphics2D g2 = (Graphics2D) g.create();
					g2.translate(PreviewMachine.this.offset, PreviewMachine.this.offset);
					g2.setPaint(lgp);
					g2.fillRect(-PreviewMachine.this.offset, -PreviewMachine.this.offset, getWidth(), getHeight());
					g2.dispose();

				}
			};

			this.leftPanel.setLayout(new MigLayout("fill"));
			JXLabel label = new JXLabel("LEFT PANEL");
			AbstractPainter painter = (AbstractPainter) label.getForegroundPainter();
			painter.setFilters(new GaussianFilter(3));
			label.setFont(SicpaFont.getFont(65));

			this.leftPanel.add(label, "center");
		}
		return this.leftPanel;
	}

	@Override
	protected void buildRightPanel() {
		JPanel p = new JPanel(new MigLayout("fill")) {
			@Override
			protected void paintComponent(final Graphics g) {
				super.paintComponent(g);

				Point start = new Point(0, 10);
				Point end = new Point(10, 0);
				float[] fractions = new float[] { 0, 0.5f, 1 };
				Color[] colors = new Color[] { SicpaColor.BLUE_ULTRA_LIGHT, SicpaColor.ORANGE,
						SicpaColor.BLUE_ULTRA_LIGHT };

				java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(start, end, fractions, colors,
						CycleMethod.REFLECT);

				Graphics2D g2 = (Graphics2D) g.create();
				g2.translate(PreviewMachine.this.offset, -PreviewMachine.this.offset);
				g2.setPaint(lgp);
				g2.fillRect(-PreviewMachine.this.offset, +PreviewMachine.this.offset, getWidth(), getHeight());
				g2.dispose();

			}
		};

		setRightPanel(p);
		setRightPanelPreferredWidth(250);

		JXLabel label = new JXLabel("RIGHT\nPANEL");
		label.setLineWrap(true);
		AbstractPainter painter = (AbstractPainter) label.getForegroundPainter();
		painter.setFilters(new GaussianFilter(3));
		label.setFont(SicpaFont.getFont(65));

		p.add(label, "center");

	}

	public static void main(final String[] args) {

		Locale.setDefault(new Locale("en"));

		EventDispatchThreadViolationRepaintManager.install();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();

				SicpaLookAndFeelCusto.setDefaultVirtualKeyboard(new VirtualKeyboardType() {

					@Override
					public VirtualKeyboardPanel getKeyboard(final JTextComponent textComp) {
						VirtualKeyboardPanel kb = new VirtualKeyboardPanel(textComp);
						kb.addLayoutConstraint(" ", "span 7 ,grow");
						kb.addLayoutConstraint("{del}", "spany 2 ,grow");
						kb.addButtonsRow("1234567890");
						kb.addButtonsRow("QWERTYUIOP");
						kb.addButtonsRow("ASDFGHJKL{del}");
						kb.addButtonsRow("{capsLock}ZXCVBNM");
						kb.addButtonsRow("{ctrl}{alt} {clr}");
						return kb;

					}

				});

				final PreviewMachine t = new PreviewMachine();

				Draggable.makeDraggable(t);
				t.setVisible(true);
			}
		});
	}

	// @Override
	// protected Image getSicpaLogo() {
	// return ImageUtils.createRandomStrippedImage(150, 100);
	// }

	private JPanel[] panels;

	@Override
	protected JPanel[] getConfigPanels() {
		if (this.panels == null) {
			this.panels = new JPanel[10];
			this.panels[0] = new JPanel(new MigLayout("wrap 2"));
			this.panels[1] = new JPanel();
			this.panels[2] = new JPanel();
			this.panels[3] = new JPanel();
			this.panels[4] = new JPanel();
			this.panels[5] = new JPanel();
			this.panels[6] = new JPanel();
			this.panels[7] = new JPanel();
			this.panels[8] = new JPanel();
			this.panels[9] = new JPanel();

			JLabel l = new JLabel("LABEL SPINNER");
			l.setOpaque(true);
			l.setBackground(Color.RED);
			this.panels[0].add(l, "");

			OldValueWrapper wrapper;

			JSpinner spinner = new JSpinner();
			spinner.setPreferredSize(new Dimension(350, 150));
			trackConfigModification(spinner, true);
			// OldValueWrapper wrapper=getOldValueWrapper(spinner);
			this.panels[0].add(spinner, "");
			// trackOldValue(wrapper, 0, 0);

			JSlider slider = new JSlider();
			trackConfigModification(slider, true);
			wrapper = getOldValueWrapper(slider);
			wrapper.setFormater(new OldValueFormater() {
				@Override
				public String getFormatedValue(final Object oldValue) {
					return "Old value" + oldValue.toString();
				}
			});
			this.panels[0].add(wrapper, "growx");
			trackOldValue(wrapper, 0, 0);

			JComboBox combo = new JComboBox();
			combo.addItem("aaaa");
			combo.addItem("bbbb");
			combo.addItem("cccc");
			trackConfigModification(combo, true);
			// wrapper=getOldValueWrapper(combo);
			this.panels[0].add(combo);
			// trackOldValue(wrapper, 0, 0);

			JRadioButton radio = new JRadioButton("radio");
			trackConfigModification(radio, true);
			// wrapper=getOldValueWrapper(radio);
			this.panels[0].add(radio);
			// trackOldValue(wrapper, 0, 0);

			JCheckBox check = new JCheckBox("check");
			trackConfigModification(check, true);
			// wrapper=getOldValueWrapper(check);
			this.panels[0].add(check);
			// trackOldValue(wrapper,radio ,50,
			// 50,SicpaFont.getFont(28),SicpaColor
			// .RED,SicpaColor.BLUE_ULTRA_LIGHT);

			JToggleButton toggle = new JToggleButton("toggle");
			trackConfigModification(toggle, true);
			// wrapper=getOldValueWrapper(toggle);
			this.panels[0].add(toggle);
			// trackOldValue(wrapper, 0, 0);

			// this.panels[0].setBackground(Color.red);
			// this.panels[1].setBackground(Color.blue);
			// this.panels[2].setBackground(Color.green);
		}
		return this.panels;
	}

	@Override
	protected String[] getConfigPanelsTitle() {
		return new String[] { "1111111", "222222222", "33333333333333333333333333333", "4444444", "5555555555",
				"66666666", "777777777", "88888888888", "9999999999", "0000000000000" };
	}

	@Override
	protected SaveTask getSaveConfigTask() {
		return new SaveTask() {
			@Override
			public void save(final int i) {
				ThreadUtils.sleepQuietly(5000);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
					}
				});

			}
		};
	}

	int j = 0;

	@Override
	protected AbstractAction getStartAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {
			}
		};
	}

	@Override
	protected AbstractAction getStopAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {

			}
		};
	}

	@Override
	protected boolean isConfigPanelNavigBarVisible() {
		return true;
	}

	@Override
	protected AbstractAction getLogoutExtraAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {

			}
		};
	}

	@Override
	protected boolean isConfigCancellable(final int index) {
		return true;
	}

	@Override
	protected boolean isConfigValidateable(final int index) {
		return true;
	}

	@Override
	protected void buildLayeredLeftPanel() {
		int index = 0;
		addLayerToLeftPanel(getLeftPanel(), index++);
		getLeftPanel().setVisible(true);
		addLayerToLeftPanel(getScrollingErrorMinor(), index++, true);
		addLayerToLeftPanel(getScrollingErrorFatal(), index++, true);
		addLayerToLeftPanel(getConfirmationPanel(), index++);
		addLayerToLeftPanel(getEmergencyPanel(), index++);
	}
}
