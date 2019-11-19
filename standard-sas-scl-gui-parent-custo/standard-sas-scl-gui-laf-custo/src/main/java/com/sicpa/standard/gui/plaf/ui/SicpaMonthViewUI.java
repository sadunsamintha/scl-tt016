package com.sicpa.standard.gui.plaf.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Constructor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.border.IconBorder;
import org.jdesktop.swingx.plaf.basic.CalendarState;
import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceImageCreator;
import org.pushingpixels.substance.internal.utils.SubstanceTextUtilities;
import org.pushingpixels.substance.swingx.SubstanceMonthViewUI;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.PaintUtils;

public class SicpaMonthViewUI extends SubstanceMonthViewUI {

	public static void main(final String[] args) throws UnsupportedLookAndFeelException {

		Locale.setDefault(Locale.ENGLISH);

		SicpaLookAndFeelCusto.install();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				// SicpaLookAndFeel.setFontSize(10);

				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
				f.setSize(300, 100);
				
				final JXDatePicker dp = new JXDatePicker();
//				dp.getMonthView().setFont(SicpaFont.getFont(8));
				//strange thing that is needed for the "today panel"
//				dp.addPopupMenuListener(new PopupMenuListener() {
////					@Override
////					public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
////						for (Component comp : dp.getLinkPanel().getComponents()) {
////							comp.setFont(SicpaFont.getFont(8));
////						}
////					}
//
//					@Override
//					public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
//					}
//
//					@Override
//					public void popupMenuCanceled(PopupMenuEvent arg0) {
//					}
//				});

				f.getContentPane().setLayout(new MigLayout());
				f.getContentPane().add(dp);
			}
		});
	}

	/** Return value used to identify when the year down button is pressed. */
	public static final int YEAR_DOWN = 3;
	/** Return value used to identify when the year up button is pressed. */
	public static final int YEAR_UP = 4;

	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		SicpaLookAndFeelCusto.flagAsWorkArea(comp);
		return new SicpaMonthViewUI();
	}

	@Override
	protected void paintMonthHeader(final Graphics g, final Calendar calendar) {
		Rectangle page = getMonthHeaderBounds(calendar.getTime(), false);
		Graphics2D g2d = (Graphics2D) g.create();

		// use this.monthView.getWidth() instead of page.width because
		// if the width of the text today link is bigger than the header
		// there will be a not painted area
		PaintUtils.paintHighlight(g2d, page.x, page.y, this.monthView.getWidth(), page.height);

		g2d.dispose();

		paintDayOfMonth(g, page, calendar, CalendarState.TITLE);

		// try {
		// // show click area
		// int x = 30, y = 0;
		// Rectangle headerBounds = getMonthHeaderBoundsAtLocation(x, y);
		//
		// Rectangle yearDown = new Rectangle(+x, 0,
		// this.yearDownImage.getIconWidth(), headerBounds.height);
		// Rectangle yearup = new Rectangle(headerBounds.width -
		// this.yearUpImage.getIconWidth() + x, 0,
		// this.yearUpImage.getIconWidth(), headerBounds.height);
		//
		// g.setColor(Color.RED);
		// ((Graphics2D) g).fill(yearDown);
		// ((Graphics2D) g).fill(yearup);
		//
		// g.setColor(Color.GREEN);
		//
		// Rectangle monthDown = new Rectangle(this.yearDownImage.getIconWidth()
		// + 10 + x, 0,
		// this.monthDownImage.getIconWidth(), headerBounds.height);
		//
		// Rectangle monthup = new Rectangle(headerBounds.width -
		// this.yearUpImage.getIconWidth()
		// - this.monthUpImage.getIconWidth() - 10 + x, 0,
		// this.monthUpImage.getIconWidth(),
		// headerBounds.height);
		//
		// ((Graphics2D) g).fill(monthDown);
		// ((Graphics2D) g).fill(monthup);
		// } catch (Exception e) {
		// // TODO: handle exception
		// }

	}

	@Override
	protected int getTraversableGridPositionAtLocation(final int x, final int y) {

		Rectangle headerBounds = getMonthHeaderBoundsAtLocation(x, y);
		if (headerBounds == null) {
			return -1;
		}
		int xoffset = headerBounds.x;

		Rectangle yearDown = new Rectangle(xoffset, 0, this.yearDownImage.getIconWidth(), headerBounds.height);
		Rectangle yearup = new Rectangle(headerBounds.width - this.yearUpImage.getIconWidth() + xoffset, 0,
				this.yearUpImage.getIconWidth(), headerBounds.height);

		Rectangle monthDown = new Rectangle(this.yearDownImage.getIconWidth() + 10 + xoffset, 0,
				this.monthDownImage.getIconWidth(), headerBounds.height);

		Rectangle monthup = new Rectangle(xoffset + headerBounds.width - this.yearUpImage.getIconWidth()
				- this.monthUpImage.getIconWidth() - 10, 0, this.monthUpImage.getIconWidth(), headerBounds.height);

		if (yearDown.contains(x, y)) {
			return this.isLeftToRight ? YEAR_DOWN : YEAR_UP;
		} else if (yearup.contains(x, y)) {
			return this.isLeftToRight ? YEAR_UP : YEAR_DOWN;
		} else if (monthDown.contains(x, y)) {
			return this.isLeftToRight ? MONTH_DOWN : MONTH_UP;
		} else if (monthup.contains(x, y)) {
			return this.isLeftToRight ? MONTH_UP : MONTH_DOWN;
		}
		return -1;
	}

	@Override
	protected SicpaRenderingHandler createRenderingHandler() {
		return new SicpaRenderingHandler();
	}

	protected class SicpaRenderingHandler extends RenderingHandler {
		@Override
		public JComponent prepareRenderingComponent(final JXMonthView monthView, final Calendar calendar,
				final CalendarState dayState) {
			JComponent result = super.prepareRenderingComponent(monthView, calendar, dayState);

			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int month = calendar.get(Calendar.MONTH);
			int year = calendar.get(Calendar.YEAR);
			if (dayState == CalendarState.IN_MONTH || dayState == CalendarState.TODAY) {
				ComponentState state = getDayState(day, month, year);
				// ComponentState prevState = getPrevDayState(day, month, year);

				// fix for issue 4 (custom colors on the control)
				Color customFgColor = monthView.getDayForeground(calendar.get(Calendar.DAY_OF_WEEK));
				boolean isForegroundUiResource = customFgColor instanceof UIResource;
				Color fgColor = customFgColor;
				if (isForegroundUiResource) {
					if (monthView.isEnabled()) {
						SubstanceColorScheme cs = SubstanceColorSchemeUtilities.getColorScheme(monthView, state);
						fgColor = cs.getForegroundColor();
					} else {
						float textAlpha = SubstanceColorSchemeUtilities.getAlpha(monthView,
								ComponentState.DISABLED_UNSELECTED);
						fgColor = SubstanceTextUtilities.getForegroundColor(monthView, " ",
								ComponentState.DISABLED_UNSELECTED, textAlpha);
					}
				}
				result.setForeground(fgColor);
			}

			if (dayState == CalendarState.TITLE) {
				// month title
				SubstanceColorScheme cs = SubstanceColorSchemeUtilities.getColorScheme(monthView,
						ComponentState.DEFAULT);
				result.setForeground(cs.getDarkColor());
			}

			if (dayState == CalendarState.LEADING || dayState == CalendarState.TRAILING) {
				float textAlpha = SubstanceColorSchemeUtilities.getAlpha(monthView, ComponentState.DISABLED_UNSELECTED);
				result.setForeground(SubstanceTextUtilities.getForegroundColor(monthView, " ",
						ComponentState.DISABLED_UNSELECTED, textAlpha));
			}
			// previous/next month button
			if (dayState == CalendarState.TITLE) {
				result.setBorder(getTitleBorder());
			}
			result.setOpaque(false);

			return result;
		}

		private Border getTitleBorder() {
			if (SicpaMonthViewUI.this.monthView.isTraversable()) {// previous/next
				// month
				// button
				IconBorder monthup = new IconBorder(SicpaMonthViewUI.this.monthUpImage, SwingConstants.EAST,
						SicpaMonthViewUI.this.monthView.getBoxPaddingX());

				IconBorder yearup = new IconBorder(SicpaMonthViewUI.this.yearUpImage, SwingConstants.EAST,
						SicpaMonthViewUI.this.monthView.getBoxPaddingX());

				IconBorder monthdown = new IconBorder(SicpaMonthViewUI.this.monthDownImage, SwingConstants.WEST,
						SicpaMonthViewUI.this.monthView.getBoxPaddingX());

				IconBorder yeardown = new IconBorder(SicpaMonthViewUI.this.yearDownImage, SwingConstants.WEST,
						SicpaMonthViewUI.this.monthView.getBoxPaddingX());

				Border compoundUp = BorderFactory.createCompoundBorder(yearup, monthup);
				Border compoundDown = BorderFactory.createCompoundBorder(yeardown, monthdown);

				Border compound = BorderFactory.createCompoundBorder(compoundUp, compoundDown);
				Border empty = BorderFactory.createEmptyBorder(2 * SicpaMonthViewUI.this.monthView.getBoxPaddingY(), 0,
						2 * SicpaMonthViewUI.this.monthView.getBoxPaddingY(), 0);
				return BorderFactory.createCompoundBorder(compound, empty);
			}

			return BorderFactory.createEmptyBorder(SicpaMonthViewUI.this.monthView.getBoxPaddingY(),
					SicpaMonthViewUI.this.monthView.getBoxPaddingX(), SicpaMonthViewUI.this.monthView.getBoxPaddingY(),
					SicpaMonthViewUI.this.monthView.getBoxPaddingX());
		}
	}

	// @Override
	// protected int getTraversableGridPositionAtLocation(final int x, final int
	// y) {
	//
	// }

	private ComponentState getDayState(final int dayIndex, final int monthIndex, final int yearIndex) {
		boolean isEnabled = this.monthView.isEnabled();
		try {
			// can't access to DateID class, so use reflection...
			// org.pushingpixels.substance.swingx.SubstanceMonthViewUI$DateId
			Class<?> cd = Class.forName("org.pushingpixels.substance.swingx.SubstanceMonthViewUI$DateId");
			Constructor cons = cd.getConstructor(int.class, int.class, int.class);
			cons.setAccessible(true);
			Object o = cons.newInstance(dayIndex, monthIndex, yearIndex);
			boolean isRollover = o.equals(this.rolloverDateId);
			boolean isSelected = this.selectedDates.contains(o);
			return ComponentState.getState(isEnabled, isRollover, isSelected);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ComponentState.getState(isEnabled, false, false);
	}

	private MouseListener yearChangeListener;
	private Icon yearUpImage;
	private Icon yearDownImage;

	@Override
	protected void installListeners() {
		super.installListeners();
		this.yearChangeListener = createYearMouseClickListener();
		this.monthView.addMouseListener(this.yearChangeListener);
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();
		this.monthView.removeMouseListener(this.yearChangeListener);
		this.yearChangeListener = null;
	}

	private MouseListener createYearMouseClickListener() {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {

				int arrowType = getTraversableGridPositionAtLocation(e.getX(), e.getY());
				if (arrowType == YEAR_UP) {
					nextYear();
					return;
				} else if (arrowType == YEAR_DOWN) {
					previousYear();
					return;
				}

			}
		};
	}

	private void previousYear() {
		Date lowerBound = this.monthView.getLowerBound();
		if (lowerBound == null || lowerBound.before(getFirstDisplayedDay())) {
			Calendar cal = getCalendar();
			cal.add(Calendar.YEAR, -1);
			this.monthView.setFirstDisplayedDay(cal.getTime());
		}
	}

	private void nextYear() {
		Date upperBound = this.monthView.getUpperBound();
		if (upperBound == null || upperBound.after(getLastDisplayedDay())) {
			Calendar cal = getCalendar();
			cal.add(Calendar.YEAR, 1);
			this.monthView.setFirstDisplayedDay(cal.getTime());
		}
	}

	@Override
	protected void installDelegate() {
		super.installDelegate();

		this.yearDownImage = SubstanceImageCreator.getDoubleArrowIconDelta(50, 5, 5, 1, SwingConstants.WEST,
				SubstanceColorSchemeUtilities.getColorScheme(this.monthView, ColorSchemeAssociationKind.MARK,
						ComponentState.ENABLED));

		this.yearUpImage = SubstanceImageCreator.getDoubleArrowIconDelta(50, 5, 5, 1, SwingConstants.EAST,
				SubstanceColorSchemeUtilities.getColorScheme(this.monthView, ColorSchemeAssociationKind.MARK,
						ComponentState.ENABLED));

	}
}
