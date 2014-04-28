package com.sicpa.standard.sasscl.wizard.pombuilder.resources;

import java.util.ArrayList;
import java.util.List;

public class Resource {

	private String directory = "";
	private String targetPath = "";
	private boolean filtering;

	private List<String> includes;
	private List<String> excludes;

	public Resource() {
	}

	public Resource(String directory, String targetPath, boolean filtering, List<String> includes, List<String> excludes) {
		this.directory = directory;
		this.targetPath = targetPath;
		this.filtering = filtering;
		this.includes = includes;
		this.excludes = excludes;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public void addInclude(String include) {
		if (includes == null) {
			includes = new ArrayList<String>();
		}
		includes.add(include);
	}

	public void addExclude(String exclude) {
		if (excludes == null) {
			excludes = new ArrayList<String>();
		}
		excludes.add(exclude);
	}

	public boolean isFiltering() {
		return filtering;
	}

	public void setFiltering(boolean filtering) {
		this.filtering = filtering;
	}

	public List<String> getIncludes() {
		return includes;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public String getDirectory() {
		return directory;
	}

	public String getTargetPath() {
		return targetPath;
	}
}
