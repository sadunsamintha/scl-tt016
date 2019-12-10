package com.sicpa.standard.gui.components.propertySheet;

import java.awt.BorderLayout;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.swing.LookAndFeelTweaks;

/**
 * PropertySheetPage. <br>
 * 
 */
public class PropertySheetPage extends JPanel {
	private static final long serialVersionUID = 1L;
	private PropertySheetPanel sheet;
	private JTextArea message;
	private boolean scanParentClass;

	public PropertySheetPage() {
		setLayout(new BorderLayout());

		this.message = new JTextArea();
		add(this.message, BorderLayout.NORTH);

		this.sheet = new PropertySheetPanel();

		this.sheet.setMode(PropertySheet.VIEW_AS_FLAT_LIST);

		this.sheet.setDescriptionVisible(false);
		this.sheet.setSortingCategories(false);
		this.sheet.setSortingProperties(false);

		add(this.sheet, BorderLayout.CENTER);
	}

	public void setShowCategorie(final boolean flag) {
		if (flag) {
			this.sheet.setMode(PropertySheet.VIEW_AS_CATEGORIES);
		} else {
			this.sheet.setMode(PropertySheet.VIEW_AS_FLAT_LIST);
		}
	}

	public void setSortingProperties(final boolean flag) {
		this.sheet.setSortingProperties(flag);
	}

	public void setSortingCategories(final boolean flag) {
		this.sheet.setSortingCategories(flag);
	}

	public void setToolBarVisible(final boolean flag) {
		this.sheet.setToolBarVisible(flag);
	}

	public void setDescriptionVisible(final boolean flag) {
		this.sheet.setDescriptionVisible(flag);
	}

	public void setBean(final Object data, final String title) {
		DefaultBeanInfoResolver resolver = new DefaultBeanInfoResolver();

		Class c = data.getClass();
		ArrayList<PropertyDescriptor> list = new ArrayList<PropertyDescriptor>();
		boolean first = true;

		// scan the class hierarchie to find properties descripteur
		while (c != null && !c.equals(Object.class)) {
			try {
				BeanInfo beanInfo = resolver.getBeanInfo(c);
				if (beanInfo != null) {
					for (PropertyDescriptor desc : beanInfo.getPropertyDescriptors()) {
						list.add(desc);
					}
				}
			} catch (ClassNotFoundException e) {
				if (first) {// only show exception for the first class , because
							// all the parents may not have an beanInfo
					e.printStackTrace();
				}
			}

			if (this.scanParentClass) {
				c = c.getSuperclass();
			} else {
				c = null;
			}
			first = false;
		}

		PropertyDescriptor[] tab = new PropertyDescriptor[list.size()];
		this.sheet.setProperties(list.toArray(tab));
		this.sheet.readFromObject(data);

		// everytime a property change, update the bean with it
		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent evt) {
				Property prop = (Property) evt.getSource();
				prop.writeToObject(data);
			}
		};
		this.sheet.addPropertySheetChangeListener(listener);

		this.message.setText(title);
		LookAndFeelTweaks.makeMultilineLabel(this.message);
	}

	public void addPropertySheetChangeListener(final PropertyChangeListener lis) {
		this.sheet.addPropertySheetChangeListener(lis);
	}

	public void setScanParentClass(final boolean scanParentClass) {
		this.scanParentClass = scanParentClass;
	}

	public boolean isScanParentClass() {
		return this.scanParentClass;
	}

}
