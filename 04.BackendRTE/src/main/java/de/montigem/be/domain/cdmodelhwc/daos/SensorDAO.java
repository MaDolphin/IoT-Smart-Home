/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.domain.cdmodelhwc.daos;

import de.montigem.be.domain.cdmodelhwc.classes.sensor.Sensor;
import de.montigem.be.domain.cdmodelhwc.classes.sensortype.SensorType;
import de.montigem.be.system.sensor.dtos.LineGraphEntryDTO;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class SensorDAO extends SensorDAOTOP {

  @Transactional
  public List<Sensor> getListOfSensorsForType(String resource, SensorType type) {
    router.setDataSource(resource);
    TypedQuery<Sensor> query = em.createQuery(
        "SELECT s FROM " + getDomainClass().getName() + " s WHERE s.type = :type", getDomainClass());
    query.setParameter("type", type);

    return query.getResultList();
  }

  @Transactional
  public List<Long> getListOfSensorIdsForType(String resource, SensorType type) {
    router.setDataSource(resource);
    TypedQuery<Long> query = em.createQuery(
        "SELECT s.id FROM " + getDomainClass().getName() + " s WHERE s.type = :type", Long.class);
    query.setParameter("type", type);

    return query.getResultList();
  }

  @Transactional
  public Optional<LineGraphEntryDTO> getValueInTimeById(String resource, ZonedDateTime currentTime, int seconds, long id) {
    router.setDataSource(resource);
    TypedQuery<LineGraphEntryDTO> query = em.createQuery(
        "SELECT new " + LineGraphEntryDTO.class.getName() + "(s.id, s.sensorId, v.value) FROM " + getDomainClass().getName() + " s JOIN s.value AS v "
            + "WHERE s.id = :id "
            + " AND v.timestamp >= :lastUpdate AND v.timestamp < :currentTime",
        LineGraphEntryDTO.class);
    query.setParameter("id", id);
    query.setParameter("lastUpdate", currentTime.minusSeconds(seconds));
    query.setParameter("currentTime", currentTime);
    query.setMaxResults(1);

    LineGraphEntryDTO singleResult;
    try {
      singleResult = query.getSingleResult();
    }
    catch (NoResultException e) {
      return Optional.empty();
    }
    return Optional.of(singleResult);
  }
}
