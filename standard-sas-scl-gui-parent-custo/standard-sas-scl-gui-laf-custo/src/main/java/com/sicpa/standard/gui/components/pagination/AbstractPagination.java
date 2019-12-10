package com.sicpa.standard.gui.components.pagination;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import org.divxdede.swing.busy.JBusyComponent;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceImageCreator;

import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.gui.utils.ThreadUtils;

public abstract class AbstractPagination<ITEM> extends JPanel {

	protected Executor executor = Executors.newCachedThreadPool();

	private static final long serialVersionUID = 1L;

	public AbstractPagination() {
		initIcons();
	}

	protected AbstractButton buttonPrevious;
	protected AbstractButton buttonNext;
	protected AbstractButton buttonFirst;
	protected AbstractButton buttonLast;

	protected JBusyComponent<JComponent> busyComponent;

	protected JScrollPane scroll;

	protected int currentPage;
	protected int totalPages;

	protected JPanel mainPanel;

	protected JLabel labelPageNumber;

	protected abstract List<ITEM> getItems(int pageNumber);

	protected abstract void displayItem(ITEM item);

	protected void displayPage(List<ITEM> items, int pageNumber) {
		this.currentPage = pageNumber;
		updateButtonState();
		getLabelPageNumber().setText(pageNumber + "/" + totalPages);
		for (ITEM t : items) {
			displayItem(t);
		}
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill,"));
		add(getBusyComponent(), "grow,push");
	}

	public JBusyComponent<JComponent> getBusyComponent() {
		if (busyComponent == null) {
			busyComponent = new JBusyComponent<JComponent>(getMainPanel());
		}
		return busyComponent;
	}

