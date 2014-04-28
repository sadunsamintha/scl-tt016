package com.sicpa.standard.sasscl.benchmark.chart;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Utility class that allows Strings, Images or other Transferables (rtf) to be placed in the clipboard.<br>
 * If an exception occurs a warning message is shown.<br>
 * Examples<br>
 * In your application just call<br>
 * String txt="some text";<br>
 * ClipboardUtils.setContents(txt,null);<br>
 * <br>
 * some JComponent<br>
 * JPanel panel=new MyPanel();<br>
 * Clipboardutils.setContentsImageContents(panel,null);<br>
 */
public class ClipboardUtils {
	private final static Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();

	private ClipboardUtils() {
	}

	/**
	 * Sets the current contents of the clipboard to the specified image and registers the specified clipboard owner as
	 * the owner of the new contents
	 * 
	 * @param content
	 * @param owner
	 * @return boolean false if exception occurred
	 */
	public static boolean setContents(final Image image, final ClipboardOwner owner) {
		return setContents(new ImageTransferable(image), owner);
	}

	/**
	 * Sets the current contents of the clipboard to the specified StringBuffer and registers the specified clipboard
	 * owner as the owner of the new contents
	 * 
	 * @param content
	 * @param owner
	 * @return boolean false if exception occurred
	 */
	public static boolean setContents(final String content, final ClipboardOwner owner) {
		return setContents(new StringSelection(content), owner);

	}

	/**
	 * Sets the current contents of the clipboard to an graphical image of the component and registers the specified
	 * clipboard owner as the owner of the new contents This method isn't just called setContents(Component comp,
	 * ClipboardOwner owner) because Components might implement the Transferable Interface themselves which would lead
	 * to confusion with the setContents(Transferable trans,ClipboardOwner owner)-method.
	 * 
	 * @param Component
	 *            comp
	 * @return boolean false if exception occurred
	 */
	public static boolean setImageContents(final Component comp, final ClipboardOwner owner) {
		return setContents(new ImageTransferable(comp), owner);
	}

	/**
	 * Fills the clipboard with a RTF-Transferable.
	 * 
	 * @param ByteArrayOutputStream
	 *            - an rtf in form of ByteArrayOutputStream
	 */
	public static void setRtfContents(final ByteArrayOutputStream outStream, final ClipboardOwner owner) {
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(outStream.toByteArray());
		setContents(new RtfTransferable(byteInputStream), owner);
	}

	public static void setRtfContents(final byte[] data, final ClipboardOwner owner) {
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
		setContents(new RtfTransferable(byteInputStream), owner);
	}

	/**
	 * Sets the current contents of the clipboard to the specified transferable object and registers the specified
	 * clipboard owner as the owner of the new contents. Shows warning message if an exception occured
	 * 
	 * @param contents
	 * @return boolean false if an exception occured
	 */
	public static boolean setContents(final Transferable contents, final ClipboardOwner owner) {
		boolean result = true;
		try {
			CLIPBOARD.setContents(contents, owner);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	/**
	 * Draws a given component into a buffered image and returns that image. Can be used to copy to images of components
	 * to the clipboard
	 * 
	 * @param Component
	 *            comp
	 * @return BufferedImage
	 */
	public static BufferedImage toBufferedImage(final Component comp) {
		BufferedImage tempImage = new BufferedImage(comp.getSize().width, comp.getSize().height,
				BufferedImage.TYPE_INT_RGB);
		Graphics tempGraphics = tempImage.getGraphics();
		comp.paint(tempGraphics);
		tempGraphics.dispose();
		return tempImage;
	}

	/**
	 * Turns a BufferedImage into a jpg and then into an array of bytes, which can be useful for generating rtf.
	 * 
	 * @param image
	 *            - a buffered image
	 * @return byte[] array of bytes containing the image
	 * @throws IOException
	 */
	public static byte[] toByteArray(final BufferedImage image) throws IOException {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", outstream);
		return outstream.toByteArray();
	}
}

class ImageTransferable implements Transferable {
	private Image image;

	ImageTransferable(final Component comp) {
		this.image = ClipboardUtils.toBufferedImage(comp);
	}

	ImageTransferable(final Image image) {
		this.image = image;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}

	public boolean isDataFlavorSupported(final DataFlavor flavor) {
		return DataFlavor.imageFlavor.equals(flavor);
	}

	public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
		if (!isDataFlavorSupported(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return this.image;
	}

}

class RtfTransferable implements Transferable {
	public DataFlavor rtfDataFlavor;
	private ByteArrayInputStream stream;

	RtfTransferable(final ByteArrayInputStream stream) {
		try {
			this.rtfDataFlavor = new DataFlavor("text/rtf; class=java.io.InputStream");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		this.stream = stream;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { this.rtfDataFlavor };
	}

	public boolean isDataFlavorSupported(final DataFlavor flavor) {
		return flavor.equals(this.rtfDataFlavor);
	}

	public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
		if (!isDataFlavorSupported(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return this.stream;
	}
}