package com.sicpa.standard.gui.components.propertySheet;

import java.beans.BeanInfo;

/**
 * DefaultBeanInfoResolver. <br>
 * 
 */
public class DefaultBeanInfoResolver // implements BeanInfoResolver
{

	public DefaultBeanInfoResolver() {
		super();
	}

	public BeanInfo getBeanInfo(final Class<?> clazz) throws ClassNotFoundException {
		if (clazz == null) {
			return null;
		}

		String classname = clazz.getName();

		// look for .impl.basic., remove it and call getBeanInfo(class)
		int index = classname.indexOf(".impl.basic");
		if (index != -1 && classname.endsWith("Basic")) {
			classname = classname.substring(0, index)
					+ classname.substring(index + ".impl.basic".length(), classname.lastIndexOf("Basic"));
			try {
				return getBeanInfo(Class.forName(classname));
			} catch (ClassNotFoundException e) {
				return null;
			}
		} else {
			BeanInfo beanInfo;
			try {
				beanInfo = (BeanInfo) Class.forName(classname + "BeanInfo").newInstance();
				return beanInfo;
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

}
