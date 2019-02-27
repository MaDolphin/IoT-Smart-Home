/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.authz.model.exceptions;

public class AssignmentNotFoundException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -4404667074764615024L;
  
  public AssignmentNotFoundException() {
    super("Assignment not found!");
  }
}
