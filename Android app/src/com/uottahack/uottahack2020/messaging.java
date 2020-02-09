package com.uottahack.uottahack2020;

import org.eclipse.paho.client.mqttv3.*;

public class messaging implements MqttCallback{
    private final MqttClient client;
    public messaging(String url, String user, String pw) throws MqttException {
        client = new MqttClient(url, "hacker");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(user);
        options.setPassword(pw.toCharArray());
        client.setCallback(this);
        client.connect(options);
    }
    public void subscribe(String input){
        try {
            client.subscribe(input);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void publish(String input, String topic){
        try {
            client.publish(topic, new MqttMessage(input.getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        if (s == "orderArrivedOrderMain"){
            String str = mqttMessage.toString();

        } else if (s == "orderArrivedAddressMain") {

        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
