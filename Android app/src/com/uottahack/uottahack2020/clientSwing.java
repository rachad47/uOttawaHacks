package com.uottahack.uottahack2020;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.sun.glass.ui.Cursor.setVisible;

public class clientSwing implements ActionListener, MqttCallback {
    boolean trigger = false;
    JFrame base = new JFrame();
    JFrame orderFrame = new JFrame();
    JButton b = new JButton("Order Package");
    JLabel incoming = new JLabel("Incoming Packages");
    JTextArea incomingText = new JTextArea("");
    JButton back,order0,order1,order2,order3;
    JTextField address = new JTextField("Address Here");
    String orderType, addr;
    String[] orderTypes = new String[10];
    String[] addrs = new String[10];
    private messaging solace = new messaging("tcp://mr2hd0llj3vxlf.messaging.solace.cloud:1883", "solace-cloud-client", "es6i1emuvigohrfijv4g5mii6e");

    clientSwing() throws MqttException {
        // Base window stuff
        solace.subscribe("OrderArrived");
        b.setBounds(30,280,180, 40);
        incoming.setBounds(65,0,180,30);
        incomingText.setBounds(30,40,180,200);
        incomingText.setEditable(false);
        b.addActionListener(this);

        //Base Window
        base.setSize(260,430);
        base.add(incomingText);
        base.add(incoming);
        base.add(b);
        //Order Window
        orderFrame.setSize(260,430);
        back = new JButton("Back");
        back.setBounds(0,0,70,30);
        back.addActionListener(this);

        address.setBounds(10,40,200,30);
        order0 = new JButton("Order Package #1");
        order0.setBounds(20,80,200,30);
        order1 = new JButton("Order Package #2");
        order1.setBounds(20,120,200,30);
        order2 = new JButton("Order Package #3");
        order2.setBounds(20,160,200,30);
        order3 = new JButton("Order Package #4");
        order3.setBounds(20,200,200,30);
        order0.addActionListener(this);
        order1.addActionListener(this);
        order2.addActionListener(this);
        order3.addActionListener(this);

        base.setLayout(null);
        base.setVisible(true);
        orderFrame.setVisible(false);
        orderFrame.setLayout(null);
        orderFrame.add(back);
        orderFrame.add(order0);
        orderFrame.add(order1);
        orderFrame.add(order2);
        orderFrame.add(order3);
        orderFrame.add(address);
    }

    public static void main(String[] args) throws MqttException {
        new clientSwing();
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception{
        int tmp = 0;
        if (s=="orderArrivedOrderMain"){
            orderType = mqttMessage.toString();
        } else if (s=="orderArrivedAddressMain"){
            addr=mqttMessage.toString();
            for (int i=0;i<10;i++){
                if (orderTypes[i] == orderType && addrs[i] == addr){
                    tmp = i;
                }
            }
        }
        for (int y = tmp;y<9;y++){
            orderTypes[y] = orderTypes[y+1];
            orderTypes[y+1] = "";
            addrs[y] = addrs[y+1];
            addrs[y+1] = "";
        }
        updateList();
    }

    public void updateList(){
        incomingText.setText("");
        String tmp = ("");
        for (int i=0; i<10; i++){
            if (orderTypes[i] != null) {
                tmp = tmp + "Box Type #" + orderTypes[i] + " at " + addrs[i] + "\n";
            }
        }
        incomingText.setText(tmp);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        trigger = false;
        if (e.getSource()==b){
            orderFrame.setVisible(true);
            base.setVisible(false);
        } else
        if (e.getSource()==back){
            base.setVisible(true);
            orderFrame.setVisible(false);
        } else if (e.getSource() == order0){
            solace.publish(address.getText(), "orderReqAdd");
            solace.publish("1", "orderReqNum");
            for (int i=9; i>0; i--){
                if (addrs[i-1]!=null){
                    addrs[i] = addrs[i-1];
                }
                if (orderTypes[i-1]!=null){
                    orderTypes[i] = orderTypes[i-1];
                }
            }
            addrs[0] = address.getText();
            orderTypes[0] = "1";
            updateList();
        } else if (e.getSource() == order1){
            solace.publish(address.getText(), "orderReqAdd");
            solace.publish("2", "orderReqNum");
            for (int i=9; i>0; i--){
                if (addrs[i-1]!=null){
                    addrs[i] = addrs[i-1];
                }
                if (orderTypes[i-1]!=null){
                    orderTypes[i] = orderTypes[i-1];
                    trigger = true;
                }
            }
            addrs[0] = address.getText();
            orderTypes[0] = "2";
            updateList();
        } else if (e.getSource() == order2){
            solace.publish(address.getText(), "orderReqAdd");
            solace.publish("3", "orderReqNum");
            for (int i=9; i>0; i--){
                if (addrs[i-1]!=null){
                    addrs[i] = addrs[i-1];
                }
                if (orderTypes[i-1]!=null){
                    orderTypes[i] = orderTypes[i-1];
                    trigger = true;
                }
            }
            addrs[0] = address.getText();
            orderTypes[0] = "3";
            updateList();
        } else if (e.getSource() == order3){
            solace.publish(address.getText(), "orderReqAdd");
            solace.publish("4", "orderReqNum");
            for (int i=9; i>0; i--){
                if (addrs[i-1]!=null){
                    addrs[i] = addrs[i-1];
                }
                if (orderTypes[i-1]!=null){
                    orderTypes[i] = orderTypes[i-1];
                    trigger = true;
                }
            }
            addrs[0] = address.getText();
            orderTypes[0] = "4";
            updateList();
        }
    }
    @Override
    public void connectionLost(Throwable throwable){

    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
