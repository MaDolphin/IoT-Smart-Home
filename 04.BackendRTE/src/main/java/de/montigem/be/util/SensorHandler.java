/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.util;

import de.montigem.be.domain.cdmodelhwc.classes.sensortype.SensorType;
import de.montigem.be.marshalling.JsonMarshal;
import de.montigem.be.service.WebSocketService;
import de.montigem.be.system.sensor.dtos.LineGraphDTO;
import de.montigem.be.system.sensor.dtos.LineGraphEntryDTO;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.websocket.Session;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * handle sensor refresh operations
 */
public class SensorHandler {
  private static SensorHandler INSTANCE;
  public static final int FREQUENCY_IN_SECONDS = 1;

  // Collection of all connected clients
  private Set<Pair<SensorType, Session>> sensors;

  public static SensorHandler getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new SensorHandler();
    }
    return INSTANCE;
  }

  public SensorHandler() {
    sensors = new HashSet<>();
  }

  public void add(Session session, SensorType type) {
    sensors.add(new ImmutablePair<>(type, session));
    Log.debug("Add new sensor type: " + type, getClass().getName());
  }

  public static void run(DAOLib daoLib, ZonedDateTime currentTime) {
    Set<Pair<SensorType, Session>> sensors = getInstance().sensors;
    sensors.removeIf(s -> !s.getRight().isOpen());

    for (Pair<SensorType, Session> sensor : sensors) {
      if (currentTime.getSecond() % getSecondsForSensorType(sensor.getLeft()) == 0) {
        sendToSensor(daoLib, WebSocketService.getJWT(sensor.getRight()).getResource(), currentTime, sensor);
      }
    }
  }

  private static void sendToSensor(DAOLib daoLib, String resource, ZonedDateTime currentTime, Pair<SensorType, Session> sensor) {
    List<LineGraphEntryDTO> values = daoLib.getSensorDAO().getListOfSensorIdsForType(resource, sensor.getLeft()).parallelStream()
        .map(sId -> daoLib.getSensorDAO().getValueInTimeById(resource, currentTime, getSecondsForSensorType(sensor.getLeft()), sId))
        .filter(Optional::isPresent).map(Optional::get)
        .collect(Collectors.toList());

    String dto = JsonMarshal.getInstance().marshal(new LineGraphDTO(0, values, currentTime));
    if (sensor.getRight().isOpen()) {
      sensor.getRight().getAsyncRemote().sendText(dto);
    }
  }

  public static int getSecondsForSensorType(SensorType type) {
    switch (type) {
      case TEMPERATURE:
        return 5;
      case PERCENT:
        return 3;
      case ANGLE:
        return 1;
      default:
        return 10;
    }
  }
}
