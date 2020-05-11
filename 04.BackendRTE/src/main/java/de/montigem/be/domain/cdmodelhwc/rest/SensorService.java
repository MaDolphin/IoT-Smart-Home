/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.domain.cdmodelhwc.rest;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.montigem.be.config.Config;
import de.montigem.be.domain.cdmodelhwc.classes.sensor.Sensor;
import de.montigem.be.domain.cdmodelhwc.classes.sensortype.SensorType;
import de.montigem.be.domain.cdmodelhwc.classes.sensorvalue.SensorValue;
import de.montigem.be.domain.cdmodelhwc.daos.SensorDAO;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.marshalling.JsonMarshal;
import de.montigem.be.util.DAOLib;
import de.montigem.be.util.Responses;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
   * Body - raw - Json type : {"sensorId":"000021","type":"CO2","value":90}
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
      String sensorId = element.getAsJsonObject().get("sensorId").getAsString();
      String type = element.getAsJsonObject().get("type").getAsString();
      int value = element.getAsJsonObject().get("value").getAsInt();

      type = type.toUpperCase();

      SensorType sensorType;
      SensorDAO dao = daoLib.getSensorDAO();
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
      dao.setSensorValue(sensorId,sensorType,value);
      String output = "SUCCESS INSERT : " + sensorId + "-" + type + "-" + value;
      return Responses.okResponse(output);
    } catch (JsonParseException e) {
      Log.warn("SensorService: incoming json is not parseable");
      Log.trace(json, "SensorService");
    }
    return Responses.error(MontiGemErrorFactory.deserializeError(json), getClass());
  }

}
