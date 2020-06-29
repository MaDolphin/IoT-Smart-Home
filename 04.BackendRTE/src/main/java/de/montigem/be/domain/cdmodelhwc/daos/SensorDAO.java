/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.domain.cdmodelhwc.daos;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.montigem.be.domain.cdmodelhwc.classes.sensor.Sensor;
import de.montigem.be.domain.cdmodelhwc.classes.sensortype.SensorType;
import de.montigem.be.domain.cdmodelhwc.classes.sensorunit.SensorUnit;
import de.montigem.be.domain.cdmodelhwc.classes.sensorvalue.SensorValue;
import de.montigem.be.domain.cdmodelhwc.rest.SensorService;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.system.sensor.dtos.GaugeChartDataEntryDTO;
import de.montigem.be.system.sensor.dtos.LineGraphEntryDTO;
import de.montigem.be.util.DAOLib;
import de.montigem.be.util.Responses;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.openejb.SystemException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.montigem.be.util.tuple.*;

public class SensorDAO extends SensorDAOTOP {

    @Inject
    private DAOLib daoLib;

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

    @Transactional
    public Optional<GaugeChartDataEntryDTO> getValueInTimeById_GaugeChart(String resource, ZonedDateTime currentTime, int seconds, long id) {
        router.setDataSource(resource);
        TypedQuery<GaugeChartDataEntryDTO> query = em.createQuery(
                "SELECT new " + GaugeChartDataEntryDTO.class.getName() + "(s.id, s.sensorId, v.value) FROM " + getDomainClass().getName() + " s JOIN s.value AS v "
                        + "WHERE s.id = :id "
                        + " AND v.timestamp >= :lastUpdate AND v.timestamp < :currentTime",
                GaugeChartDataEntryDTO.class);
        query.setParameter("id", id);
        query.setParameter("lastUpdate", currentTime.minusSeconds(seconds));
        query.setParameter("currentTime", currentTime);
        query.setMaxResults(1);

        GaugeChartDataEntryDTO singleResult;
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
    public void setSensor(String sensorId, SensorType type, SensorUnit unit) {
        EntityManager em = getEntityManager();
        Sensor sensor = new Sensor();
        sensor.setSensorId(sensorId);
        sensor.setType(type);
        sensor.setUnit(unit);
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

        }
        return sensor;
    }

    /**
     * @Description: insert SensorValue into DB. When the SensorId is not in the DB, then create a new Sensor this id is SensorId
     * @Param: [sensorId, type, value]
     * @return: void
     * @Author: Haikun
     * @Date: 2020/5/8
     */
    @Transactional
    public void setSensorValue(String sensorId, SensorType type, SensorUnit unit, SensorValue sensorValue) {

        EntityManager em = getEntityManager();

        Sensor sensor;

        if (this.getSensorById(sensorId) == null) {  // if the Sensor is not in the DB, then create a new Sensor.
            this.setSensor(sensorId, type, unit);
        }
        sensor = this.getSensorById(sensorId);

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
    @Transactional
    public String parseSensorValue(String json){
        try {
            JsonElement element = new Gson().fromJson(json, JsonElement.class);
            JsonArray dataArray = element.getAsJsonObject().get("data").getAsJsonArray();
            ArrayList<FourTuple<String, SensorType, SensorUnit, SensorValue>> sensorData = new ArrayList<>();
            for(JsonElement d : dataArray){
                String sensorId = d.getAsJsonObject().get("sensorId").getAsString();
                String type = d.getAsJsonObject().get("type").getAsString();
                float value = d.getAsJsonObject().get("value").getAsFloat();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
                ZonedDateTime timeStamp = ZonedDateTime.parse(d.getAsJsonObject().get("timeStamp").getAsString() + " UTC", formatter);
                SensorType sensorType;
                SensorUnit sensorUnit;
                switch (type){
                    case "TEMPERATURE": sensorType = SensorType.TEMPERATURE;
                                        sensorUnit = SensorUnit.CELCIUS; break;
                    case "ANGLE": sensorType = SensorType.ANGLE;
                                  sensorUnit = SensorUnit.NONE; break;
                    case "PERCENT": sensorType = SensorType.PERCENT;
                                    sensorUnit = SensorUnit.NONE; break;
                    case "LIGHT": sensorType = SensorType.LIGHT;
                                  sensorUnit = SensorUnit.NONE; break;
                    case "CO2": sensorType = SensorType.CO2;
                                sensorUnit = SensorUnit.NONE; break;
                    case "MOTION": sensorType = SensorType.MOTION;
                                   sensorUnit = SensorUnit.NONE; break;
                    default:
                        return "No SensorType";
                }
                SensorValue sensorValue = new SensorValue();
                sensorValue.rawInitAttrs(null, timeStamp, value);

                sensorData.add(FourTuple.of(sensorId, sensorType, sensorUnit,sensorValue));
            }
            StringBuilder output = new StringBuilder();
            output.append("Successfully inserted following values into the database: ");
            for(FourTuple<String, SensorType, SensorUnit, SensorValue> d : sensorData){
                setSensorValue(d.getFirst(), d.getSecond(), d.getThird(), d.getFourth());
                output.append(d.getFirst()).append("-").append(d.getSecond()).append("-").append(d.getThird()).append("-").append(d.getFourth().getValue()).append("-").append(d.getFourth().getTimestamp()).append(";");
            }
            return output.toString();
        } catch (Exception e) {
            Log.warn("SensorService: incoming json is not parseable");
            Log.trace(json, "SensorService");
        }
        return MontiGemErrorFactory.deserializeError(json).toString();
    }
}
