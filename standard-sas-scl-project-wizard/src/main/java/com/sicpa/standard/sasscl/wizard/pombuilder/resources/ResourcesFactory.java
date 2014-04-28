package com.sicpa.standard.sasscl.wizard.pombuilder.resources;

public class ResourcesFactory {

	public static Resources create() {
		Resources res = new Resources();

		res.addResource(new ResourceBuilder().directory("src/main/resources/config").targetPath("config").includes("*")
				.build());

		res.addResource(new ResourceBuilder().directory("src/main/resources/images").targetPath("config").includes("*")
				.build());

		res.addResource(new ResourceBuilder().directory("src/main/resources").targetPath(".").includes("*")
				.filtering(true).build());
		
		res.addResource(new ResourceBuilder().directory("src/main/resources/spring").targetPath("spring").includes("*")
				.filtering(true).build());

		res.addResource(new ResourceBuilder().directory("src/main/resources/language").targetPath("language")
				.includes("*").build());

		return res;
	}
}
