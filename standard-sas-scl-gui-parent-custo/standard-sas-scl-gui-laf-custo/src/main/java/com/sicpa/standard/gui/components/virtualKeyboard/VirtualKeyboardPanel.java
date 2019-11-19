package com.sicpa.standard.gui.components.virtualKeyboard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton.Direction;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.plaf.ui.SicpaButtonUI;
import com.sicpa.standard.gui.utils.Pair;

public class VirtualKeyboardPanel extends JPanel {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				UIManager.put(SicpaButtonUI.ROLLOVER_EFFECT, Boolean.FALSE);
				
				GAP_BETWEEN_BUTTON = false;

				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				final JTextField p = new JTextField();

				final VirtualKeyboardPanel k = new VirtualKeyboardPanel(p);

				CapsLockVirtualKeyboardButton capslocks = (CapsLockVirtualKeyboardButton) k.getButton("{capsLock}");
				capslocks.setDirection(Direction.DOWN);
				k.addAction("{capsLock}", VirtualKeyboardPanel.getTaskCapsLockONByDefault());

				k.addAvailableSpecialButton(new SpecialVirtualKeyboardButton("#", "{symbol}"));
				k.addAvailableSpecialButton(new SpecialVirtualKeyboardButton("abc", "{alpha}"));

				k.addLayoutConstraint(" ", "span 7,grow,");
				k.addLayoutConstraint("{del}", "span 2,grow");

				String[] defaultLayout = new String[5];
				defaultLayout[0] = "1234567890";
				defaultLayout[1] = "QWERTYUIOP";
				defaultLayout[2] = "ASDFGHJKL";
				defaultLayout[3] = "{symbol}ZXCVBNM";
				defaultLayout[4] = "{capsLock} {del}";
				k.setDefaultLayout(defaultLayout);

				f.getContentPane().setLayout(new MigLayout("fill"));
				f.getContentPane().add(p, "south");

				f.getContentPane().add(k, "");

				String[] altLayout = new String[5];
				altLayout[0] = "/*-+";
				altLayout[1] = "%&?!";
				altLayout[2] = ",()=";
				altLayout[3] = "[]\\{\\}";
				altLayout[4] = ";:{del}{alpha}";

				k.addAlternativeLayout("symbol", altLayout);

				k.addAction("{symbol}", new IVirtualKeyboardButtonTask() {
					@Override
					public void buttonClicked(VirtualKeyboardButton button, VirtualKeyboardPanel panel) {
						k.changeLayout("symbol");
					}
				});

				k.addAction("{alpha}", new IVirtualKeyboardButtonTask() {
					@Override
					public void buttonClicked(VirtualKeyboardButton button, VirtualKeyboardPanel panel) {
						k.resetLayout();
					}
				});

