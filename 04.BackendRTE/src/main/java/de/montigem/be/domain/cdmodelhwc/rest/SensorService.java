/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.domain.cdmodelhwc.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.montigem.be.domain.cdmodelhwc.classes.sensortype.SensorType;
import de.montigem.be.domain.cdmodelhwc.classes.sensorvalue.SensorValue;
import de.montigem.be.domain.cdmodelhwc.daos.SensorDAO;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.marshalling.JsonMarshal;
import de.montigem.be.util.DAOLib;
import de.montigem.be.util.Responses;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
  public Response getSensorValue(@QueryParam("sensorId") String sensorId) {
    SensorDAO dao = daoLib.getSensorDAO();
    if (dao.getListOfSensorValueById(sensorId) == null) {
      return Responses.okResponse("No Result");
    } else {
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
   * {"sensorId":"1", "type":"CO2", "value":88, "timeStamp":"2020-01-01 08:00:00"},
   * {"sensorId":"1", "type":"CO2", "value":89, "timeStamp":"2020-01-01 08:00:01"},
   * {"sensorId":"1", "type":"CO2", "value":90, "timeStamp":"2020-01-01 08:00:02"},
   * {"sensorId":"1", "type":"CO2", "value":91, "timeStamp":"2020-01-01 08:00:03"},
   * {"sensorId":"1", "type":"CO2", "value":92, "timeStamp":"2020-01-01 08:00:04"},
   * {"sensorId":"1", "type":"CO2", "value":93, "timeStamp":"2020-01-01 08:00:05"}
   * ]
   * }
   * @Param: [sensorId, type, value]
   * @return: javax.ws.rs.core.Response
   * @Author: Haikun
   * @Date: 2020/5/10
   */
  @POST
  @Path("/insertSensorValue")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response setSensorValue(String json) {
    String output = daoLib.getSensorDAO().parseSensorValue(json);
    if(!output.contains("Success")){
      if(output.equals("No SensorType")){
        return Responses.noContent();
      }
      return Responses.error(MontiGemErrorFactory.deserializeError(json), getClass());
    }
    return Responses.okResponse(output);
  }
}
