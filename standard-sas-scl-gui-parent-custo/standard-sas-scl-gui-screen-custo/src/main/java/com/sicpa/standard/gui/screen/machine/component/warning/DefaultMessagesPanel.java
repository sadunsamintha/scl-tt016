package com.sicpa.standard.gui.screen.machine.component.warning;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;

import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.components.scroll.SmoothScrolling;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelConfig;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.MessageEvent;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.MessageEventUpdate;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DefaultMessagesPanel extends AbstractMessagesPanel {
	protected static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new BorderLayout());
				final DefaultMessagesPanel mp = new DefaultMessagesPanel();
				UIManager.put(DefaultWarningCellRenderer.LINE_WRAP, Boolean.TRUE);
				f.getContentPane().add(mp);
				JButton b = new JButton("add");
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								mp.getModel().addOrUpdateMessage(
										new Warning("code",
												"tesddd dddddddtesddddddddddt tesddddddddddt ttesddddddddddt  ", true));
							}
						}).start();
					}
				});

				f.getContentPane().add(b, BorderLayout.SOUTH);
				f.setSize(300, 500);
				f.setVisible(true);
				mp.getModel()
						.addOrUpdateMessage(
								new Warning(
										"code",
										"tesdddpppppppppppp qwertzuiopasdfghjklyxcvbnmqwertzuiopasdfghjklyxcvbnm tesddddddddddt ttesddddddddddt  ",
										true));
				mp.getModel().addOrUpdateMessage(new Warning("code", "teddddddddddddddt  ", true));
			}
		});
	}

	protected JScrollPane scrollWarning;
	protected DefaultListModel messagesListModel;
	protected JList messageLog;
	protected int addAnimDuration;
	protected int removeAnimDuration;

	protected Timeline removeTimeline;

	public DefaultMessagesPanel() {
		this(new MessagesModel());
	}

	public DefaultMessagesPanel(final MessagesModel model) {
		super(model);
		addAnimDuration = SicpaLookAndFeelConfig.getPCCFrameWarningAddAnimationDuration();
		removeAnimDuration = SicpaLookAndFeelConfig.getPCCFrameWarningRemoveAnimationDuration();
		initGUI();
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill,hidemode 3,inset 0 0 0 0"));
		add(SmallScrollBar.createLayerSmallScrollBar(getScrollWarning(), false, true, true, true), "pushy , grow,span");
		setName("warningPanel");
	}

	protected JScrollPane getScrollWarning() {
		if (scrollWarning == null) {
			scrollWarning = new JScrollPane(getMessageLog());
			SmoothScrolling.enableFullScrolling(this.scrollWarning);
			scrollWarning.setFont(SicpaFont.getFont(40));
			scrollWarning.setName("scrollWarning");
		}
		return scrollWarning;
	}

	protected JList getMessageLog() {
		if (messageLog == null) {
			if (messagesListModel == null) {
				messagesListModel = new DefaultListModel();
			}
			messageLog = new JList();
			messageLog.setModel(this.messagesListModel);
			messageLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			messageLog.setCellRenderer(new MessageListCellRenderer());
			messageLog.setAutoscrolls(false);
			messageLog.setName("warningList");
			messageLog.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(final MouseEvent evt) {
					messageLogMouseReleased(evt);
				}
			});
		}
		return this.messageLog;
	}

	protected void messageLogMouseReleased(final MouseEvent evt) {
		if (evt.getClickCount() != 2) {
			return;
		}

		Message msg = (Message) messageLog.getSelectedValue();
		if (msg == null || !msg.isRemoveable()) {
			return;
		}

		getModel().removeMessage(msg);
	}

	protected void startRemoveAnim(final int index, final Message message) {

		Float progress = ((MessageListCellRenderer) messageLog.getCellRenderer()).getAnimProgress(index);
		if (progress != null && progress != 0 && progress != 1) {// anim already
			// in
			// progress
			return;
		}

		if (removeTimeline != null && removeTimeline.getState() == TimelineState.PLAYING_FORWARD) {
			removeTimeline.cancel();
		}

		removeTimeline = new Timeline();
		removeTimeline.setDuration(this.removeAnimDuration);
		removeTimeline.addCallback(new TimelineCallback() {
			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE || newState == TimelineState.CANCELLED) {
					ThreadUtils.invokeLater(new Runnable() {
						@Override
						public void run() {
							int toRemove = findMessageIndex(message);
							((DefaultListModel) messageLog.getModel()).remove(toRemove);
							((MessageListCellRenderer) messageLog.getCellRenderer()).setAnimProgress(index, 1);
							messageLog.repaint();
						}
					});
				}
			}

			@Override
			public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
				ThreadUtils.invokeLater(new Runnable() {
					@Override
					public void run() {
						((MessageListCellRenderer) messageLog.getCellRenderer()).setAnimProgress(index,
								1f - timelinePosition);
						messageLog.repaint();
					}
				});
			}
		});
		removeTimeline.play();
	}

	protected void startAddMessage() {
		((MessageListCellRenderer) messageLog.getCellRenderer()).setAnimProgress(messagesListModel.getSize() - 1,
				0.001f);

		final int i = messagesListModel.getSize() - 1;
		Timeline timeline = new Timeline();
		timeline.setDuration(addAnimDuration);
		timeline.addCallback(new TimelineCallbackAdapter() {
			@Override
			public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
				ThreadUtils.invokeLater(new Runnable() {
					@Override
					public void run() {
						((MessageListCellRenderer) messageLog.getCellRenderer()).setAnimProgress(i, timelinePosition);
						messageLog.repaint();
					}
				});
			}

			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
					ThreadUtils.invokeLater(new Runnable() {
						@Override
						public void run() {
							((MessageListCellRenderer) messageLog.getCellRenderer()).setAnimProgress(i,
									timelinePosition);
							messageLog.repaint();
						}
					});
				}
			}
		});
		timeline.play();

		// if not in a new invoke later the scroll bar won t scroll to the max
		// value
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				DefaultMessagesPanel.this.scrollWarning.getVerticalScrollBar().setValue(
						scrollWarning.getVerticalScrollBar().getMaximum());
			}
		});
	}

	final public void addWarning(final String text, final String code, final boolean removeable) {
		getModel().addMessage(new Warning(code, text, removeable));
	}

	final public void addInfo(final String text, final String code) {
		getModel().addMessage(new Info(code, text));
	}

	final public void addError(final String text, final String code) {
		getModel().addMessage(new Error(code, text));
	}

	protected int findMessageIndex(final Message w) {
		for (int i = 0; i < messagesListModel.getSize(); i++) {
			if (messagesListModel.get(i).equals(w)) {
				return i;
			}
		}
		return -1;
	}

	public void clearMessages() {
		getModel().reset();
	}

	/**
	 * 
	 * @return all the warnings currently in the list
	 */
	public List<Warning> getAllWarnings() {
		ArrayList<Warning> list = new ArrayList<Warning>();
		for (int i = 0; i < messagesListModel.getSize(); i++) {
			if (messagesListModel.get(i) instanceof Warning) {
				list.add((Warning) messagesListModel.get(i));
			}
		}
		return list;
	}

	@Override
	protected void modelMessageAdded(final MessageEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				messagesListModel.addElement(evt.getMessage());
				startAddMessage();
			}
		});
	}

	@Override
	protected void modelMessageRemove(final MessageEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				startRemoveAnim(findMessageIndex(evt.getMessage()), evt.getMessage());
			}
		});
	}

	@Override
	public void setModel(final MessagesModel model) {
		super.setModel(model);
		if (messagesListModel == null) {
			messagesListModel = new DefaultListModel();
		}
		messagesListModel.clear();
		for (Message m : model.getMessages()) {
			messagesListModel.addElement(m);
		}
	}

	@Override
	protected void modelReseted() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				messagesListModel.clear();
			}
		});
	}

	@Override
	protected void modelMessageUpdated(final MessageEventUpdate evt) {

		Object item = null;
		for (int i = 0; i < messagesListModel.size(); i++) {
			if (messagesListModel.get(i).equals(evt.getMessage())) {
				item = messagesListModel.get(i);
				break;
			}
		}
		if (item != null) {
			messagesListModel.removeElement(item);
			messagesListModel.addElement(item);
		}
		repaint();
	}

	public void setAddAnimDuration(int addAnimDuration) {
		this.addAnimDuration = addAnimDuration;
	}
}