				f.pack();
				f.setVisible(true);
				SicpaLookAndFeelCusto.turnOffButtonStateTracking();
			}
			
		});
	}

	private Map<String, VirtualKeyboardButton> tagButtonMapping = new HashMap<String, VirtualKeyboardButton>();
	private Map<String, IVirtualKeyboardButtonTask> buttonActionMapping = new HashMap<String, IVirtualKeyboardButtonTask>();
	private Map<String, Pair<Color, Color>> tagColorMapping = new HashMap<String, Pair<Color, Color>>();
	// map <layout name ,map<>>
	private Map<String, Map<String, String>> layoutConstraintsMapping = new HashMap<String, Map<String, String>>();;

	private Map<String, List<String>> layouts = new HashMap<String, List<String>>();

	private String currentLayout = "initial";

	public static final String I18N_BACK = GUIi18nManager.SUFFIX + "virtualKeyboard.removeLast";
	public static final String I18N_CLEAR = GUIi18nManager.SUFFIX + "virtualKeyboard.clear";
	public static final String I18N_ALT = GUIi18nManager.SUFFIX + "virtualKeyboard.alt";
	public static final String I18N_CTRL = GUIi18nManager.SUFFIX + "virtualKeyboard.ctrl";

	public static final String DEFAULT_KEYBOARD_KEY = "com.sicpa.standard.gui.components.virtualKeyboard.default";

	private static final long serialVersionUID = 1L;

	private List<VirtualKeyboardButton> buttons;

	public static boolean GAP_BETWEEN_BUTTON = true;

	public static boolean WITH_BUTTON_PRESSED_FEEDBACK = true;

	/**
	 * Constructor
	 * 
	 * @param comp
	 *            the target text field
	 * @param withNumber
	 *            if the numeric key has to be shown
	 * 
	 */
	public VirtualKeyboardPanel(final JTextComponent comp) {
		super();
		this.buttons = new ArrayList<VirtualKeyboardButton>();
		initGUI();
		if (comp != null) {
			addVirtualKeyboardListener(new DefaultVirtualKeyboardListener(comp));
		}
		init();
	}

	public VirtualKeyboardPanel() {
		this(null);
	}

	protected boolean isGapPresent() {
		return GAP_BETWEEN_BUTTON;
	}

	private void init() {
		this.tagColorMapping.put("{del}", new Pair<Color, Color>(Color.BLUE, Color.WHITE));
		this.tagColorMapping.put("{clr}", new Pair<Color, Color>(Color.BLUE, Color.WHITE));
		this.tagColorMapping.put("{alt}", new Pair<Color, Color>(Color.BLUE, Color.WHITE));
		this.tagColorMapping.put("{ctrl}", new Pair<Color, Color>(Color.BLUE, Color.WHITE));

		addAction("{capsLock}", getTaskCapsLockONByDefault());

		addAvailableSpecialButton(createSpecialButton("{clr}", I18N_CLEAR));
		addAvailableSpecialButton(createSpecialButton("{alt}", I18N_ALT));
		addAvailableSpecialButton(createSpecialButton("{ctrl}", I18N_CTRL));
		addAvailableSpecialButton(createSpecialButton("{del}", I18N_BACK));
		addAvailableSpecialButton(createSpecialButton("{-}", "-/+"));
		addAvailableSpecialButton(new CapsLockVirtualKeyboardButton("{capsLock}"));

	}

	private void initGUI() {

		String s = "";
		if (!isGapPresent()) {
			s = "inset 0 0 0 0, gap 0 0 0 0";
		}

		setLayout(new MigLayout("fill," + s, "", ""));
	}

	public void addAvailableSpecialButton(final SpecialVirtualKeyboardButton button) {
		this.tagButtonMapping.put(button.buttonName, button);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				fireVirtualKeyboardKeyPressed(button, true);
			}
		});
	}

	public void addAvailableSpecialButton(String text, String name) {
		addAvailableSpecialButton(new SpecialVirtualKeyboardButton(text, name));
	}

	public VirtualKeyboardButton getButton(String name) {
		return tagButtonMapping.get(name);
	}

	public void addAlternativeLayoutConstraint(final String layoutName, final String buttonName,
			final String constraints) {

		Map<String, String> map = this.layoutConstraintsMapping.get(layoutName);
		if (map == null) {
			map = new HashMap<String, String>();
			this.layoutConstraintsMapping.put(layoutName, map);
		}
		map.put(buttonName, constraints);
	}

	public void addLayoutConstraint(final String buttonName, final String constraints) {
		addAlternativeLayoutConstraint("initial", buttonName, constraints);
	}

	public void addAction(final String buttonName, final IVirtualKeyboardButtonTask task) {
		this.buttonActionMapping.put(buttonName, task);
	}

	public void addAlternativeLayout(final String name, final String... buttonsRows) {

		List<String> layout = this.layouts.get(name);
		if (layout == null) {
			layout = new ArrayList<String>();
			this.layouts.put(name, layout);
		}
		for (String row : buttonsRows) {
			layout.add(row);
		}
	}

	public void setDefaultLayout(String... rows) {
		for (String row : rows) {
			addButtonsRow(row);
		}
	}

	private void addButtonsRowInternal(final String buttons) {

		String special = "";
		boolean first = true;
		boolean inSpecial = false;
		for (int i = 0; i < buttons.length(); i++) {
			char c = buttons.charAt(i);

			if (c == '\\') {
				i++;
				char nextChar = buttons.charAt(i);
				addANormalButtonToLayout(first, nextChar);
				first = false;

			} else if (c == '{') {
				inSpecial = true;
				special = "{";
			} else if (c == '}') {
				special += "}";
				inSpecial = false;
				if (special.equals("{ }")) {
					add(new JLabel(), (first ? ",newline," : ""));

				} else {
					VirtualKeyboardButton button = this.tagButtonMapping.get(special);
					if (button != null) {
						add(button, getContraints(first, button.buttonName));
						this.buttons.add(button);
					} else {

						throw new IllegalArgumentException(special + " is not a possible key");
					}
				}
				first = false;
				special = "";
			} else if (inSpecial) {
				special += c;
			} else {
				addANormalButtonToLayout(first, c);
				first = false;
			}
		}
	}

	private void addANormalButtonToLayout(final boolean first, final char c) {
		VirtualKeyboardButton button = getKeyboardButton(String.valueOf(c));
		this.buttons.add(button);
		add(button, getContraints(first, button.buttonName));
	}

	public void addButtonsRow(final String buttons) {

		List<String> layout = this.layouts.get("initial");
		if (layout == null) {
			layout = new ArrayList<String>();
			this.layouts.put("initial", layout);
		}
		layout.add(buttons);

		addButtonsRowInternal(buttons);
	}

	public void changeLayout(final String name) {
		this.currentLayout = name;

		removeAll();
		this.buttons.clear();

		for (String row : this.layouts.get(name)) {
			addButtonsRowInternal(row);
		}

		revalidate();
		repaint();
	}

	public void resetLayout() {
		changeLayout("initial");
	}

	public String getCurrentLayout() {
		return this.currentLayout;
	}

	private String getContraints(final boolean first, final String buttonName) {

		Map<String, String> mapConstraints = this.layoutConstraintsMapping.get(this.currentLayout);

		String constraints = null;
		if (mapConstraints != null) {
			constraints = mapConstraints.get(buttonName);
		}
		if (constraints == null) {
			constraints = "grow, h 50 , w 50";
		}

		return constraints + (first ? ",newline," : "");
	}

	public static boolean USING_SMALL_BUTTON = false;

	private final Map<String, VirtualKeyboardButton> normalButtonCache = new HashMap<String, VirtualKeyboardButton>();

	private VirtualKeyboardButton getKeyboardButton(final String text) {
		VirtualKeyboardButton button = normalButtonCache.get(text);
		if (button == null) {
			button = new VirtualKeyboardButton(text, text, false);
			if (button.getUI() instanceof SicpaButtonUI) {
				((SicpaButtonUI) button.getUI()).setSmallButton(USING_SMALL_BUTTON);
			}
			button.setName(text);
			button.setFocusable(false);
			button.addActionListener(this.keylistener);
			button.setFont(getFont());
			normalButtonCache.put(text, button);
		}
		return button;
	}

	private ActionListener keylistener = new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			fireVirtualKeyboardKeyPressed((VirtualKeyboardButton) e.getSource(), false);
		}
	};

	protected static class FaddingBorder extends LineBorder {

		protected Color faddedoutColor;
		protected Color activeColor;

		public FaddingBorder(Color color, int thickness) {
			super(color, thickness);
			faddedoutColor = new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 0);
			activeColor = color;
			setLineColor(faddedoutColor);
		}

		private static final long serialVersionUID = 1L;

		public void startFadeout(final JButton button) {
			setLineColor(activeColor);
			button.repaint();
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					setLineColor(faddedoutColor);
					button.repaint();
				}
			}, 250);

		}

		public void setLineColor(Color c) {
			lineColor = c;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			if (getLineColor().getAlpha() == 0) {
				return;
			}
			super.paintBorder(c, g, x, y, width, height);
		}
	}

	private void installCustomColors(final VirtualKeyboardButton button) {
		Pair<Color, Color> c = this.tagColorMapping.get(button.buttonName);
		if (c != null) {
			if (c.getValue1() != null) {
				button.setBackground(c.getValue1());
			}
			if (c.getValue2() != null) {
				button.setForeground(c.getValue2());
			}
		}
	}

	private SpecialVirtualKeyboardButton createSpecialButton(final String name, final String langKey) {
		final SpecialVirtualKeyboardButton button = new SpecialVirtualKeyboardButton(GUIi18nManager.get(langKey), name);
		if (button.getUI() instanceof SicpaButtonUI) {
			((SicpaButtonUI) button.getUI()).setSmallButton(USING_SMALL_BUTTON);
		}
		button.setName(langKey);
		button.setFocusable(false);
		installCustomColors(button);
		return button;
	}

	// --------------------------------------
	// -------------CUSTOM EVENT HANDLING----
	// --------------------------------------

	public static class VirtualKeyboardEvent {
		private String key;
		private boolean special;

		public VirtualKeyboardEvent(final String key, final boolean special) {
			this.key = key;
			this.special = special;
		}

		public String getKey() {
			return this.key;
		}

		public boolean isSpecial() {
			return this.special;
		}
	}

	public static interface VirtualKeyboardListener extends EventListener {
		void keyPressed(VirtualKeyboardEvent evt);
	}

	public static class DefaultVirtualKeyboardListener implements VirtualKeyboardListener {
		private JTextComponent comp;

		public DefaultVirtualKeyboardListener(final JTextComponent comp) {
			this.comp = comp;
		}

		@Override
		public void keyPressed(final VirtualKeyboardEvent evt) {
			String text = this.comp.getText();
			if (evt.isSpecial()) {
				if (evt.getKey().equals("{del}")) {
					if (text.length() > 0) {
						this.comp.setText(text.substring(0, text.length() - 1));
					}
				} else if (evt.getKey().equals("{clr}")) {
					this.comp.setText("");
				} else if (evt.getKey().equals("{-}")) {
					try {
						Double.parseDouble(text);
						if (text.startsWith("-")) {
							text = text.substring(1);
						} else {
							text = "-" + text;
						}
						this.comp.setText(text);
					} catch (Exception e) {
					}
				}
			} else {
				this.comp.setText(text + evt.key);
			}

		}
	}

	protected VirtualKeyboardListener keyboardEventInterceptor;

	public void setKeyboardEventInterceptor(final VirtualKeyboardListener keyboardEventInterceptor) {
		this.keyboardEventInterceptor = keyboardEventInterceptor;
	}

	public synchronized void addVirtualKeyboardListener(final VirtualKeyboardListener l) {
		this.listenerList.add(VirtualKeyboardListener.class, l);
	}

	public synchronized void removeVirtualKeyboard(final VirtualKeyboardListener l) {
		this.listenerList.remove(VirtualKeyboardListener.class, l);
	}

	protected void fireVirtualKeyboardKeyPressed(final VirtualKeyboardButton button, final boolean isSpecial) {

		VirtualKeyboardEvent e = new VirtualKeyboardEvent(button.buttonName, isSpecial);
		if (this.keyboardEventInterceptor != null) {
			this.keyboardEventInterceptor.keyPressed(e);
		}
		if (e.getKey() != null) {
			Object[] listeners = this.listenerList.getListenerList();
			for (int i = listeners.length - 2; i >= 0; i -= 2) {
				if (listeners[i] == VirtualKeyboardListener.class) {
					((VirtualKeyboardListener) listeners[i + 1]).keyPressed(e);
				}
			}
		}
		if (isSpecial) {
			executeButtonTask(button);
		}
	}

	protected void executeButtonTask(final VirtualKeyboardButton button) {
		IVirtualKeyboardButtonTask task = this.buttonActionMapping.get(button.buttonName);
		if (task != null) {
			task.buttonClicked(button, this);
		}
	}

	public static VirtualKeyboardPanel getNumericKeyboard(final JTextComponent comp) {
		VirtualKeyboardPanel p = new VirtualKeyboardPanel(comp);
		p.addButtonsRow("123");
		p.addButtonsRow("456");
		p.addButtonsRow("789");
		p.addButtonsRow(".0{clr}");
		return p;
	}

	public static VirtualKeyboardPanel getQWERTZKeyboard(final JTextComponent comp) {
		VirtualKeyboardPanel p = new VirtualKeyboardPanel(comp);
		p.addButtonsRow("1234567890");
		p.addButtonsRow("QWERTZUIOP");
		p.addButtonsRow("ASDFGHJKL");
		p.addButtonsRow("YXCVBNM {del}{clr}");
		return p;
	}

	public static VirtualKeyboardPanel getAZERTYKeyboard(final JTextComponent comp) {
		VirtualKeyboardPanel p = new VirtualKeyboardPanel(comp);

		p.addButtonsRow("1234567890");
		p.addButtonsRow("AZERTYUIOP");
		p.addButtonsRow("QSDFGHJKLM");
		p.addButtonsRow("WXCVBN {del}{clr}");
		return p;
	}

	public static VirtualKeyboardPanel getQWERTYKeyboard(final JTextComponent comp) {
		VirtualKeyboardPanel p = new VirtualKeyboardPanel(comp);

		p.addButtonsRow("1234567890");
		p.addButtonsRow("QWERTYUIOP");
		p.addButtonsRow("ASDFGHJKL");
		p.addButtonsRow("ZXCVBNM {del}{clr}");

		return p;
	}

	public static VirtualKeyboardPanel getAlphaSortedKeyboard(final JTextComponent comp) {
		VirtualKeyboardPanel p = new VirtualKeyboardPanel(comp);
		p.addButtonsRow("1234567890");
		p.addButtonsRow("ABCDEFGHIJ");
		p.addButtonsRow("KLMNOPQRST");
		p.addButtonsRow("UVWXYZ { }{del}{clr}");

		return p;
	}

	public static VirtualKeyboardPanel getDefaultKeyboard(final JTextComponent comp) {
		VirtualKeyboardPanel p = null;
		Object res = UIManager.get(DEFAULT_KEYBOARD_KEY);

		if (res instanceof VirtualKeyboardType) {
			VirtualKeyboardType kbType = (VirtualKeyboardType) res;
			p = kbType.getKeyboard(comp);
		} else {
			p = getAlphaSortedKeyboard(comp);
		}
		return p;
	}

	private static VirtualKeyboardDialog dialog;

	public static void setDialog(VirtualKeyboardDialog dialog) {
		VirtualKeyboardPanel.dialog = dialog;
	}

	public static class MouseClickKeyboardDialogListener extends MouseAdapter {
		JTextComponent comp;
		VirtualKeyboardPanel kb;

		public MouseClickKeyboardDialogListener(final JTextComponent comp, final VirtualKeyboardPanel kb) {
			this.comp = comp;
			this.kb = kb;
			if (dialog == null) {
				dialog = new VirtualKeyboardDialog();
			}
			comp.addMouseListener(this);
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			if (!SicpaLookAndFeelCusto.isVirtualKeyboardOn()) {
				return;
			}
			if (this.comp.isEnabled()) {
				if (this.comp.isEditable()) {
					dialog.setOwner(this.kb, (JComponent) e.getSource());
					dialog.setLocation(dialog.computePosition());
					dialog.setVisible(true);
				}
			}
		}
	}

	public static void attachKeyboardDialog(final JTextComponent comp, final VirtualKeyboardPanel kb) {
		try {
			new MouseClickKeyboardDialogListener(comp, kb);
		} catch (HeadlessException e) {
		}

	}

	public List<VirtualKeyboardButton> getButtons() {
		return this.buttons;
	}

	public static class SpecialVirtualKeyboardButton extends VirtualKeyboardButton {

		private static final long serialVersionUID = 1L;

		public SpecialVirtualKeyboardButton(String text, String name) {
			super(text, name, true);
		}
	}

	public static class VirtualKeyboardButton extends JButton {
		private static final long serialVersionUID = 1L;
		String buttonName;
		boolean isSpecial;
		FaddingBorder border;

		 Color borderColor = SicpaColor.YELLOW;

		public VirtualKeyboardButton(final String text, final String name, final boolean isSpecial) {
			super(text);
			this.buttonName = name;
			this.isSpecial = isSpecial;
			setFocusable(false);
			if (isSpecial) {
				setBackground(Color.BLUE);
				setForeground(Color.WHITE);
			}

			if (WITH_BUTTON_PRESSED_FEEDBACK) {
				border = new FaddingBorder(borderColor, 4);
				setBorder(border);
			}
			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					thisActionPerformed();
				}
			});
		}

		protected void thisActionPerformed() {
			if (WITH_BUTTON_PRESSED_FEEDBACK) {
				border.startFadeout(this);
			}
		}

		public void setBorderColor(Color borderColor) {
			this.borderColor = borderColor;
		}

		public String getButtonName() {
			return this.buttonName;
		}

		public boolean isSpecial() {
			return this.isSpecial;
		}

		public void setButtonName(final String buttonName) {
			this.buttonName = buttonName;
		}

		public void setSpecial(final boolean isSpecial) {
			this.isSpecial = isSpecial;
		}
	}

	protected static class CapsLockVirtualKeyboardButton extends SpecialVirtualKeyboardButton {

		private static final long serialVersionUID = 1L;
		DirectionButton delegate;

		public CapsLockVirtualKeyboardButton(final String name) {
			super("", name);
			this.delegate = new DirectionButton(Direction.DOWN);
			this.delegate.setWithShadow(false);
		}

		@Override
		public void paint(final Graphics g) {
			this.delegate.setSize(getSize());
			this.delegate.paint(g);
		}

		public void setDirection(final Direction direction) {
			this.delegate.setDirection(direction);
		}
	}

	public static IVirtualKeyboardButtonTask getTaskCapsLockONByDefault() {
		return getTaskCapsLockUPByDefault(true);
	}

	public static IVirtualKeyboardButtonTask getTaskCapsLockOFFByDefault() {
		return getTaskCapsLockUPByDefault(false);
	}

	private static IVirtualKeyboardButtonTask getTaskCapsLockUPByDefault(final boolean onbyDefault) {
		return new IVirtualKeyboardButtonTask() {
			boolean capsOn = onbyDefault;

			@Override
			public void buttonClicked(final VirtualKeyboardButton button, final VirtualKeyboardPanel panel) {
				for (VirtualKeyboardButton b : panel.buttons) {
					if (!b.isSpecial) {
						if (this.capsOn) {
							b.buttonName = b.buttonName.toLowerCase();
							b.setText(b.getText().toLowerCase());
							((CapsLockVirtualKeyboardButton) button).setDirection(Direction.UP);

						} else {
							b.setText(b.getText().toUpperCase());
							b.buttonName = b.buttonName.toUpperCase();
							((CapsLockVirtualKeyboardButton) button).setDirection(Direction.DOWN);
						}
					}
				}
				this.capsOn = !this.capsOn;
			}
		};
	}

	public static interface IVirtualKeyboardButtonTask {
		void buttonClicked(VirtualKeyboardButton button, VirtualKeyboardPanel panel);

	}

	@Override
	public void setFont(final Font font) {
		super.setFont(font);
		if (this.buttons != null) {
			for (JButton button : this.buttons) {
				button.setFont(font);
			}
		}
	}
}
