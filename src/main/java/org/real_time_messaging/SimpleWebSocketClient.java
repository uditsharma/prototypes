package org.real_time_messaging;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class SimpleWebSocketClient extends WebSocketClient {

    public SimpleWebSocketClient(URI serverUri) {
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
        WebSocketClient client = new SimpleWebSocketClient(new URI(serverUri));
        client.connect();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.nextLine();
            client.send(message);
        }
    }
}

