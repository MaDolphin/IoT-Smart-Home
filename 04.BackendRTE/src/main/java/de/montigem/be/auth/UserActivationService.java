/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.auth;

import de.montigem.be.auth.jwt.ShiroJWTFilter;
import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.DAOLib;
import de.montigem.be.util.Responses;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class UserActivationService {

  @Inject
  private DAOLib daoLib;

  @Inject
  private RolePermissionManager rolePermissionManager;

  @Inject
  private UserActivationManager activationManager;

  /**
   * This endpoint gets called by the frontend when the user clicks the activation link that he received prior via
   * email. It will produce a temporary token which can be used only to set the user's password.
   *
   * @param idString
   * @param activationKey
   * @return
   */
  @POST
  @Path("/activations/{id}/{key}/{resource}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response activateAccount(@PathParam("id") String idString,
      @PathParam("key") String activationKey,
      @PathParam("resource") String resource) {
    UUID id = UUID.fromString(idString);
    Optional<DomainUser> user = activationManager.activateAccount(id, activationKey, resource);
    if (user.isPresent()) {
      Log.info("Account successfully activated!", getClass().getName());
      String jwt = ShiroJWTFilter.createTokenForUser(user.get().getUsername(), true, resource, daoLib, rolePermissionManager);
      return Responses.okResponseWithJWT(jwt);
    }
    else {
      Log.info("Invalid activation key: " + activationKey, getClass().getName());
      return Responses.error(MontiGemErrorFactory.resolverError(null), getClass());
    }
  }
}
