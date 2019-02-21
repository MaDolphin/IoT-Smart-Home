/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.auth;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Response.Status;

import de.montigem.be.AbstractDomainTest;
import org.junit.Test;

import main.java.be.auth.jwt.ExtendedJWT;
import main.java.be.util.json.JsonBooleanValue;
import main.java.be.util.json.JsonStringValue;

/**
 * TODO: Write me!
 *
 *
 * @author  (last commit) $Author$
 * @version $Date$<br>
 *          $Revision$
 */
public class RefreshTokenTest extends AbstractDomainTest {

  @Test
  public void testJWTRetrieval() throws Exception {
    login("admin", "passwort", Status.OK);
    String refreshToken = getRefreshToken();
    
    // test retrieval of new access token using refresh token
    ExtendedJWT newToken = testPost("/api/auth/tokens/" + getJwt() + "/refresh", Status.OK, new JsonStringValue(refreshToken), ExtendedJWT.class);
    assertNotNull(newToken);
    
    // validate new token
    JsonBooleanValue valid = testGetWithoutAuth("/api/auth/tokens/" + newToken.getJwt() + "/validity", Status.OK, JsonBooleanValue.class);
    assertTrue(valid.getValue());
  }
}
