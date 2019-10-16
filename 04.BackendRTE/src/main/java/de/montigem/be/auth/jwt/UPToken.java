/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.auth.jwt;

import org.apache.shiro.authc.AuthenticationToken;

public class UPToken implements AuthenticationToken {

  /**
   *
   */
  private static final long serialVersionUID = -5426391345560480008L;

  private String username;

  private String password;

  private String resource;

  public UPToken() {
  }

  public UPToken(String username, String password, String resource) {
    this.username = username;
    this.password = password;
    this.resource = resource;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getResource() {
    return resource;
  }

  @Override
  public Object getPrincipal() {
    return username;
  }

  @Override
  public Object getCredentials() {
    return password;
  }
}
