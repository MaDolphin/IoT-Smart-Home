/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be;

import de.montigem.be.database.DatabaseDummies;
import de.montigem.be.database.DatabaseReset;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.Responses;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * This class is for demonstration purposes and should be removed/ not used in production. It clears
 * the entire database. If the domain model is extended, new DAO classes yhould be added here.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
@Stateless
@Path("/develop")
@Produces("application/json")
public class ResetService {

  @Inject
  private DatabaseReset databaseReset;

  @Inject
  private DatabaseDummies databaseDummies;

  @GET
  @Path("resetDB")
  public Response resetDB() {
    databaseReset.removeDatabaseEntries(false);
    return Responses.okResponse();
  }

  @GET
  @Path("resetDBwithUser")
  public Response resetDBwithUser() {
    databaseReset.removeDatabaseEntries(true);
    return Responses.okResponse();
  }

  @POST
  @Path("createDummy")
  public Response createDummy() {
    if (databaseDummies.createDatabaseDummies()) {
      return Responses.okResponse();
    }
    return Responses.error(MontiGemErrorFactory.validationError("createDummy"), getClass());
  }

}
