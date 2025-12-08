package com.pokemonreview.api;

import com.pokemonreview.api.service.MqttService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class ApiApplication {

    private final MqttService mqttService;

    @Autowired
    public ApiApplication(MqttService mqttService) {
        this.mqttService = mqttService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    // Tự động gọi sau khi Spring context khởi tạo xong
    @PostConstruct
    public void init() {
        mqttService.connect();

        // Test publish
        mqttService.publish("/doan/air_quality/debug_pub", "{\"hello\": \"mqtt\"}");
    }
}
