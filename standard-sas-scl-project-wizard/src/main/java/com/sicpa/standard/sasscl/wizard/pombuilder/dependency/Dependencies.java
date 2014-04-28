package com.sicpa.standard.sasscl.wizard.pombuilder.dependency;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public class Dependencies {

	private static XStream xStream = new XStream();
	{
		xStream.addImplicitCollection(Dependencies.class, "dependencies");
		xStream.alias("dependencies", Dependencies.class);
		xStream.alias("dependency", Dependency.class);
	}

	private List<Dependency> dependencies = new ArrayList<Dependency>();

	public void addDependency(Dependency dependency) {
		dependencies.add(dependency);
	}

	public void addAllDependencies(List<Dependency> dependencies) {
		this.dependencies.addAll(dependencies);
	}

	public String getXML() {
		return xStream.toXML(this);
	}
}
