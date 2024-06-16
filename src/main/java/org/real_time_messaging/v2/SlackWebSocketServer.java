package org.real_time_messaging.v2;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import com.google.gson.Gson;

import java.net.InetSocketAddress;
import java.util.*;

public class SlackWebSocketServer extends WebSocketServer {

    private static class Message {
        String channel;
        String user;
        String content;
        long timestamp;
    }

    private static class Channel {
        String name;
        Set<WebSocket> members = new HashSet<>();
        List<Message> messages = new ArrayList<>();
    }

    private Map<String, Channel> channels = new HashMap<>();
    private Gson gson = new Gson();

    public SlackWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
        channels.values().forEach(channel -> channel.members.remove(conn));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message: " + message);
        Map<String, String> messageMap = gson.fromJson(message, Map.class);

        switch (messageMap.get("type")) {
            case "create":
                handleCreateChannel(messageMap);
                break;
            case "join":
                handleJoinChannel(conn, messageMap);
                break;
            case "leave":
                handleLeaveChannel(conn, messageMap);
                break;
            case "history":
                handleHistoryRequest(conn, messageMap);
                break;
            case "message":
                handleNewMessage(conn, messageMap);
                break;
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started successfully");
    }

    private void handleCreateChannel(Map<String, String> messageMap) {
        String channelName = messageMap.get("channel");
        channels.put(channelName, new Channel());
        channels.get(channelName).name = channelName;
        System.out.println("Channel created: " + channelName);
    }

    private void handleJoinChannel(WebSocket conn, Map<String, String> messageMap) {
        String channelName = messageMap.get("channel");
        Channel channel = channels.get(channelName);
        if (channel != null) {
            channel.members.add(conn);
            System.out.println("User joined channel: " + channelName);
        }
    }

    private void handleLeaveChannel(WebSocket conn, Map<String, String> messageMap) {
        String channelName = messageMap.get("channel");
        Channel channel = channels.get(channelName);
        if (channel != null) {
            channel.members.remove(conn);
            System.out.println("User left channel: " + channelName);
        }
    }

    private void handleHistoryRequest(WebSocket conn, Map<String, String> messageMap) {
        String channelName = messageMap.get("channel");
        Channel channel = channels.get(channelName);
        if (channel != null) {
            String history = gson.toJson(channel.messages);
            conn.send(history);
            System.out.println("Sent history for channel: " + channelName);
        }
    }

    private void handleNewMessage(WebSocket conn, Map<String, String> messageMap) {
        String channelName = messageMap.get("channel");
        String user = messageMap.get("user");
        String content = messageMap.get("content");

        Channel channel = channels.get(channelName);
        if (channel != null) {
            Message newMessage = new Message();
            newMessage.channel = channelName;
            newMessage.user = user;
            newMessage.content = content;
            newMessage.timestamp = System.currentTimeMillis();

            channel.messages.add(newMessage);
            String messageJson = gson.toJson(newMessage);

            for (WebSocket member : channel.members) {
                member.send(messageJson);
            }
            System.out.println("Broadcasted message in channel: " + channelName);
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8887;
        WebSocketServer server = new SlackWebSocketServer(new InetSocketAddress(host, port));
        server.start();
        System.out.println("WebSocket server started on port: " + port);
    }
}

