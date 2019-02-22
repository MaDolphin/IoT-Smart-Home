package de.montigem.be.auth;

import de.montigem.be.auth.jwt.PrincipalWrapper;
import de.montigem.be.auth.jwt.ShiroJWTFilter;
import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.database.DatabaseDataSourceUtil;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.DAOLib;
import de.montigem.be.util.Responses;
import de.se_rwth.commons.logging.Log;
import org.apache.shiro.SecurityUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class ForgotPwdService {

  @Inject
  private DAOLib daoLib;

  @Inject
  private RolePermissionManager rolePermissionManager;

  @Inject
  private UserActivationManager activationManager;

  @Inject
  private UserActivationDAO activationDAO;

  /**
   * This endpoint gets called when the user clicks the "forgot password" link in the frontend. It will send an email
   * with a reset password link to the corresponding user.
   *
   * @return
   */
  @POST
  @Path("/forgotpwd/users/{email}/{resource}")
  public Response forgotPwd(@PathParam("email") String email,
      @PathParam("resource") String resource) {
    Optional<DomainUser> user = daoLib.getDomainUserDAO().findByEmail(email, resource);

    if (!user.isPresent()) {
      return Responses.error(MontiGemErrorFactory.loadIDError(email), getClass());
    }

    // send mail to user
    try {
      String dbBezeichner = DatabaseDataSourceUtil.getDatenbankBezeichner(resource);
      activationManager
          .sendForgotPwdMail(user.get().getEmail(), user.get().getUsername(), resource,
              dbBezeichner);
      return Responses.okResponse();
    } catch (MessagingException e) {
      return Responses.error(MontiGemErrorFactory.exceptionCaught(e), getClass());
    }
  }

  /**
   * This endpoint gets called by the frontend after the user clicks the reset password link that he received in an
   * email. It will produce a temporary token which can only be used to change the user's password.
   *
   * @param idString The id contained in the reset password link
   * @param key      The key contained in the reset password link
   * @return
   */
  @POST
  @Path("/forgotpwd/temptoken/{id}/{key}/{resource}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response resetPwd(@PathParam("id") String idString, @PathParam("key") String key,
      @PathParam("resource") String resource) {
    UUID id = UUID.fromString(idString);
    UserActivation activation = activationDAO.checkActivationKey(id, key, resource);

    if (activation != null) {
      String jwt = ShiroJWTFilter
          .createTokenForUser(activation.getUser().getUsername(), true,
              resource, daoLib, rolePermissionManager);
      return Responses.okResponseWithJWT(jwt);
    } else {
      Log.info("Invalid activation key: " + key, getClass().getName());
      return Responses.error(MontiGemErrorFactory.resolverError(null), getClass());
    }
  }

  private PrincipalWrapper getPrincipalWrapper() {
    return (PrincipalWrapper) SecurityUtils.getSubject().getPrincipal();
  }
}
