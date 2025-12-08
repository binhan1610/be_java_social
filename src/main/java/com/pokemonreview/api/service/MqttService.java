package com.pokemonreview.api.service;

import com.pokemonreview.api.models.DataEntity;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

@Service
public class MqttService {

    private final String broker = "h066391a.ala.asia-southeast1.emqxsl.com";
    private final int port = 8883;
    private final String username = "nguyenquoctien";
    private final String password = "20102003";
    private final List<String> topics = List.of(
            "/doan/air_quality/realtime_display",
            "/doan/air_quality/prediction_24h",
            "/doan/air_quality/system_info",
            "/doan/air_quality/debug_sub",
            "/doan/air_quality/log"
    );

    private MqttClient client;
    private final DataService dataService;

    @Autowired
    public MqttService(DataService dataService) {
        this.dataService = dataService;
    }

    public void connect() {
        try {
            if (client != null && client.isConnected()) {
                return;
            }

            String uri = "ssl://" + broker + ":" + port;
            client = new MqttClient(uri, MqttClient.generateClientId());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setAutomaticReconnect(true);
            options.setKeepAliveInterval(60);

            // TLS trust all
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };
            sslContext.init(null, trustAllCerts, new SecureRandom());
            options.setSocketFactory(sslContext.getSocketFactory());

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.err.println("[MQTT] Connection lost: " + cause.getMessage());
                    reconnect();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    long timestamp = new Date().getTime();
                    DataEntity entity = new DataEntity();
                    entity.setId(timestamp);
                    entity.setTopic(topic);
                    entity.setData(message.toString());
                    dataService.saveData(entity);
                    System.out.println("[MQTT] Message from " + topic + ": " + message.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            System.out.println("[MQTT] Connecting to broker...");
            client.connect(options);
            System.out.println("[MQTT] Connected!");

            for (String topic : topics) {
                client.subscribe(topic);
                System.out.println("[MQTT] Subscribed: " + topic);
            }

        } catch (Exception e) {
            System.err.println("[MQTT] Connect failed: " + e.getMessage() + ", retry in 5s");
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            reconnect();
        }
    }

    private void reconnect() {
        try {
            if (client != null) {
                client.disconnect();
            }
        } catch (Exception ignored) {}
        connect();
    }

    public void publish(String topic, String payload) {
        try {
            if (client != null && client.isConnected()) {
                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(1);
                client.publish(topic, message);
                System.out.println("[MQTT] Published to " + topic + ": " + payload);
            } else {
                System.err.println("[MQTT] Cannot publish, client not connected");
            }
        } catch (Exception e) {
            System.err.println("[MQTT] Publish failed: " + e.getMessage());
        }
    }
}