	public JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new MigLayout("fill"));
			mainPanel.add(getScroll(), "push,grow,span");
			mainPanel.add(getButtonFirst(), "spanx,split 5, right , sg 1");
			mainPanel.add(getButtonPrevious(), "sg 1");
			mainPanel.add(getLabelPageNumber());
			mainPanel.add(getButtonNext(), "sg 1");
			mainPanel.add(getButtonLast(), "sg 1");

		}
		return mainPanel;
	}

	public JScrollPane getScroll() {
		if (scroll == null) {
			scroll = new JScrollPane();
		}
		return scroll;
	}

	protected Icon iconFirstEnable;
	protected Icon iconFirstDisable;

	protected Icon iconLastEnable;
	protected Icon iconLastDisable;

	protected Icon iconNextEnable;
	protected Icon iconNextDisable;

	protected Icon iconPreviousEnable;
	protected Icon iconPreviousDisable;

	protected void initIcons() {

		Color[] cActive = new Color[7];
		Arrays.fill(cActive, SicpaColor.BLUE_MEDIUM);
		SubstanceColorScheme colorSchemeEnable = SubstanceColorSchemeUtilities.getLightColorScheme("allblue", cActive);

		Color[] cDisable = new Color[7];
		Arrays.fill(cDisable, SicpaColor.GRAY);
		SubstanceColorScheme colorSchemeDisable = SubstanceColorSchemeUtilities
				.getLightColorScheme("allgrey", cDisable);

		iconLastEnable = SubstanceImageCreator.getDoubleArrowIconDelta(50, 5, 5, 1, SwingConstants.EAST,
				colorSchemeEnable);
		iconLastDisable = SubstanceImageCreator.getDoubleArrowIconDelta(50, 5, 5, 1, SwingConstants.EAST,
				colorSchemeDisable);

		iconFirstEnable = SubstanceImageCreator.getDoubleArrowIconDelta(50, 5, 5, 1, SwingConstants.WEST,
				colorSchemeEnable);
		iconFirstDisable = SubstanceImageCreator.getDoubleArrowIconDelta(50, 5, 5, 1, SwingConstants.WEST,
				colorSchemeDisable);

		iconPreviousEnable = SubstanceImageCreator.getArrowIcon(50, SwingConstants.WEST, colorSchemeEnable);
		iconPreviousDisable = SubstanceImageCreator.getArrowIcon(50, SwingConstants.WEST, colorSchemeDisable);

		iconNextEnable = SubstanceImageCreator.getArrowIcon(50, SwingConstants.EAST, colorSchemeEnable);
		iconNextDisable = SubstanceImageCreator.getArrowIcon(50, SwingConstants.EAST, colorSchemeDisable);

	}

	public AbstractButton getButtonNext() {
		if (buttonNext == null) {
			buttonNext = createButton();
			buttonNext.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonNextActionPerformed();
				}
			});
		}
		return buttonNext;
	}

	protected AbstractButton createButton() {
		JButton b = new JButton();
		b.setOpaque(false);
		b.setBorderPainted(false);
		b.setContentAreaFilled(false);
		b.setFocusPainted(false);
		return b;
	}

	protected void buttonNextActionPerformed() {
		request(currentPage + 1);
	}

	public AbstractButton getButtonPrevious() {
		if (buttonPrevious == null) {
			buttonPrevious = createButton();
			buttonPrevious.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonPreviousActionPerformed();
				}
			});
		}
		return buttonPrevious;
	}

	protected void buttonPreviousActionPerformed() {
		request(currentPage - 1);
	}

	protected void requestFirst() {
		getBusyComponent().setBusy(true);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				final Pair<List<ITEM>, Integer> firstResult = doRequestFirst();
				ThreadUtils.invokeLater(new Runnable() {
					@Override
					public void run() {
						setTotalPages(firstResult.getValue2());
						displayPage(firstResult.getValue1(), 1);
						getBusyComponent().setBusy(false);
					}

				});
			}
		});
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	/**
	 * @return List<T> the items for the 1st pages + total pages number
	 */
	protected abstract Pair<List<ITEM>, Integer> doRequestFirst();

	protected void request(final int pageNumber) {
		getBusyComponent().setBusy(true);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				final List<ITEM> items = getItems(pageNumber);
				ThreadUtils.invokeLater(new Runnable() {
					@Override
					public void run() {
						displayPage(items, pageNumber);
						getBusyComponent().setBusy(false);
					}
				});
			}
		});
	}

	public JLabel getLabelPageNumber() {
		if (labelPageNumber == null) {
			labelPageNumber = new JLabel("?/?");
		}
		return labelPageNumber;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	protected void updateButtonState() {
		boolean canGetNext = currentPage < totalPages;
		boolean canGetPrevious = currentPage > 1;

		if (canGetNext) {
			getButtonNext().setIcon(iconNextEnable);
			getButtonLast().setIcon(iconLastEnable);
		} else {
			getButtonNext().setIcon(iconNextDisable);
			getButtonLast().setIcon(iconLastDisable);
		}
		getButtonNext().setEnabled(canGetNext);
		getButtonLast().setEnabled(canGetNext);

		if (canGetPrevious) {
			getButtonPrevious().setIcon(iconPreviousEnable);
			getButtonFirst().setIcon(iconFirstEnable);
		} else {
			getButtonPrevious().setIcon(iconPreviousDisable);
			getButtonFirst().setIcon(iconFirstDisable);
		}

		getButtonPrevious().setEnabled(canGetPrevious);
		getButtonFirst().setEnabled(canGetPrevious);

	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public AbstractButton getButtonFirst() {
		if (buttonFirst == null) {
			buttonFirst = createButton();
			buttonFirst.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonFirstActionPerformed();
				}
			});
		}
		return buttonFirst;
	}

	protected void buttonFirstActionPerformed() {
		request(1);
	}

	public AbstractButton getButtonLast() {
		if (buttonLast == null) {
			buttonLast = createButton();
			buttonLast.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonLastActionPerformed();
				}
			});
		}
		return buttonLast;
	}

	protected void buttonLastActionPerformed() {
		request(totalPages);
	}
}
