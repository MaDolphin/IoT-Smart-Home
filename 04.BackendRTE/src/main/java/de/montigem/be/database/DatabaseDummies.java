/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.database;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.sensor.Sensor;
import de.montigem.be.domain.cdmodelhwc.classes.sensortype.SensorType;
import de.montigem.be.domain.cdmodelhwc.classes.sensorvalue.SensorValue;
import de.montigem.be.util.DAOLib;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;

@Stateless
public class DatabaseDummies {
  @Inject
  private DAOLib daoLib;

  @Inject
  private SecurityHelper securityHelper;

  public boolean createDatabaseDummies() {
    Log.info("Creating DummyValues....", getClass().getName());

    // ...
    createSensors(securityHelper.getSessionCompliantResource());

    Log.info("DummyValues created.", getClass().getName());

    return true;
  }

  public void createSensors(String resource) {
    daoLib.getSensorDAO().create(
            new Sensor().rawInitAttrs(Collections.emptyList(), "SensorX25", SensorType.ANGLE, Arrays.asList(
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(5), 4),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(6), 7),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(7), 2),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(8), 4),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(9), 5),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(10), 5),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(11), 3),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(12), 4),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(13), 5),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(14), 7)
            )), resource);
    daoLib.getSensorDAO().create(
            new Sensor().rawInitAttrs(Collections.emptyList(), "SensorX26", SensorType.ANGLE, Arrays.asList(
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(7), 5),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(8), 3),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(9), 3),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(10), 2),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(11), 6),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(12), 7),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(13), 6),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(14), 3),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(15), 5),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(16), 7)
            )), resource);
  }
}
