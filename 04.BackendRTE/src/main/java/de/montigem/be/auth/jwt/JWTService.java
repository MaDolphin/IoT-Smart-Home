/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.auth.jwt;

import javax.ejb.Stateless;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * This class simply provides login/logout endpoints. Functionality is implemented in {@link ShiroJWTFilter}.
 */
@Stateless
@Path("/auth")
public class JWTService {

  @POST
  @Path("/login")
  public void login() {

  }

  @POST
  @Path("/logout")
  public void logout() {

  }
}
