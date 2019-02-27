/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */
package de.montigem.be;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import de.montigem.be.util.json.JsonBooleanValue;

/**
 * TODO: Write me!
 *
 *
 * @author  (last commit) $Author$
 * @version $Date$<br>
 *          $Revision$
 */
public class TokenServiceTest extends AbstractDomainTest {
  
  @Test
  public void testTokenValidity() throws Exception {
    loginBootstrapUserWithDBReset();
    
    // jwt should be valid
    String jwt = getJWT();
    assertTrue(testGet("api/auth/tokens/" + jwt + "/validity", Status.OK, JsonBooleanValue.class).getValue());
    
    logout(Status.NO_CONTENT);

    // jwt should be invalid
    assertFalse(testGetWithoutAuth("api/auth/tokens/" + jwt + "/validity", Status.OK, JsonBooleanValue.class).getValue());
  }
}
