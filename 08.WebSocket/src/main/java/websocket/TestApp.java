package websocket;

import org.json.JSONObject;
import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

/*
The timestamp of any sensor data posted to the backend has to be current time in UTC timeformat for this client to be udpated

Webclient to get data updates from backend/send data to backend
If messageHandler == Add then the client is registered to send data to the backend (usage is irrelevant)
If messageHandler == Sensor then the client is registered to receive data updates from backend (usage specifies which type of sensor data the client will be updated about)
Example JSON to send data to backend:

{ "data": [{"sensorId":"22", "type":"ANGLE", "value":88, "timeStamp":"2020-01-01 08:00:00"},{"sensorId":"22", "type":"ANGLE", "value":89, "timeStamp":"2020-01-01 08:00:00"},{"sensorId":"1", "type":"CO2", "value":90, "timeStamp":"2020-01-01 08:00:00"},{"sensorId":"1", "type":"CO2", "value":91, "timeStamp":"2020-01-01 08:00:00"},{"sensorId":"1", "type":"CO2", "value":92, "timeStamp":"2020-01-01 08:00:00"},{"sensorId":"1", "type":"CO2", "value":93, "timeStamp":"2020-01-01 08:00:00"}]}

*/

public class TestApp {

  protected static String loginURL = "http://localhost:8080/montigem-be/api/auth/login";
  protected static String bodyText = "{\"username\":\"admin\",\"password\":\"passwort\",\"resource\":\"TestDB\"}";

  public static void main(String[] args) throws Exception {
    Util util = new Util();
    JSONObject obj = util.getToken(loginURL,bodyText);
    String jwt = obj.get("jwt").toString();
    MessageHandlerType messageHandlerType = MessageHandlerType.Sensor;
//    String usage = "CO2";
    WebSocketConnect webSocketConnect_1 = new WebSocketConnect();
    WebSocketConnect webSocketConnect_2 = new WebSocketConnect();
    WebSocketConnect webSocketConnect_3 = new WebSocketConnect();
    WebSocketConnect webSocketConnect_4 = new WebSocketConnect();
    WebSocketConnect webSocketConnect_5 = new WebSocketConnect();
    WebSocketConnect webSocketConnect_6 = new WebSocketConnect();
    webSocketConnect_1.connect(jwt,messageHandlerType,"LIGHT");
    webSocketConnect_2.connect(jwt,messageHandlerType,"CO2");
    webSocketConnect_3.connect(jwt,messageHandlerType,"MOTION");
    webSocketConnect_4.connect(jwt,messageHandlerType,"TEMPERATURE");
    webSocketConnect_5.connect(jwt,messageHandlerType,"PERCENT");
    webSocketConnect_6.connect(jwt,messageHandlerType,"ANGLE");

  }
}


