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

        server.addEventListener("join_chat_user", JoinRoomDto.class, new DataListener<JoinRoomDto>() {
            @Override
            public void onData(SocketIOClient socketIOClient, JoinRoomDto data, AckRequest ackRequest) throws Exception {
                long roomId = data.getId();
                long userId = data.getUserId();

                socketIOClient.joinRoom(String.valueOf(roomId));
                System.out.println("User " + userId + " joined room: " + roomId);
            }
        });


        server.addEventListener("join_chat_group", JoinRoomDto.class, new DataListener<JoinRoomDto>() {
            @Override
            public void onData(SocketIOClient socketIOClient, JoinRoomDto data, AckRequest ackRequest) throws Exception {
                long roomId = data.getId();
                long userId = data.getUserId();

                socketIOClient.joinRoom(String.valueOf(roomId));
                System.out.println("User " + userId + " joined room: " + roomId);
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

