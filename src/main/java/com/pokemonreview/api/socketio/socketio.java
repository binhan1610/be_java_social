package com.pokemonreview.api.socketio;


import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class socketio {

    private SocketIOServer server;

    @PostConstruct
    public void startServer() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);
        config.setOrigin("http://localhost:8080");
        server = new SocketIOServer(config);

        // Lắng nghe khi có kết nối từ client
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                System.out.println("Client connected: " + client.getSessionId());
            }
        });

        // Lắng nghe khi client ngắt kết nối
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                System.out.println("Client disconnected: " + client.getSessionId());
            }
        });

        server.addEventListener("join_room_hehe", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                socketIOClient.joinRoom("hehe");
                System.out.println("user join room hehe");
            }
        });

        server.addEventListener("join_room_hihi", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                socketIOClient.joinRoom("hihi");
                System.out.println("user join room hihi");

            }
        });

        server.addEventListener("send_message_hehe", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                server.getRoomOperations("hehe").sendEvent("message_response", "My friend: " + s);
            }
        });

        server.addEventListener("send_message_hihi", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                server.getRoomOperations("hihi").sendEvent("message_response", "My friend: " + s);
            }
        });

        server.start();
        System.out.println("Socket.IO server started on port 9092");
    }

    @PreDestroy
    public void stopServer() {
        if (server != null) {
            server.stop();
            System.out.println("Socket.IO server stopped");
        }
    }
}

