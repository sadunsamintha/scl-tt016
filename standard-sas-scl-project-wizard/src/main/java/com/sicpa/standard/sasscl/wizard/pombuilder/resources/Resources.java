package com.sicpa.standard.sasscl.wizard.pombuilder.resources;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;

public class Resources {

	private static XStream xstream = new XStream();
	{
		xstream.alias("resources", Resources.class);
		xstream.addImplicitCollection(Resources.class, "resources");
		xstream.alias("resource", Resource.class);
		ClassAliasingMapper mapperInclude = new ClassAliasingMapper(xstream.getMapper());
		mapperInclude.addClassAlias("include", String.class);
		xstream.registerLocalConverter(Resource.class, "includes", new CollectionConverter(mapperInclude));

		ClassAliasingMapper mapperExclude = new ClassAliasingMapper(xstream.getMapper());
		mapperExclude.addClassAlias("exclude", String.class);
		xstream.registerLocalConverter(Resource.class, "excludes", new CollectionConverter(mapperExclude));
	}

	private List<Resource> resources = new ArrayList<Resource>();

	public void addResource(Resource resource) {
		resources.add(resource);
	}

	public String getXML() {
		return xstream.toXML(this);
	}

	public static void main(String[] args) {

		Resources rs = new Resources();

		Resource r = new Resource();
		r.setDirectory("src/main/resources/config");
		r.setTargetPath("config");
		r.addInclude("*");
		r.addExclude("version");
		r.addExclude("startup");

		rs.addResource(r);

		String xml = xstream.toXML(rs);
		System.out.println(xml);
	}
}
