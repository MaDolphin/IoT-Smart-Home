/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.montigem.be.error;


/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$, $Date$
 * @since   TODO: add version number
 *
 */
public class DataConsistencyException extends RuntimeException {

  /**
   * TODO: Write me!
   */
  private static final long serialVersionUID = 306863717224573129L;
  
  public DataConsistencyException(String message) {
    super(message);
  }
  
}
