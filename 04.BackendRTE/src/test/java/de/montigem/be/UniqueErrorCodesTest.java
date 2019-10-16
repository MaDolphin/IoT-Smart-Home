/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be;

import de.montigem.be.error.MontiGemErrorCode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;

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
    for (MontiGemErrorCode f : MontiGemErrorCode.values()) {
      assertFalse("Duplicate error code " + f.getCode() + " !", errorCodes.contains(f.getCode()));
      errorCodes.add(f.getCode());
      
    }
  }
  
}
