/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be;

import com.google.common.base.Throwables;
import de.montigem.be.config.Config;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.Responses;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * An example of a REST class with a REST method. This class is the only REST class that does not
 * require any security-related configuration
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
@Stateless
@Path("/general")
@Produces("application/json")
public class GeneralService {

  @GET
  @Path("info")
  public Response getInfo() {
    String info = "MontiGem V" + Config.VERSION + ", " + Config.TIMESTAMP + ", Commit " + Config.COMMIT +", " + Config.COMMIT_TIME +", " +Config.BRANCH;
    try {
      return Responses.okResponse(info);
    } catch (Exception e) {
      Log.debug(Throwables.getStackTraceAsString(e), getClass().getName());
      return Responses.error(MontiGemErrorFactory.exceptionCaught(e), getClass());
    }
  }

  @GET
  @Path("version")
  public Response getVersion() {
    String version = "Version: " + Config.VERSION;
    try {
      return Responses.okResponse(version);
    } catch (Exception e) {
      Log.debug(Throwables.getStackTraceAsString(e), getClass().getName());
      return Responses.error(MontiGemErrorFactory.exceptionCaught(e), getClass());
    }
  }

  @GET
  @Path("buildTime")
  public Response getBuildTime() {
    String buildTime = "BuildTime: " + Config.TIMESTAMP;
    try {
      return Responses.okResponse(buildTime);
    } catch (Exception e) {
      Log.debug(Throwables.getStackTraceAsString(e), getClass().getName());
      return Responses.error(MontiGemErrorFactory.exceptionCaught(e), getClass());
    }
  }
}
