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
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(5), 4f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(6), 7f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(7), 2f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(8), 4f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(9), 5f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(10), 5f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(11), 3f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(12), 4f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(13), 5f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(14), 7f)
            )), resource);
    daoLib.getSensorDAO().create(
            new Sensor().rawInitAttrs(Collections.emptyList(), "SensorX26", SensorType.ANGLE, Arrays.asList(
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(7), 5f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(8), 3f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(9), 3f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(10), 2f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(11), 6f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(12), 7f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(13), 6f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(14), 3f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(15), 5f),
                    new SensorValue().rawInitAttrs(Collections.emptyList(), ZonedDateTime.now().plusSeconds(16), 7f)
            )), resource);
  }
}
