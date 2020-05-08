/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.domain.cdmodelhwc.rest;

import com.google.common.base.Throwables;
import de.montigem.be.config.Config;
import de.montigem.be.domain.cdmodelhwc.classes.sensortype.SensorType;
import de.montigem.be.domain.cdmodelhwc.classes.sensorvalue.SensorValue;
import de.montigem.be.domain.cdmodelhwc.daos.SensorDAO;
import de.montigem.be.error.MontiGemErrorFactory;
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

  @GET
  @Path("/{sensorId}")
  public Response getSensorValue(@PathParam("sensorId") String sensorId){
    String output = "SUCCESS INSERT : " + sensorId;
    return Responses.okResponse(output);
  }

//  http://localhost:8080/montigem-be/api/general/sensor/000021/CO2/90
  @GET
  @Path("/{sensorId}/{type}/{value}")
  public Response setSensorValue(
          @PathParam("sensorId") String sensorId,
          @PathParam("type") String type,
          @PathParam("value") int value){

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

}
