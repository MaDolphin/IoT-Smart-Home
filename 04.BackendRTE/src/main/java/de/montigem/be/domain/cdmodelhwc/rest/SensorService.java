/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.domain.cdmodelhwc.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.montigem.be.domain.cdmodelhwc.classes.sensor.Sensor;
import de.montigem.be.domain.cdmodelhwc.classes.sensortype.SensorType;
import de.montigem.be.domain.cdmodelhwc.classes.sensorvalue.SensorValue;
import de.montigem.be.domain.cdmodelhwc.daos.SensorDAO;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.marshalling.JsonMarshal;
import de.montigem.be.util.DAOLib;
import de.montigem.be.util.Responses;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * An example of a REST class with a REST method. This class is the only REST class that does not
 * require any security-related configuration
 *
 */
@Stateless
@Path("/domain/receive-json")
@Produces("application/json")
public class SensorService {

  @Inject
  private DAOLib daoLib;

  /**
   * @Description: retrieving Sensor-Values
   * GET http://localhost:8080/montigem-be/api/domain/receive-json/querySensorValue?sensorId=000021
   * @Param: [sensorId]
   * @return: javax.ws.rs.core.Response
   * @Author: Haikun
   * @Date: 2020/5/10
   */
  @GET
  @Path("/querySensorValue")
  public Response getSensorValue(@QueryParam("sensorId") String sensorId){
    SensorDAO dao = daoLib.getSensorDAO();
    if(dao.getListOfSensorValueById(sensorId) == null){
      return Responses.okResponse("No Result");
    }else {
      List<SensorValue> sensorList = dao.getListOfSensorValueById(sensorId);
      String jsonResponse = JsonMarshal.getInstance().marshal(sensorList);
      return Responses.okResponse(jsonResponse);
    }

  }


  /**
   * @Description: insert Sensor-Value
   * POST http://localhost:8080/montigem-be/api/domain/receive-json/insertSensorValue
   * Body - raw - Json type - Example:
   * { "data": [
   *    {"sensorId":"1", "type":"CO2", "value":88, "timeStamp":"2020-01-01 08:00:00"},
   *    {"sensorId":"1", "type":"CO2", "value":89, "timeStamp":"2020-01-01 08:00:01"},
   *    {"sensorId":"1", "type":"CO2", "value":90, "timeStamp":"2020-01-01 08:00:02"},
   *    {"sensorId":"1", "type":"CO2", "value":91, "timeStamp":"2020-01-01 08:00:03"},
   *    {"sensorId":"1", "type":"CO2", "value":92, "timeStamp":"2020-01-01 08:00:04"},
   *    {"sensorId":"1", "type":"CO2", "value":93, "timeStamp":"2020-01-01 08:00:05"}
   *   ]
   * }
   * @Param: [sensorId, type, value]
   * @return: javax.ws.rs.core.Response
   * @Author: Haikun
   * @Date: 2020/5/10
   */
  @POST
  @Path("/insertSensorValue")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response setSensorValue(String json){
    try {

      JsonElement element = new Gson().fromJson(json, JsonElement.class);
      JsonArray dataArray = element.getAsJsonObject().get("data").getAsJsonArray();
      ArrayList<ImmutableTriple<String, SensorType, SensorValue>> sensorData = new ArrayList<>();
      for(JsonElement d : dataArray){
        String sensorId = d.getAsJsonObject().get("sensorId").getAsString();
        String type = d.getAsJsonObject().get("type").getAsString();
        int value = d.getAsJsonObject().get("value").getAsInt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        ZonedDateTime timeStamp = ZonedDateTime.parse(d.getAsJsonObject().get("timeStamp").getAsString() + " UTC", formatter);
        SensorType sensorType;
        switch (type){
          case "TEMPERATURE": sensorType = SensorType.TEMPERATURE; break;
          case "ANGLE": sensorType = SensorType.ANGLE; break;
          case "PERCENT": sensorType = SensorType.PERCENT; break;
          case "LIGHT": sensorType = SensorType.LIGHT; break;
          case "CO2": sensorType = SensorType.CO2; break;
          case "MOTION": sensorType = SensorType.MOTION; break;
          default:
            return Responses.noContent();
        }
        SensorValue sensorValue = new SensorValue();
        sensorValue.rawInitAttrs(null, timeStamp, value);

        sensorData.add(new ImmutableTriple<>(sensorId, sensorType,sensorValue));
      }

      SensorDAO dao = daoLib.getSensorDAO();
      StringBuilder output = new StringBuilder();
      output.append("Successfully inserted following values into the database:");
      for(ImmutableTriple<String, SensorType, SensorValue> d : sensorData){
        dao.setSensorValue(d.getLeft(), d.getMiddle(), d.getRight());
        output.append(d.getLeft()).append("-").append(d.getMiddle()).append("-").append(d.getRight().getValue()).append("-").append(d.getRight().getTimestamp()).append(";");
      }


      return Responses.okResponse(output.toString());
    } catch (JsonParseException e) {
      Log.warn("SensorService: incoming json is not parseable");
      Log.trace(json, "SensorService");
    }
    return Responses.error(MontiGemErrorFactory.deserializeError(json), getClass());
  }

}
