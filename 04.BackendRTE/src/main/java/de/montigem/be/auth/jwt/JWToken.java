/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.auth.jwt;

import org.apache.shiro.authc.AuthenticationToken;

public class JWToken implements AuthenticationToken {

  /**
   *
   */
  private static final long serialVersionUID = 287824435074973995L;

  private String token;

  private String username;

  private String resource;
  
  public JWToken(String token, String username, String resource) {
    this.token = token;
    this.username = username;
    this.resource = resource;
  }

  @Override
  public String getCredentials() {
    return token;
  }

  @Override
  public String getPrincipal() {
    return username;
  }

  public String getToken() {
    return token;
  }

  public String getUsername() {
    return username;
  }

  public String getResource() {
    return resource;
  }
}
