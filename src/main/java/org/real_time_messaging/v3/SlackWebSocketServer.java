package org.real_time_messaging.v3;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class SlackWebSocketServer extends WebSocketServer {


    private Map<String, WebSocket> clientMap = new HashMap<>();

    public SlackWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Extract user info from query parameters
        String user = handshake.getResourceDescriptor().replace("/websocket?", "").replace("user=", "");

        // Associate WebSocket connection with user
        conn.setAttachment(user);
        clientMap.put(user, conn);
        System.out.println("User " + user + " connected.");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String user = conn.getAttachment();
        if (user != null) {
            clientMap.remove(user);
            System.out.println("Removed " + user + " from active connection list ");
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message from " + conn.getRemoteSocketAddress() + ": " + message);
        handleMessage(conn, message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started ");
    }

    private void handleMessage(WebSocket conn, String message) {
        Gson gson = new Gson();
        Message msg = gson.fromJson(message, Message.class);
        handleMessageBroadcast(conn, msg);
    }

    private void handleMessageBroadcast(WebSocket conn, Message msg) {
        String channel = msg.getChannel();
        Gson gson = new Gson();
        String json = gson.toJson(msg);
        Channel ch = PersistenceManager.getChannel(channel);
        for (String member : ch.getMembers()) {
            WebSocket webSocket = clientMap.get(member);
            webSocket.send(json);
        }
    }

    public static void main(String[] args) {
        int port = 8887;
        SlackWebSocketServer server = new SlackWebSocketServer(port);
        server.start();
        System.out.println("WebSocket server started on port " + port);
    }
}


