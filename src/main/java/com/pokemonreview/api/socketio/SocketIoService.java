package com.pokemonreview.api.socketio;


import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.SocketIOClient;
import com.pokemonreview.api.dto.JoinRoomDto;
import com.pokemonreview.api.repository.UserRepository;
import com.pokemonreview.api.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class SocketIoService {

    private SocketIOServer server;
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public SocketIoService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @PostConstruct
    public void startServer() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9094);
        config.setOrigin("http://localhost:8080");
        config.setOrigin("*");
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

        server.start();
        System.out.println("Socket.IO server started on port 9092");
    }

    public void sendData(String topic, Object data) {
        if (server != null) {
            server.getBroadcastOperations().sendEvent(topic, data);

            System.out.println("Sent socket event: " + topic);
        }
    }
    @PreDestroy
    public void stopServer() {
        if (server != null) {
            server.stop();
            System.out.println("Socket.IO server stopped");
        }
    }
}

