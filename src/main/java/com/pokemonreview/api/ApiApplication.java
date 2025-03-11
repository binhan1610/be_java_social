	package com.pokemonreview.api;

	import org.springframework.boot.SpringApplication;
	import org.springframework.boot.autoconfigure.SpringBootApplication;
	import org.eclipse.paho.client.mqttv3.*;

	@SpringBootApplication
	public class ApiApplication {
//		public static void main(String[] args) {
//			String broker = "tcp://broker.hivemq.com:1883";
//			String clientId = "JavaClient";
//			String topic = "test/topic";
//
//			try {
//				MqttClient client = new MqttClient(broker, clientId);
//				client.connect();
//
//				client.setCallback(new MqttCallback() {
//					@Override
//					public void connectionLost(Throwable cause) {
//						System.out.println("Connection lost!");
//					}
//
//					@Override
//					public void messageArrived(String topic, MqttMessage message) throws Exception {
//						System.out.println("Received message: " + new String(message.getPayload()));
//					}
//
//					@Override
//					public void deliveryComplete(IMqttDeliveryToken token) {
//						System.out.println("Message delivered successfully!");
//					}
//				});
//
//				client.subscribe(topic);
//				MqttMessage message = new MqttMessage("Hello from Java via HiveMQ".getBytes());
//				client.publish(topic, message);
//				MqttMessage message1 = new MqttMessage("hello bình an".getBytes());
//				client.publish("an", message1);
//
//			} catch (MqttException e) {
//				e.printStackTrace();
//			}
//		}

		public static void main(String[] args) {
			SpringApplication.run(ApiApplication.class, args);
		}
	}
