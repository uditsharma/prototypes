package org.real_time_messaging.v3;

import com.fasterxml.jackson.core.util.JacksonFeature;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.java_websocket.server.WebSocketServer;


public class SlackHttpServer {

    private static final int HTTP_PORT = 8080;
    private static final int WS_PORT = 8887;

    public static void main(String[] args) throws Exception {
        // Start WebSocket server
        WebSocketServer wsServer = new SlackWebSocketServer(WS_PORT);
        wsServer.start();

        // Start Jetty server for HTTP
        Server server = new Server();

        // HTTP Connector
        ServerConnector httpConnector = new ServerConnector(server);
        httpConnector.setPort(HTTP_PORT);
        server.addConnector(httpConnector);

        // Servlet context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Configure Jersey
        ResourceConfig config = new ResourceConfig();
        config.packages("org.real_time_messaging.v3"); // Ensure this matches your package
        // Enable JSON processingconfig.register()
        config.register(JacksonFeature.class); // Enable Jackson JSON provider
        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(config));
        context.addServlet(jerseyServlet, "/*");

        // Start server
        server.start();
        server.join();
    }
}



