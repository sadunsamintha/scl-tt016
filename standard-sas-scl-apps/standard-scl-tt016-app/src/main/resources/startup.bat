java -Xmx1024m -Xms256m -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.port=21110 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=localhost -jar ${project.artifactId}-${project.version}.jar

