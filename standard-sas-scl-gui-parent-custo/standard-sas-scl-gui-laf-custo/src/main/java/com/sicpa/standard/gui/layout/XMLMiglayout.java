package com.sicpa.standard.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import net.miginfocom.swing.MigLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sicpa.standard.gui.utils.ThreadUtils;

public class XMLMiglayout {

	public static void main(final String[] args) {
		XMLMiglyaoutTest.main(args);
	}

	private Container panel;
	private URL file;
	private Object caller;
	private String content;

	public XMLMiglayout(final Container panel, final URL file, final Object caller) throws FileNotFoundException {
		this.panel = panel;
		this.file = file;
		this.caller = caller;
	}

	public XMLMiglayout(final Container panel, final String name, final Object caller) throws FileNotFoundException {
		this(panel, caller.getClass().getResource(name + ".xml"), caller);
	}

	private String getNewContent() throws IOException {

		InputStreamReader isr = new InputStreamReader(this.file.openStream());
		BufferedReader in = new BufferedReader(isr);
		String line;
		StringBuilder sbuff = new StringBuilder();
		while ((line = in.readLine()) != null) {
			sbuff.append(line).append('\n');
		}
		in.close();
		isr.close();

		return sbuff.toString();
	}

	public void maybeUpdateLayout() throws SAXException, IOException, ParserConfigurationException {
		String newContent = getNewContent();
		if (!newContent.equals(this.content)) {
			this.content = newContent;

			// schema validation
			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			Schema schema = factory.newSchema(new StreamSource(getClass().getResourceAsStream("miglayout.xsd")));
			Validator validator = schema.newValidator();
			Source source = new StreamSource(new StringReader(this.content));
			validator.validate(source);

			// load dom doc
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(this.content)));

			// do layout
			applyLayout(doc);
		}
	}

	private void applyLayout(final Document doc) {
		this.panel.removeAll();
		Element root = doc.getDocumentElement();
		String generalConstraints = root.getAttribute("generalConstraints");
		String columnConstraints = root.getAttribute("columnConstraints");
		String rowConstraints = root.getAttribute("rowConstraints");
		final MigLayout miglayout = new MigLayout(generalConstraints, columnConstraints, rowConstraints);
		this.panel.setLayout(miglayout);

		final NodeList listComp = root.getElementsByTagName("component");

		ThreadUtils.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < listComp.getLength(); i++) {
					Element eComp = (Element) listComp.item(i);

					String name = eComp.getAttribute("name");
					String constraints = eComp.getAttribute("constraints");
					try {
						Component comp = getComponent(name);
						XMLMiglayout.this.panel.add(comp, constraints);
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				XMLMiglayout.this.panel.validate();
				XMLMiglayout.this.panel.repaint();
			}
		});

		if (!XMLMiglayoutRefreshManager.isManaged(this)) {
			XMLMiglayoutRefreshManager.manage(this);
		}
	}

	public void applyLayout() throws ParserConfigurationException, SAXException, IOException {
		this.content = "";
		maybeUpdateLayout();
	}

	private Component getComponent(final String name) throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		if (name.length() == 0) {
			throw new IllegalArgumentException("component name cannot be empty");
		}
		if (!name.contains(".")) {
			String sgetter = "get" + name.substring(0, 1).toUpperCase() + (name.length() > 1 ? name.substring(1) : "");
			Method getter = this.caller.getClass().getDeclaredMethod(sgetter);
			getter.setAccessible(true);
			return (Component) getter.invoke(this.caller);
		} else {
			int index = name.lastIndexOf('.');
			String classname = name.substring(0, index);
			String sgetter = name.substring(index + 1);

			Method getter = Class.forName(classname).getDeclaredMethod(sgetter);
			getter.setAccessible(true);
			return (Component) getter.invoke(null);
		}
	}
}
