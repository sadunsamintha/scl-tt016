How to run the application
-------------------------------------

The application uses some native libraries. Therefore you should configure the location where such libraries are in your system.
To do so, you have to add the following VM argument: -Djava.library.path=<path>.
If you run the application with the eclipse platform, that path will be: -Djava.library.path=dll

If you don't add the VM argument, probably you will have the following error:

	java.lang.reflect.InvocationTargetException
	[...]
	Caused by: java.lang.UnsatisfiedLinkError: no standard-plc-1.0 in java.library.path


There are some environments that even you configure the eclipse in this way, there is still a problem with the native libraries location.
The error will be:

	java.lang.reflect.InvocationTargetException
	[...]
	Caused by: java.lang.UnsatisfiedLinkError: C:\projects\standard-sas-scl\standard-sas-scl-app\dll\standard-plc-1.0.dll: Can't find dependent libraries

In this case the solution is to add the native library location to your PATH in your environment configuration.

There shouldn't be problem when the application is run from the artifact created by the assembly goal of MAVEN.


