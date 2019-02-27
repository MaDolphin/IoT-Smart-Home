/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.authz.model.exceptions;

public class ObjectNotFoundException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 7621669978753634029L;

  public ObjectNotFoundException() {
    super("Object not found!");
  }
}
