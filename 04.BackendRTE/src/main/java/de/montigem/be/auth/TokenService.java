/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.auth;

import de.montigem.be.auth.jwt.ExtendedJWT;
import de.montigem.be.auth.jwt.IRefreshTokenManager;
import de.montigem.be.auth.jwt.JWToken;
import de.montigem.be.auth.jwt.ShiroJWTFilter;
import de.montigem.be.auth.jwt.blacklist.BlackListManager;
import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.error.JsonException;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.marshalling.JsonMarshal;
import de.montigem.be.util.DAOLib;
import de.montigem.be.util.Responses;
import de.montigem.be.util.json.JsonBooleanValue;
import de.montigem.be.util.json.JsonStringValue;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * REST service for checking the validity of java web tokens (JWTs).
 *
 * @author (last commit) $Author$
 * @version $Date$<br>
 * $Revision$
 */
@Stateless
@Path("/auth/tokens")
public class TokenService {

  @Inject
  private DAOLib daoLib;

  @Inject
  private RolePermissionManager rolePermissionManager;

  @Inject
  private IRefreshTokenManager refreshTokenManager;

 @GET
  @Path("{token}/validity")
  public Response validateToken(@PathParam("token")
                                    String token) {
    try {
      JWToken jwt = new JWToken(token, ShiroJWTFilter.getUsernameFromToken(token),
          ShiroJWTFilter.getServerInstanceFromToken(token));
      Optional<DomainUser> user = daoLib.getDomainUserDAO().find(jwt.getUsername(), jwt.getResource());
      if (user.isPresent() && !BlackListManager.getBlacklist()
          .isTokenBlacklisted(jwt.getToken(), jwt.getResource())) {
        return Responses.okResponse(new JsonBooleanValue(true));
      }
      return Responses.okResponse(new JsonBooleanValue(false));
    } catch (Exception e) {
      return Responses.okResponse(new JsonBooleanValue(false));
    }
  }

  @POST
  @Path("{token}/refresh")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response refreshAccessToken(@PathParam("token")
                                         String token, String jsonRefreshToken) {
    try {
      String refreshToken = JsonMarshal.getInstance()
          .unmarshal(jsonRefreshToken, JsonStringValue.class).getValue();
      if (refreshToken != null) {
        String username = refreshTokenManager.retrieveUsernameForRefreshToken(refreshToken);

        // check refresh token validity
        if (username != null) {
          // invalidate refresh token and generate new refresh token
          //          refreshTokenManager.removeRefreshToken(refreshToken);
          //          String newRefreshToken = ShiroJWTFilter.generateRefreshToken();
          //          refreshTokenManager.registerRefreshToken(newRefreshToken, username);

          // retrieve new access token
          Instant date = new Date(new Date().getTime() + ShiroJWTFilter.VALIDITY).toInstant();
          ZonedDateTime expirationDate = ZonedDateTime.ofInstant(date, ZoneId.systemDefault());

          String resource = ShiroJWTFilter.getServerInstanceFromToken(token);

          String jwToken = ShiroJWTFilter
              .createTokenForUser(username, false, resource, daoLib, rolePermissionManager);
          // ExtendedJWT extjwt = new ExtendedJWT(jwToken, newRefreshToken, expirationDate);
          ExtendedJWT extjwt = new ExtendedJWT(jwToken, refreshToken, expirationDate);
          return Responses.okResponse(extjwt);
        }
      }
    } catch (JsonException e) {
      Log.warn(TokenService.class.getName() + " Token couldnt be refreshed", e);
      return Responses.error(MontiGemErrorFactory.deserializeError(jsonRefreshToken), getClass());
    }
    return Responses.error(MontiGemErrorFactory.unauthorized(), getClass());
  }
}
