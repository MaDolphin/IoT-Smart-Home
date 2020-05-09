/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.domain.cdmodelhwc.daos;

import de.montigem.be.domain.cdmodelhwc.classes.sensor.Sensor;
import de.montigem.be.domain.cdmodelhwc.classes.sensortype.SensorType;
import de.montigem.be.domain.cdmodelhwc.classes.sensorvalue.SensorValue;
import de.montigem.be.system.sensor.dtos.LineGraphEntryDTO;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
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
        } catch (NoResultException e) {
            return Optional.empty();
        }
        return Optional.of(singleResult);
    }

    /**
     * @Description: insert Seneor into DB
     * @Param: [sensorId, type]
     * @return: void
     * @Author: Haikun
     * @Date: 2020/5/8
     */
    @Transactional
    public void setSensor(String sensorId, SensorType type) {
        EntityManager em = getEntityManager();
        Sensor sensor = new Sensor();
        sensor.setSensorId(sensorId);
        sensor.setType(type);
        em.persist(sensor);
    }

    /**
     * @Description: find Sensor by SensorId
     * @Param: [sensorId]
     * @return: de.montigem.be.domain.cdmodelhwc.classes.sensor.Sensor
     * @Author: Haikun
     * @Date: 2020/5/8
     */
    @Transactional
    public Sensor getSensorById(String sensorId) {
        Sensor sensor = null;
        try {
            TypedQuery<Sensor> query = em.createQuery(
                    "SELECT s FROM " + getDomainClass().getName() + " s WHERE s.sensorId = :sensorId", getDomainClass());
            query.setParameter("sensorId", sensorId);
            sensor = query.getSingleResult();
        } catch (NoResultException nre) {

        } finally {
            return sensor;
        }
    }

    /**
     * @Description: insert SensorValue into DB. When the SensorId is not in the DB, then create a new Sensor this id is SensorId
     * @Param: [sensorId, type, value]
     * @return: void
     * @Author: Haikun
     * @Date: 2020/5/8
     */
    @Transactional
    public void setSensorValue(String sensorId, SensorType type, int value) {

        EntityManager em = getEntityManager();
//    this.setSensor("000001",SensorType.LIGHT);
        SensorValue sensorValue = new SensorValue();
        sensorValue.setValue(value);
        sensorValue.setTimestamp(ZonedDateTime.now());

        Sensor sensor;
        if (this.getSensorById(sensorId) == null) {  // if the Sensor is not in the DB, then create a new Sensor.
            this.setSensor(sensorId, type);
            sensor = this.getSensorById(sensorId);
        } else {
            sensor = this.getSensorById(sensorId);
        }

        List<SensorValue> sensorValueList = null;
        try {
            sensorValueList = sensor.getValues();
        } catch (NullPointerException e) {
        }

        if (sensorValueList == null){
            sensorValueList = new ArrayList<>();
            sensorValueList.add(sensorValue);
            sensor.setValues(sensorValueList);
        }else {
            sensor.addValue(sensorValue);
        }

        em.persist(sensor);
    }

    /**
    * @Description: find all Sensor-Values by given SensorId
    * @Param: [sensorId]
    * @return: java.util.List<de.montigem.be.domain.cdmodelhwc.classes.sensorvalue.SensorValue>
    * @Author: Haikun
    * @Date: 2020/5/10
    */
    @Transactional
    public List<SensorValue> getListOfSensorValueById(String sensorId) {
        Sensor sensor;
        if (this.getSensorById(sensorId) == null) {  // if the Sensor is not in the DB, then create a new Sensor.
            return null;
        } else {
            sensor = this.getSensorById(sensorId);
        }
        return sensor.getValues();
    }

}
