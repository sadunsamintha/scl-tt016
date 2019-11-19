package com.sicpa.standard.gui.screen.DMS.log;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.plaf.SicpaColor;

public class UserLog {
	public static enum EStatusLog {

		success(null), failure(null);

		private Icon icon;

		private EStatusLog(final Icon icon) {
			this.icon = icon;
		}

		public Icon getIcon() {
			return this.icon;
		}

		private static Icon failureIcon;

		private static Icon getFailureIcon() {
			if (failureIcon == null) {
				failureIcon = getIcon(SicpaColor.RED);
			}
			return failureIcon;
		}

		private static Icon successIcon;

		private static Icon getSuccessIcon() {
			if (successIcon == null) {
				successIcon = getIcon(SicpaColor.BLUE_MEDIUM);
			}
			return successIcon;
		}

		private static Icon getIcon(final Color c) {
			BufferedImage buff = GraphicsUtilities.createCompatibleTranslucentImage(20, 20);
			Graphics g = buff.createGraphics();
			g.setColor(c);
			g.fillRect(0, 0, 20, 20);
			g.dispose();
			return new ImageIcon(buff);
		}
	}

	private Date date;
	private String itemCode;
	private String operation;
	private String result;
	private EStatusLog status;

	public UserLog(final String itemCode, final String operation, final String result, final EStatusLog status) {
		this(itemCode, operation, result, status, new Date(System.currentTimeMillis()));
	}

	public UserLog(final String itemCode, final String operation, final String result, final EStatusLog status,
			final Date date) {
		super();
		this.date = date;
		this.itemCode = itemCode;
		this.operation = operation;
		this.result = result;
		this.status = status;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public String getItemCode() {
		return this.itemCode;
	}

	public void setItemCode(final String itemCode) {
		this.itemCode = itemCode;
	}

	public String getOperation() {
		return this.operation;
	}

	public void setOperation(final String operation) {
		this.operation = operation;
	}

	public String getResult() {
		return this.result;
	}

	public void setResult(final String result) {
		this.result = result;
	}

	public EStatusLog getStatus() {
		return this.status;
	}

	public void setStatus(final EStatusLog status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return this.date.toLocaleString() + ":" + this.status + ":" + this.itemCode + ":" + this.operation + ":"
				+ this.result;
	}

}
