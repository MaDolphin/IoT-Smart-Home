/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.auth.jwt;


public class PrincipalWrapper {


  private String resource;

  public PrincipalWrapper(String resource) {
    this.resource = resource;
  }

  public String getResource() {
    return resource;
  }
}
