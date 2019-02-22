/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.auth.jwt;


import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;

public class PrincipalWrapper {

  private DomainUser user;

  private String resource;

  public PrincipalWrapper(DomainUser user, String resource) {
    this.user = user;
    this.resource = resource;
  }

  public DomainUser getUser() {
    return user;
  }

  public String getResource() {
    return resource;
  }
}
