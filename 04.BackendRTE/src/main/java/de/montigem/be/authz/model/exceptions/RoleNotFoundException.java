/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.authz.model.exceptions;

public class RoleNotFoundException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -8542615541058677133L;

  public RoleNotFoundException(String name) {
    super("Role " + name + " not found!");
  }

  public RoleNotFoundException(long id) {
    super("Role with ID " + id + " not found!");
  }

}
