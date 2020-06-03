package websocket;

import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public class WebSocketConnect {

  public void connect(String jwt,MessageHandlerType messageHandlerType,String usage){
    try{
      String websocketURL = "ws://localhost:8080/montigem-be/websocket/"+jwt+"/"+messageHandlerType.toString()+"/"+usage;
      CountDownLatch latch = new CountDownLatch(2);
      WebSocketClientEndpoint ws = WebSocketClientEndpoint.connect(new URI(websocketURL), session -> {

      }, msg -> {
        switch(messageHandlerType){
          case Sensor:
            if (msg.contains("\"entries\":[]")) {
              System.out.print("No recent updates - Last checked: " + msg.substring(msg.indexOf("timestamp\":\"") + 12, msg.indexOf("\",\"typeName")) + "\r");
            } else {
              System.out.println(msg);
            }
            break;
          case Add:
          case None:

          default:
            System.out.println(msg);
            break;
        }
        latch.countDown();
        return msg;
      });

      if(messageHandlerType == MessageHandlerType.Add) {
        System.out.println("Send client");
        System.out.println("To send values to backend press any key");

        while(true) {
          System.out.println("Enter JSONArray of values as JSONObject in one line: \n");
          Scanner in = new Scanner(System.in);
          String input = in.nextLine();
          ws.sendMessage(input);
          Thread.sleep(1000);
        }
      }
      System.out.println("Update client");
      Thread.sleep(1000);
      if (!latch.await(100, TimeUnit.SECONDS)) {
        fail("should get message in time");
      }

    }catch (Exception e){
      System.out.println(e);
    }

  }
}
