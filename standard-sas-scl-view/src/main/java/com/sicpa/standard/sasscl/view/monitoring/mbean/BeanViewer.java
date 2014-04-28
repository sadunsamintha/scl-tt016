package com.sicpa.standard.sasscl.view.monitoring.mbean;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class BeanViewer extends JPanel {

	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		SicpaLookAndFeel.install();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(800, 600);
				f.getContentPane().setLayout(new MigLayout("fill"));
				BeanViewer v = new BeanViewer();
				JTextField text = new JTextField();
				v.setBean(text);
				f.getContentPane().add(v);
				f.getContentPane().add(text, "south");
				f.setVisible(true);
			}
		});
	}

	private static final Logger logger = LoggerFactory.getLogger(BeanViewer.class);

	protected Object bean;
	protected JPanel mainPanel;
	protected List<String> ignoredProperties;
	protected List<String> classesToDetail;

	protected Timer refreshTimer = new Timer(5000, new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent e) {
			timerTick();
		}
	});

	protected void timerTick() {
		if (getMainPanel().isShowing() && bean != null) {
			setBean(bean);
		}
	}

	public BeanViewer(final Object bean) {
		this();
		setBean(bean);
	}

	public BeanViewer() {
		initGUI();
		ignoredProperties = new ArrayList<String>();
		classesToDetail = new ArrayList<String>();
		ignoredProperties.add("class");
		refreshTimer.start();
	}

	private void initGUI() {
		setLayout(new MigLayout("ltr,fill"));
		add(SmallScrollBar.createLayerSmallScrollBar(new JScrollPane(getMainPanel())), "grow");
	}

	public void setBean(final Object bean) {
		this.bean = bean;

		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				buildBeanView();
			}
		});
	}

	protected void buildBeanView() {

		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(bean.getClass());

		getMainPanel().removeAll();
		for (PropertyDescriptor desc : descriptors) {
			if (ignoredProperties.contains(desc.getName())) {
				continue;
			}
			Method readMethod = desc.getReadMethod();

			JLabel label = new JLabel(desc.getName());
			JLabel valueLabel = new JLabel();
			BeanViewer child = null;
			valueLabel.setForeground(SicpaColor.BLUE_DARK);
			try {
				if (readMethod != null) {

					Object readValue = readMethod.invoke(bean);
					if (readValue instanceof ImageIcon) {
						valueLabel.setIcon((Icon) readValue);
					} else if (readValue != null && classesToDetail.contains(readValue.getClass().getName())) {
						child = new BeanViewer(readValue);
						child.setBorder(new TitledBorder(desc.getName()));
					} else {
						String text = readValue + "";
						if (text.contains("\n")) {
							text = "<html>" + text.replace("\n", "<br>");
						}
						valueLabel.setText(text);
					}
					if (valueLabel.getText().isEmpty()) {
						valueLabel.setText(" ");
					}
				}
			} catch (Exception e) {
				valueLabel.setText(e.getMessage());
				valueLabel.setForeground(SicpaColor.RED);
				logger.error("", e);
			}

			if (readMethod != null) {
				if (child == null) {
					getMainPanel().add(label, "wrap");
					getMainPanel().add(valueLabel, "gapleft 50, gapbottom 5 ,  wrap");
				} else {
					getMainPanel().add(child, "wrap");
				}
			}
		}
		getMainPanel().revalidate();
		revalidate();
	}

	public JPanel getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = new JPanel(new MigLayout("ltr,inset 0 0 0 0, gap 0 0 0 0 "));
		}
		return this.mainPanel;
	}

	public void addPropertyToIgnore(final String property) {
		this.ignoredProperties.add(property);
	}

	public void addClassToDetailList(final String className) {
		this.classesToDetail.add(className);
	}
}
