/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.domain.cdmodelhwc.rest;

import com.google.common.base.Throwables;
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

/**
 * An example of a REST class with a REST method. This class is the only REST class that does not
 * require any security-related configuration
 *
 */
@Stateless
@Path("/general/sensor")
@Produces("application/json")
public class SensorService {

  @Inject
  private DAOLib daoLib;

  /**
  * @Description: retrieving Sensor-Values
   * http://localhost:8080/montigem-be/api/general/sensor/querySensorValue?sensorId=000021
  * @Param: [sensorId]
  * @return: javax.ws.rs.core.Response
  * @Author: Haikun
  * @Date: 2020/5/10
  */
  @GET
  @Path("/querySensorValue")
  public Response getSensorValue(@QueryParam("sensorId") String sensorId){
    SensorDAO dao = daoLib.getSensorDAO();
    List<SensorValue> sensorList = dao.getListOfSensorValueById(sensorId);
    String jsonResponse = JsonMarshal.getInstance().marshal(sensorList);
    return Responses.okResponse(jsonResponse);
  }


  /**
  * @Description: insert Sensor-Value
   * http://localhost:8080/montigem-be/api/general/sensor/insertSensorValue?sensorId=000021&type=CO2&value=90
  * @Param: [sensorId, type, value]
  * @return: javax.ws.rs.core.Response
  * @Author: Haikun
  * @Date: 2020/5/10
  */
  @GET
  @Path("/insertSensorValue")
//  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response setSensorValue(
          @QueryParam("sensorId") String sensorId,
          @QueryParam("type") String type,
          @QueryParam("value") int value){

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
  }

//  @POST
//  @Path("/testPost")
//  @Consumes("application/json")
//  public Response testPost(@FormParam("formData") String f){
//    String output = "POST : " + f;
//    return Responses.okResponse(output);
//  }

}
