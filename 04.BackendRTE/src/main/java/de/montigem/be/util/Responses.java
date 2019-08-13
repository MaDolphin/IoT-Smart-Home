/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.util;

import de.montigem.be.auth.jwt.ShiroJWTFilter;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.marshalling.JsonMarshal;
import de.se_rwth.commons.logging.Log;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This class provides methods to build some default responses as return types of REST interface
 * methods. These responses include both successful responses as errors.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class Responses {

  protected Responses() {
    // intentionally left empty
  }

  public static Response okResponse() {
    return Response.ok()
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
            .build();
  }

  public static Response okResponseWithJWT(String jwt) {
    return Response.ok()
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
            .header(ShiroJWTFilter.AUTH_HEADER, jwt)
            .build();
  }

  public static Response okResponse(Object o) {
    return okResponseJson(JsonMarshal.getInstance().marshal(o));
  }

  public static Response okResponseJson(String json) {
    return Response.ok(json)
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
            .build();
  }

  public static Response noContent() {
    return Response.noContent()
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
            .build();
  }

  public static Response userError(MontiGemError error, Class clazz) {
    Log.warn(clazz.getName() + " " + error.toString(), error);
    return Response.status(error.getHttpStatusCode()).entity(error.toJsonString())
            .type(MediaType.TEXT_PLAIN)
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
            .build();
  }

  public static Response error(MontiGemError error, Class clazz) {
    return errorInternal(error, clazz);
  }

  // Never returns a response but throws a WebApplicationException
  protected static Response errorInternal(MontiGemError error, Class clazz) {
    Log.error(clazz.getName() + ":" + error.toString(), error);
    System.out.println(clazz.getName() + " " + error.toJsonString() + "; " + error);
    throw new WebApplicationException(
            Response.status(error.getHttpStatusCode()).entity(error.toJsonString())
                    .type(MediaType.TEXT_PLAIN)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                    .build());
  }

  // Never returns a response but the exeption from errorInteral(MontiGemError) will be thrown
  protected static Response errorInternal(Exception ex) {
    return errorInternal(MontiGemErrorFactory.exceptionCaught(ex), Responses.class);
  }

}
