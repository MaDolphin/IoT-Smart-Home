/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.util;

import de.montigem.be.AbstractDomainTest;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.command.commands.Info_general;
import de.montigem.be.command.response.CommandResponseDTO;
import de.montigem.be.command.rte.general.CommandDTOList;
import de.montigem.be.database.DatabaseDummies;
import de.montigem.be.database.DatabaseReset;
import de.montigem.be.error.JsonException;
import de.montigem.be.marshalling.JsonMarshal;
import de.montigem.be.system.common.dtos.StringContainerDTO;
import de.montigem.be.util.websocket.WebSocketSessionManager;
import org.junit.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class WebSocketTest extends AbstractDomainTest {
  @Inject
  public SecurityHelper securityHelper;

  @Inject
  public DatabaseDummies databaseDummies;

  @Inject
  public DatabaseReset databaseReset;

  @Test
  public void connect() throws Exception {
    loginBootstrapUser();
    CountDownLatch latch = new CountDownLatch(2);
    WebSocketClientEndpoint ws = WebSocketClientEndpoint.connect(new URI("ws://localhost:8080/montigem-be/websocket/" + getJWT() + "/None/All"), session -> {
      session.sendMessage("test");
      session.sendMessage("test");
    }, msg -> {
      assertEquals("test", msg);
      latch.countDown();

      return msg;
    });

    if (!latch.await(1, TimeUnit.SECONDS)) {
      fail("should get message in time");
    }
  }

  @Test
  public void commandManager() throws Exception {
    loginBootstrapUser();
    CountDownLatch latch = new CountDownLatch(1);
    WebSocketClientEndpoint ws = WebSocketClientEndpoint.connect(new URI("ws://localhost:8080/montigem-be/websocket/" + getJWT() + "/CommandManager/All"), session -> {
      CommandDTOList commandDTOList = new CommandDTOList(Collections.singletonList(new Info_general()));
      session.sendMessage(JsonMarshal.getInstance().marshal(commandDTOList));
    }, msg -> {
      try {
        CommandResponseDTO responseDTO = JsonMarshal.getInstance().unmarshal(msg, CommandResponseDTO.class);
        assertTrue(responseDTO.getResults().get(0).getDto() instanceof StringContainerDTO);
        latch.countDown();
      }
      catch (JsonException e) {
        fail(e.toString());
      }

      return msg;
    });

    if (!latch.await(1, TimeUnit.SECONDS)) {
      fail("should get message in time");
    }
  }

  @Test
  public void sendTest() throws Exception {
    loginBootstrapUser();

    CountDownLatch latch = new CountDownLatch(2);

    WebSocketClientEndpoint ws = WebSocketClientEndpoint.connect(new URI("ws://localhost:8080/montigem-be/websocket/" + getJWT() + "/None/Info,Update"), session -> {
      WebSocketSessionManager.getInstance().sendToSessions(securityHelper.getSessionCompliantResource(), "Info", "info");
      WebSocketSessionManager.getInstance().sendToSessions(securityHelper.getSessionCompliantResource(), "Restart", "restart");
      WebSocketSessionManager.getInstance().sendToSessions(securityHelper.getSessionCompliantResource(), "Update", "update");
    }, msg -> {
      switch (msg) {
        case "info":
        case "update":
          latch.countDown();
          break;
        default:
          fail("should only get info and update");
      }

      return msg;
    });

    if (!latch.await(1, TimeUnit.SECONDS)) {
      fail("should get message in time");
    }
  }

  @Test
  public void sendTest2() throws Exception {
    loginBootstrapUser();

    CountDownLatch latch = new CountDownLatch(3);

    WebSocketClientEndpoint ws = WebSocketClientEndpoint.connect(new URI("ws://localhost:8080/montigem-be/websocket/" + getJWT() + "/None/All"), session -> {
      WebSocketSessionManager.getInstance().sendToSessions(securityHelper.getSessionCompliantResource(), "Info", "info");
      WebSocketSessionManager.getInstance().sendToSessions(securityHelper.getSessionCompliantResource(), "Restart", "restart");
      WebSocketSessionManager.getInstance().sendToSessions(securityHelper.getSessionCompliantResource(), "Update", "update");
    }, msg -> {
      latch.countDown();

      return msg;
    });

    if (!latch.await(1, TimeUnit.SECONDS)) {
      fail("should get message in time");
    }
  }

  @Test
  public void sensorTest() throws Exception {
    loginBootstrapUser();
    databaseDummies.createDatabaseDummies();

    CountDownLatch latch = new CountDownLatch(10);

    WebSocketClientEndpoint ws = WebSocketClientEndpoint.connect(new URI("ws://localhost:8080/montigem-be/websocket/" + getJWT() + "/Sensor/ANGLE"), session -> {
        },
        msg -> {
          latch.countDown();

          return msg;
        });

    if (!latch.await(1000, TimeUnit.SECONDS)) {
      databaseReset.removeDatabaseEntries(false);
      fail("should get message in time");
    }
  }
}
