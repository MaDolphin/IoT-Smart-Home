/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import main.java.be.error.MaCoCoErrorCode;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class UniqueErrorCodesTest {
  
  @Test
  public void testUniquenessOfErrorCodes() {
    List<String> errorCodes = new ArrayList<>();
    for (MaCoCoErrorCode f : MaCoCoErrorCode.values()) {
      assertFalse("Duplicate error code " + f.getCode() + " !", errorCodes.contains(f.getCode()));
      errorCodes.add(f.getCode());
      
    }
  }
  
}
