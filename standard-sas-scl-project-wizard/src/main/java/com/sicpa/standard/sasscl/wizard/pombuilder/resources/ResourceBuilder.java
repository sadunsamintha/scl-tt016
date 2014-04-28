package com.sicpa.standard.sasscl.wizard.pombuilder.resources;

public class ResourceBuilder {

	private Resource resource = new Resource();

	public ResourceBuilder directory(String directory) {
		resource.setDirectory(directory);
		return this;
	}

	public ResourceBuilder targetPath(String targetPath) {
		resource.setTargetPath(targetPath);
		return this;
	}

	public ResourceBuilder filtering(boolean filtering) {
		resource.setFiltering(filtering);
		return this;
	}

	public ResourceBuilder includes(String... includes) {
		if (includes != null) {
			for (String s : includes) {
				resource.addInclude(s);
			}
		}
		return this;
	}

	public ResourceBuilder excludes(String... excludes) {
		if (excludes != null) {
			for (String s : excludes) {
				resource.addExclude(s);
			}
		}
		return this;
	}

	public Resource build() {
		return resource;
	}
}
