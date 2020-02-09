package com.uottahack.uottahack2020;

import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.TrafficModel;
import com.google.maps.model.TravelMode;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.Instant;

public class Main implements MqttCallback {
    public String startPoint, endPoint;
    public String addressIn, orderIn;
    public String[] addresses, distances, orderType;
    private messagingMain solaceMain = new messagingMain("tcp://mr2hd0llj3vxlf.messaging.solace.cloud:1883", "solace-cloud-client", "es6i1emuvigohrfijv4g5mii6e");
    Main() throws MqttException {
        addresses = new String[10];
        distances = new String[10];
        orderType = new String[10];
    }
    private DistanceMatrixElement calcDist(String origin, String destination) throws InterruptedException, ApiException, IOException {
        GeoApiContext distCalcer = new GeoApiContext.Builder()
                .apiKey("AIzaSyCdSutcW8eSl_PgLiBHU6rLQssmbjqiHYk")
                .build();

        DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(distCalcer);
        DistanceMatrix result = req.origins(origin)
                .departureTime(Instant.now())
                .destinations(destination)
                .mode(TravelMode.DRIVING)
                .avoid(DirectionsApi.RouteRestriction.TOLLS)
                //.trafficModel(TrafficModel.PESSIMISTIC)
                .language("en-US")
                .await();

        DistanceMatrixElement element = result.rows[0].elements[0];
        return element;
    }



    public static void main(String[] args) throws InterruptedException, ApiException, IOException, MqttException {
        Main m = new Main();
        m.calcDist("uOttawa", "122 Gilmour");
        m.solaceMain.subscribe("orderReqAdd");
        m.solaceMain.subscribe("orderReqNum");
        m.solaceMain.subscribe("orderArrivedOrder");
        m.solaceMain.subscribe("orderArrivedAddress");
        /* if message arrives from orderReqAdd channels
        add address to array of addresses
        calculate distance
        send next address,order type to driverSwing.java on orderReqAddMain or orderReqNumMain channel

        if message arrives from orderArrived channels (message content will be type of box ordered and addresses)
        remove the corresponding information from storage array
        send information to clientSwing.java on orderArrivedOrderMain and orderArrivedAddressMain channel
         */
    }

    public int findClosestIndex() throws Exception {
        long tmpDist = calcDist(orderType[0],addresses[0]).duration.inSeconds;
        int tmpIndex = 0;
        for (int i=1; i<10;i++ ){
            if (calcDist(orderType[i],addresses[i]).duration.inSeconds < tmpDist){
                tmpDist = calcDist(orderType[i],addresses[i]).duration.inSeconds;
                tmpIndex = i;
            }
        }
        return tmpIndex;
    }

    public String findClosestOrder() throws InterruptedException, ApiException, IOException {
        String tmp="";
        long tmpDist = calcDist(orderType[0],addresses[0]).duration.inSeconds;
        int tmpIndex = 0;
        for (int i=1; i<10;i++ ){
            if (calcDist(orderType[i],addresses[i]).duration.inSeconds < tmpDist){
                tmpDist = calcDist(orderType[i],addresses[i]).duration.inSeconds;
                tmpIndex = i;
            }
        }
        tmp = orderType[tmpIndex];
        return tmp;
    }

    public String findClosestAddress() throws InterruptedException, ApiException, IOException {
        String tmp="";
        long tmpDist = calcDist(orderType[0],addresses[0]).duration.inSeconds;
        int tmpIndex = 0;
        for (int i=1; i<10;i++ ){
            if (calcDist(orderType[i],addresses[i]).duration.inSeconds < tmpDist){
                tmpDist = calcDist(orderType[i],addresses[i]).duration.inSeconds;
                tmpIndex = i;
            }
        }
        tmp = addresses[tmpIndex];
        return tmp;
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception{
        if (s=="orderReqNum"){
            for (int i=9;i<0;i--){
                orderType[i] = orderType[i-1];
            }
            orderType[0] = mqttMessage.toString();
        } else if (s=="orderReqAdd"){
            addressIn = mqttMessage.toString();
            for (int i=9;i>0;i--){
                addresses[i] = addresses[i-1];
            }
            addresses[0] = addressIn;
        } else if (s=="orderArrivedOrder"){
            orderIn = mqttMessage.toString();
            solaceMain.publish(orderIn,"orderArrivedOrderMain");
            solaceMain.publish(findClosestOrder(), "orderReqNumMain");
            solaceMain.publish(findClosestAddress(), "orderReqAddMain");
            int tmp = findClosestIndex();
            for (int i=tmp; i<9;i++){
                addresses[i] = addresses[i+1];
                addresses[i+1] = "";
                orderType[i] = orderType[i+1];
                orderType[i+1]="";
            }

        }else if (s=="orderArrivedAddress"){
            addressIn=mqttMessage.toString();
            solaceMain.publish(orderIn, "orderArrivedAddressMain");
        }
    }
    @Override
    public void connectionLost(Throwable throwable){

    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

}
