package com.colak.embeddedjetty;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

public class JettyServer {

    // http://localhost:8080/hello
    public void start() throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.setContextPath("/hello");

        server.setHandler(context);
        context.addServlet(new ServletHolder(new HelloServlet()), "/*");

        server.start();
    }

    public static void main(String[] args) throws Exception {
        JettyServer jettyServer = new JettyServer();
        jettyServer.start();
    }
}
