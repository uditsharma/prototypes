package org.real_time_messaging.v3;

import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Scanner;

public class SlackWebSocketClient extends WebSocketClient {

    private static Gson gson = new Gson();

    public SlackWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from server");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static void main(String[] args) throws URISyntaxException {
        String serverUri = "ws://localhost:8887";
        WebSocketClient client = new SlackWebSocketClient(new URI(serverUri));
        client.connect();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter command (create, join, leave, history, message): ");
            String command = scanner.nextLine();

            switch (command) {
                case "create":
                    System.out.println("Enter channel name: ");
                    String createChannel = scanner.nextLine();
                    client.send(gson.toJson(Map.of("type", "create", "channel", createChannel)));
                    break;
                case "join":
                    System.out.println("Enter channel name: ");
                    String joinChannel = scanner.nextLine();
                    client.send(gson.toJson(Map.of("type", "join", "channel", joinChannel)));
                    break;
                case "leave":
                    System.out.println("Enter channel name: ");
                    String leaveChannel = scanner.nextLine();
                    client.send(gson.toJson(Map.of("type", "leave", "channel", leaveChannel)));
                    break;
                case "history":
                    System.out.println("Enter channel name: ");
                    String historyChannel = scanner.nextLine();
                    client.send(gson.toJson(Map.of("type", "history", "channel", historyChannel)));
                    break;
                case "message":
                    System.out.println("Enter channel name: ");
                    String messageChannel = scanner.nextLine();
                    System.out.println("Enter user name: ");
                    String user = scanner.nextLine();
                    System.out.println("Enter message: ");
                    String content = scanner.nextLine();
                    client.send(gson.toJson(Map.of("type", "message", "channel", messageChannel, "user", user, "content", content)));
                    break;
                default:
                    System.out.println("Unknown command");
                    break;
            }
        }
    }
}

