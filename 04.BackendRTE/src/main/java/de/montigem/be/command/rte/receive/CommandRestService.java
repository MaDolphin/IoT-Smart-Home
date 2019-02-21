/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.command.rte.receive;

import de.montigem.be.command.response.CommandResponseDTO;
import de.montigem.be.command.rte.general.CommandManager;
import de.montigem.be.marshalling.JsonMarshal;
import de.montigem.be.util.APIExceptionInterceptor;
import de.montigem.be.util.Responses;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Service class for the class {} that contains all REST methods
 * for that class.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
@Stateless
@Path("/commands")
@Produces("application/json")
@Interceptors(APIExceptionInterceptor.class)
public class CommandRestService {

  @Inject
  private CommandManager commandManager;

  /////////////////////////////////////////////////////////////////////////////////
  // REST METHODS FOR THE CLASS
  /////////////////////////////////////////////////////////////////////////////////

  /**
   * Run all given commands
   *
   * @param cmds the commands as json
   * @return
   */
  @POST
  public Response runCommand(String cmds) {
    CommandResponseDTO response = this.commandManager.manage(cmds);
    String jsonResponse = JsonMarshal.getInstance().marshal(response);

    return Responses.okResponse(jsonResponse);
  }
}
