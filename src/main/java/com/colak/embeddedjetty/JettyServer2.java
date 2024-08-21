package com.colak.embeddedjetty;

import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class JettyServer2 {

    // http://localhost:8080/
    public void start() throws Exception {
        Server server = new Server(8080);

        WebAppContext context = new WebAppContext();
        Resource baseResource = findBaseResource(context);

        context.setBaseResource(baseResource);

        context.setLogUrlOnStart(true);
        context.setContextPath("/");
        context.setWelcomeFiles(new String[]{"welcome.html"});

        context.setParentLoaderPriority(true);

        server.setHandler(context);

        server.start();
    }

    private static Resource findBaseResource(WebAppContext context) {
        ResourceFactory resourceFactory = ResourceFactory.of(context);

        try {
            // Look for resource in classpath (this is the best choice when working with a jar/war archive)
            ClassLoader classLoader = context.getClass().getClassLoader();
            URL webXml = classLoader.getResource("/WEB-INF/web.xml");
            if (webXml != null) {
                URI uri = webXml.toURI().resolve("../..").normalize();
                return resourceFactory.newResource(uri);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("Bad ClassPath reference for: WEB-INF", e);
        }

        // Look for resource in common file system paths
        try {
            Path pwd = Path.of(System.getProperty("user.dir")).toAbsolutePath();
            Path targetDir = pwd.resolve("target");
            if (Files.isDirectory(targetDir)) {
                try (Stream<Path> listing = Files.list(targetDir)) {
                    Path embeddedServletServerDir = listing
                            .filter(Files::isDirectory)
                            .filter((path) -> path.getFileName().toString().startsWith("embedded-servlet-server-"))
                            .findFirst()
                            .orElse(null);
                    if (embeddedServletServerDir != null) {
                        return resourceFactory.newResource(embeddedServletServerDir);
                    }
                }
            }

            // Try the source path next
            Path srcWebapp = pwd.resolve("src/main/webapp/");
            if (Files.exists(srcWebapp)) {
                return resourceFactory.newResource(srcWebapp);
            }
        } catch (Throwable t) {
            throw new RuntimeException("Unable to find web resource in file system", t);
        }

        throw new RuntimeException("Unable to find web resource ref");
    }

    public static void main(String[] args) throws Exception {
        JettyServer2 jettyServer = new JettyServer2();
        jettyServer.start();
    }
}
