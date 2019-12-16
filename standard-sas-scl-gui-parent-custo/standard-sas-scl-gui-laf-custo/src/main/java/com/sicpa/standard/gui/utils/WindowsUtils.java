package com.sicpa.standard.gui.utils;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

public class WindowsUtils {
  /**
   * add an action on window closing
   * 
   * @param window
   * @param action the exit action
   */
  public static void addCloseAction(final Window window, final AbstractAction action) {
    if (action == null) {
      throw new IllegalArgumentException("The action cannot be null");
    }
    if (window == null) {
      throw new IllegalArgumentException("The window cannot be null");
    }

    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(final WindowEvent e) {
        action.actionPerformed(null);
      }
    });
  }

  // --------------------- Frame decoration
  /**
   * Make the JFrame only have the close icon and to be not resizeable
   * 
   * @param frame the JFrame to make NOT resizable/minimizable/maximizable
   */
  public static void showOnlyCloseDecoration(final JFrame frame) {
    if (frame == null) {
      throw new IllegalArgumentException("The JFrame cannot be null");
    }
    frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
    frame.setResizable(false);
  }

  /**
   * Hide the decoration around a JFrame
   * 
   * @param frame the JFrame to make NOT resizable/minimizable/maximizable/closeable
   */
  public static void hideDecoration(final JFrame frame) {
    if (frame == null) {
      throw new IllegalArgumentException("The JFrame cannot be null");
    }
    frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
  }

  /**
   * Hide the decoration around a JDialog
   * 
   * @param frame the JDialog to make NOT resizable/minimizable/maximizable/closeable
   */
  public static void hideDecoration(final JDialog frame) {
    if (frame == null) {
      throw new IllegalArgumentException("The JDialog cannot be null");
    }
    frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
  }

  /**
   * make the given window fit the entire screen
   * 
   * @param window
   */
  public static void setFullScreenWindow(final Window window) {
    if (window == null) {
      throw new IllegalArgumentException("The window cannot be null");
    }
    window.setSize(Toolkit.getDefaultToolkit().getScreenSize());
  }

  public static void setOpaque(final Window w, final boolean flag) {
    if (!flag) {
      if (w instanceof JFrame) {
        ((JFrame) w).setUndecorated(true);
        WindowsUtils.hideDecoration((JFrame) w);
      } else if (w instanceof JDialog) {
        ((JDialog) w).setUndecorated(true);
        WindowsUtils.hideDecoration((JDialog) w);
      }
    }
    w.setBackground(new Color(0, 0, 0, 0));
  }

  public static void setOpacity(final Window w, final float alpha) {
    w.setOpacity(alpha);
  }

  public static void addKeyListener(final Window window, final int key, final boolean isAltDown,
      final boolean isCtrlDown, final boolean isShiftDown, final ActionListener action) {

    Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
      @Override
      public void eventDispatched(final AWTEvent event) {

        if (window.isActive() && event instanceof KeyEvent) {
          KeyEvent kevt = (KeyEvent) event;

          if (isAltDown && !kevt.isAltDown()) {
            return;
          }
          if (isCtrlDown && !kevt.isControlDown()) {
            return;
          }
          if (isShiftDown && !kevt.isShiftDown()) {
            return;
          }

          if (kevt.getKeyCode() == key) {
            if (kevt.getID() == KeyEvent.KEY_RELEASED) {
              action.actionPerformed(null);
            }
          }
        }
      }
    }, AWTEvent.KEY_EVENT_MASK);
  }

  public static void addKeyListener(final Component comp, final int key, final boolean isAltDown,
      final boolean isCtrlDown, final boolean isShiftDown, final ActionListener action) {

    Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
      @Override
      public void eventDispatched(final AWTEvent event) {

        if (comp.isShowing() && event instanceof KeyEvent) {
          KeyEvent kevt = (KeyEvent) event;

          if (isAltDown && !kevt.isAltDown()) {
            return;
          }
          if (isCtrlDown && !kevt.isControlDown()) {
            return;
          }
          if (isShiftDown && !kevt.isShiftDown()) {
            return;
          }

          if (kevt.getKeyCode() == key) {
            if (kevt.getID() == KeyEvent.KEY_RELEASED) {
              action.actionPerformed(null);
            }
          }
        }
      }
    }, AWTEvent.KEY_EVENT_MASK);
  }

  public static void hideCursor(RootPaneContainer window) {
    BufferedImage buff = GraphicsUtilities.createCompatibleTranslucentImage(1, 1);
    Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(buff, new Point(), "");
    window.getGlassPane().setCursor(c);
    window.getGlassPane().setVisible(true);
  }
}
