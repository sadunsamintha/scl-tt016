package com.sicpa.standard.sasscl.monitoring.utils;

import java.io.File;
import java.io.Serializable;

import org.apache.commons.io.FileUtils;

public class FileInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String name;
	protected long size;
	protected long time;
	protected boolean directory;
	protected String parent;

	public FileInfo(final File f) {
		this.name = f.getName();
		this.time = f.lastModified();
		this.directory = f.isDirectory();
		if (this.directory) {
			this.size = FileUtils.sizeOfDirectory(f);
		} else {
			this.size = f.length();
		}
		this.parent = f.getParent();
	}

	public FileInfo() {

	}

	public String getName() {
		return this.name;
	}

	public long getSize() {
		return this.size;
	}

	public long getTime() {
		return this.time;
	}

	public boolean isDirectory() {
		return this.directory;
	}

	public String getParent() {
		return this.parent;
	}
}
