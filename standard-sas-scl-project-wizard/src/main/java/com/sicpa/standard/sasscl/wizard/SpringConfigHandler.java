package com.sicpa.standard.sasscl.wizard;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class SpringConfigHandler {

	private static class SpringConfigItem {
		String key;
		String fileName;

		public SpringConfigItem(String key, String fileName) {
			this.key = key;
			this.fileName = fileName;
		}
	}

	private final List<SpringConfigItem> items = new ArrayList<SpringConfigHandler.SpringConfigItem>();

	public void addItem(String key, String fileName) {
		items.add(new SpringConfigItem(key, fileName));
	}

	public String getConfigString() {
		String res = "";
		String line = "config.put({0}, \"spring\" + File.separator + \"{1}\");\n\t\t";
		for (SpringConfigItem item : items) {
			res += MessageFormat.format(line, item.key, item.fileName);
		}
		return res;
	}

	public void reset() {
		items.clear();
	}
}
