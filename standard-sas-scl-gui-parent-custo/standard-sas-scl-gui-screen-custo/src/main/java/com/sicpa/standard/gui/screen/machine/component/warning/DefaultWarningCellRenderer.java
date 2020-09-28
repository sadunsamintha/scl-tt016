package com.sicpa.standard.gui.screen.machine.component.warning;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JList;
import javax.swing.UIManager;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelConfig;
import com.sicpa.standard.gui.utils.PaintUtils;
import com.sicpa.standard.gui.utils.TextUtils;

public class DefaultWarningCellRenderer implements WarningCellRendererPainter {

	public static final String LINE_WRAP = "DefaultWarningCellRenderer.LINE_WRAP";
	public static final String TIME_ZONE = "DefaultWarningCellRenderer.TIME_ZONE";

	protected boolean lineWrap = true;
	protected boolean lineWrapSet = false;

	protected Font smallFont = SicpaFont.getFont(11);
	protected Font bigFont = SicpaFont.getFont(15).deriveFont(Font.BOLD);
	protected DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,
			Locale.getDefault());

	private final String beam_info_code = "ABI";

	@Override
	public void paintCell(final Graphics2D g2, final Message warning, final float animProgress, final int index,
			final JList list, final int width, final int height) {
		Shape oldClip = g2.getClip();

		g2.setComposite(AlphaComposite.SrcOver.derive(animProgress));

		// if the last item
		g2.setFont(getFont(index, list, animProgress));

		Shape newClip = getClip(animProgress, width, height);

		if (animProgress != 1) {
			g2.setClip(newClip);
		}

		fillBackground(g2, newClip, index, list, animProgress, warning);
		paintText(g2, warning, width, height, index, list);

		g2.setClip(oldClip);

		if (animProgress == 1) {
			g2.setColor(Color.BLACK);
			g2.drawLine(0, height - 1, width, height - 1);
		}
	}

	protected void paintText(final Graphics2D g2, final Message warning, final int width, final int height,
			final int index, final JList list) {
		Color fg;
		if (isLastCell(index, list)) {
			fg = getLastCellForeground();
		} else {
			fg = getCellForeground();
		}

		PaintUtils.drawMultiLineText(g2, getText(g2, warning, list), width, true, 0, 0, fg);
	}

	protected void fillBackground(final Graphics2D g2, final Shape clip, final int index, final JList list,
			final float animProgress, final Message message) {
		Color color;
		//Changing the fill color based on code
		String code = message.getCode();
		// if the last item
		if (isLastCell(index, list)) {
			if (message instanceof Error) {
				color = SicpaColor.RED;
			} else if (code != null
				&& code.contains(beam_info_code)) {
				color = SicpaColor.GREEN_DARK;
			} else {
				color = getLastCellColor();
			}
		} else {
			if (message instanceof Error) {
				color = SicpaColor.RED;
			} else if (code != null
				&& code.contains(beam_info_code)) {
				color = SicpaColor.GREEN_DARK;
			} else {
				color = getCellColor();
			}
			// the color of the one before last get darker over time when a new
			// warning is added
			// and the font get smaller
			// if (list.getModel().getSize() - 2 == index)
			// {
			// if (animProgress != 0)
			// {
			// color =
			// (SubstanceColorUtilities.getInterpolatedColor(getCellColor(),
			// getLastCellColor(), animProgress));
			// }
			// }
		}
		g2.setColor(color);
		g2.fill(clip);
	}

	protected Font getFont(final int index, final JList list, final float animProgress) {
		if (isLastCell(index, list)) {
			return this.bigFont;
		} else {
			// the color of the one before last get darker over time when a new
			// warning is added
			// and the font get smaller
			if (list.getModel().getSize() - 2 == index) {
				if (animProgress != 0) {
					return (this.smallFont.deriveFont((float) -4 * animProgress + 15));
				} else {
					return (this.smallFont);
				}
			} else {
				return (this.smallFont);
			}
		}
	}

	protected Shape getClip(final float animProgress, final int w, final int h) {
		Rectangle2D newClip = new Rectangle2D.Float(

		(w / 2 * (1 - animProgress)) - 1,

		(h / 2 * (1 - animProgress)) - 1,

		(w * animProgress) + 1,

		(h * animProgress) + 1);

		return newClip;
	}

	protected String getText(final Graphics g, final Message warning, final JList list) {
		if (!timeZoneSetup) {
			setupTimeZone();
			timeZoneSetup = true;
		}
		String s = "";
		s += this.formatter.format(warning.getTime()) + "\n";
		String code = warning.getCode() == null ? "" : warning.getCode();
		String message = warning.getMessage();
		if (isLineWrap()) {
			s += TextUtils.getWrappedLFText(code, list.getWidth(), g.getFont()) + "\n";
			if (list.isShowing() && list.getWidth() > 0) {
				message = TextUtils.getWrappedLFText(message, list.getWidth(), g.getFont());
			}
		}
		s += message + "\n ";
		return s;
	}

	protected boolean timeZoneSetup;

	protected void setupTimeZone() {

		String stimezone = UIManager.getString(TIME_ZONE);
		if (stimezone != null) {
			TimeZone timezone = TimeZone.getTimeZone(stimezone);
			formatter.setTimeZone(timezone);
		}

	}

	@Override
	public int getCellHeight(final Message warning, final float animProgress, final int index, final JList list) {
		int res;
		Graphics g = GraphicsUtilities.createCompatibleImage(1, 1).getGraphics();

		if (isLastCell(index, list)) {// if the last item
			g.setFont(this.bigFont);
			Rectangle r = TextUtils.getMultiLineStringBounds(getText(g, warning, list), g);
			res = r.height;
		} else {
			g.setFont(this.smallFont);
			Rectangle r = TextUtils.getMultiLineStringBounds(getText(g, warning, list), g);
			res = r.height;
		}
		g.dispose();
		return res;
	}

	protected Color getLastCellColor() {
		return SicpaLookAndFeelConfig.getPCCFrameWarningLastCellbackground();
	}

	protected Color getCellColor() {
		return SicpaLookAndFeelConfig.getPCCFrameWarningCellbackground();
	}

	protected Color getCellForeground() {
		return SicpaLookAndFeelConfig.getPCCFrameWarningCellForeground();
	}

	protected Color getLastCellForeground() {
		return SicpaLookAndFeelConfig.getPCCFrameWarningLastCellForeground();
	}

	protected boolean isLastCell(final int index, final JList list) {
		return index == list.getModel().getSize() - 1;
	}

	public boolean isLineWrap() {

		if (this.lineWrapSet) {
			return this.lineWrap;
		} else {
			Object o = UIManager.get(LINE_WRAP);
			if (o instanceof Boolean) {
				this.lineWrap = (Boolean) o;
			}
			this.lineWrapSet = true;
			return this.lineWrap;
		}
	}
}
