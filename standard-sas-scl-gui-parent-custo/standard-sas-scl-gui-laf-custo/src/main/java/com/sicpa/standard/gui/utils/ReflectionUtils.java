package com.sicpa.standard.gui.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtils {

	public static Field getField(final Class clazz, final String fieldName) {
		if (clazz == null) {
			throw new NoSuchFieldError(fieldName);
		}
		Field res = null;
		try {
			res = clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			res = getField(clazz.getSuperclass(), fieldName);
		}
		return res;
	}

	private Map<String, Method> methods;

	private Map<String, Method> getMethods() {
		if (this.methods == null) {
			this.methods = new HashMap<String, Method>();
		}
		return this.methods;
	}

	private Method getMethod(final String field, final Class<?> c) throws NoSuchMethodException {
		return getMethod(field, c, c);
	}

	private Method getMethod(final String field, final Class<?> lookIn, final Class<?> baseClassObject)
			throws NoSuchMethodException {

		Method method = getMethods().get(field + ":" + baseClassObject);

		try {
			if (method == null) {
				try {
					method = lookIn.getDeclaredMethod("get" + field);
					method.setAccessible(true);
				} catch (Exception e) {
					method = lookIn.getDeclaredMethod("is" + field);
					method.setAccessible(true);
				}
				getMethods().put(field + ":" + baseClassObject, method);
			}

		} catch (Exception e) {
			if (lookIn.getSuperclass() != null) {
				return getMethod(field, lookIn.getSuperclass(), baseClassObject);
			} else {
				throw new NoSuchMethodException("get" + field + " or is" + field + " doesn't exists");
			}
		}
		return method;
	}

	public Object getValue(String field, final Object t) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (field == null || field.isEmpty()) {
			return null;
		}
		field = Character.toUpperCase(field.charAt(0)) + field.substring(1);

		String[] tab = field.split("\\.");
		Method method;
		Object caller = t;
		for (String s : tab) {
			if (caller == null) {
				return null;
			} else {
				method = getMethod(s, caller.getClass());
				caller = method.invoke(caller);
			}
		}
		return caller;
	}
}
