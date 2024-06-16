package org.real_time_messaging;


import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SimpleWebSocketServer extends WebSocketServer {

    private Set<WebSocket> clients = Collections.synchronizedSet(new HashSet<>());

    public SimpleWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        clients.add(conn);
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clients.remove(conn);
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message from " + conn.getRemoteSocketAddress() + ": " + message);
        broadcast(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            clients.remove(conn);
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started successfully");
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8887;
        WebSocketServer server = new SimpleWebSocketServer(new InetSocketAddress(host, port));
        server.start();
        System.out.println("WebSocket server started on port: " + port);
    }
}

