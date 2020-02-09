package com.uottahack.uottahack2020;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class driverSwing implements ActionListener, MqttCallback {
    JFrame base = new JFrame("Driver Client");
    JTextArea list = new JTextArea("");
    JLabel currOrder = new JLabel("Current Order");
    JLabel label = new JLabel("Pending orders");
    JTextArea currList = new JTextArea("");
    JButton c = new JButton("Completed order");
    String address, order;
    private messagingDriver solace;

    driverSwing() throws MqttException {

        currOrder.setBounds(85,0,140,40);


        label.setBounds(80,78,140,40);


        currList.setBounds(30,40,180,40);
        currList.setEditable(false);


        list.setBounds(30,110,180,180);
        list.setEditable(false);




        c.setBounds (50,310,140,40);
        c.addActionListener(this);

        //base.add(label);
        base.add(currList);
        base.add(currOrder);
        //base.add(list);
        base.add(c);
        base.setSize(260,430);
        base.setLayout(null);
        base.setVisible(true);

        solace = new messagingDriver("tcp://mr2hd0llj3vxlf.messaging.solace.cloud:1883", "solace-cloud-client", "es6i1emuvigohrfijv4g5mii6e");
        solace.subscribe("orderReqNumMain");
        solace.subscribe("orderReqAddMain");

    }
    public static void main(String[] args) throws MqttException {
        new driverSwing();
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception{
        if (s=="orderReqNumMain"){
            order = mqttMessage.toString();
        } else if (s =="orderReqAddMain"){
            address = mqttMessage.toString();
        }
        updateCurrent();
    }

    public void updateCurrent(){
        currOrder.setText( order + " ordered to " + address);
    }

    /*public void updateList(){
        currList.setText("");
        String temp = "";
        for (int i=0; i<10;i++){
            temp = temp;
        }
    }*/

    @Override
    public void actionPerformed(ActionEvent e) {
        solace.publish(order,"orderArrivedOrder");
        solace.publish(address,"orderArrivedAddress");

    }

    @Override
    public void connectionLost(Throwable throwable){

    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
