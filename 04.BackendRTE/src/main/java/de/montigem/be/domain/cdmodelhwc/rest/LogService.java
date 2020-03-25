/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.domain.cdmodelhwc.rest;


import com.google.gson.*;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.APIExceptionInterceptor;
import de.montigem.be.util.Responses;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Service class for the class {@link Log} that contains all REST methods for
 * that class.
 *
 */
@Stateless
@Path("/logger")
@Produces(MediaType.APPLICATION_JSON)
@Interceptors(APIExceptionInterceptor.class)
public class LogService {
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/log")
  public Response log(String json) {
    try {
      JsonArray jobjs = new Gson().fromJson(json, JsonArray.class);

      for (JsonElement elem : jobjs) {
        JsonObject object = elem.getAsJsonObject();

        String logLevel = "INFO";
        JsonElement logLevelElement = object.get("logname");
        if (logLevelElement != null) {
          logLevel = logLevelElement.getAsString();
        }

        String timeStamp = "";
        JsonElement logTimeStamp = object.get("timeStamp");
        if (logTimeStamp != null) {
          Date timeStampDate = new Date();
          try {
            timeStampDate.setTime(logTimeStamp.getAsLong());

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            timeStamp = "{" + formatter.format(timeStampDate) + "} ";
          } catch (JsonParseException e) {
            Log.trace("LogService: cannot parse timeStamp" + logTimeStamp.toString(), "LogService");
          }
        }

        String logName = "";
        JsonElement logNameElement = object.get("className");
        if (logNameElement != null) {
          logName = logNameElement.getAsString();
        }

        String msg;
        JsonElement logMsgElement = object.get("msg");
        if (logMsgElement != null) {
          msg = logMsgElement.getAsString();
        }
        else {
          return Responses.okResponse();
        }

        switch (logLevel) {
          case "TRACE":
            Log.trace(timeStamp + msg, logName);
            break;
          case "DEBUG":
            Log.debug(timeStamp + msg, logName);
            break;
          case "WARN":
            Log.warn(timeStamp + logName + " " + msg);
            break;
          case "ERROR":
            Log.error(timeStamp + logName + " " + msg);
            break;
          case "INFO":
          default:
            Log.info(timeStamp + msg, logName);
            break;
        }
      }

      return Responses.okResponse();
    } catch (JsonParseException e) {
      Log.warn("LogService: incoming json is not parseable");
      Log.trace(json, "LogService");
    }

    return Responses.error(MontiGemErrorFactory.deserializeError(json), getClass());
  }
}
