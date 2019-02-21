/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be;

import de.montigem.be.auth.jwt.ExtendedJWT;
import de.montigem.be.auth.jwt.JWTLogin;
import de.montigem.be.auth.jwt.ShiroJWTFilter;
import de.montigem.be.marshalling.JsonMarshal;
import de.montigem.be.shiro.SecurityManagerInit;
import de.montigem.be.util.TestUtil;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.cxf.jaxrs.client.WebClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * TODO: Implement AALLL the methods
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
@RunWith(Arquillian.class)
public abstract class AbstractDomainTest {
  @Inject
  protected SecurityManagerInit securityManagerInit;

  protected static ExtendedJWT token;

  protected WebClient webClient;

  private final String resource = "TestDB";

  @ArquillianResource
  private URL webappUrl;

  private Response response;

  @Deployment(testable = true)
  public static WebArchive createDeployment() {
    WebArchive wa = ShrinkWrap.create(WebArchive.class, "macoco-be.war");
    TestUtil.addClassesToWebArchive(wa);
    return wa;
  }

  @Before
  public void bootstrap() throws Exception, URISyntaxException, IOException {
    webClient = WebClient.create(webappUrl.toURI());
    webClient.reset();
  }

  @Before
  public void secInit() throws NamingException {
    securityManagerInit.init();
  }

  protected String getRefreshToken() {
    return token.getRefreshToken();
  }

  protected String getJWT() {
    return token.getJwt();
  }

  protected void login(String username, String password, Status expectedCode) throws Exception {
    ExtendedJWT token = testPost("api/auth/login", expectedCode,
        JsonMarshal.getInstance().marshal(new JWTLogin(username, password, resource)),
        ExtendedJWT.class,
        false);

    if (expectedCode == Response.Status.OK) {
      this.token = token;
      Log.trace(">> received jwt: " + token.getJwt() + ", refreshToken: "
              + token.getRefreshToken() + ", expirationDate: " + token.getExpirationDate(),
          getClass().getName());
      assertNotNull(token);
    }
  }

  protected void logout(Status status) {
    webClient.reset();
    assertNotNull(token);
    response = webClient.path("api/auth/logout").header(ShiroJWTFilter.AUTH_HEADER, token.getJwt())
        .post(null);
    assertEquals(status.getStatusCode(), response.getStatus());
    token = null;
    webClient.reset();
  }

  /////////////////////////////////////////////////////////////////////////////////
  // TEST HTTP METHODS AND RETURN VOID
  /////////////////////////////////////////////////////////////////////////////////

  protected void testGet(String path, Status httpCode) throws IOException {
    response = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt()).get();
    assertEquals(httpCode.getStatusCode(), response.getStatus());
    Log.trace(">> " + TestUtil.slurp((InputStream) response.getEntity()), getClass().getName());
    webClient.reset();
  }

  protected String testPost(String path, Status httpCode, List<Pair<String, String>> params)
      throws IOException {
    WebClient builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());
    for (Pair<String, String> p : params) {
      builder = builder.query(p.getKey(), p.getValue());
    }
    response = builder.post(null);
    assertEquals(httpCode.getStatusCode(), response.getStatus());
    String result = TestUtil.slurp((InputStream) response.getEntity());
    Log.trace(">> " + result, getClass().getName());
    webClient.reset();
    return result;
  }
  /* protected void testPost(String path, Status httpCode, List<Pair<String,
   * String>> params) throws IOException { WebClient builder =
   * webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, jwt); for
   * (Pair<String, String> p : params) { builder = builder.query(p.getKey(),
   * p.getValue()); } response = builder.post(null);
   * assertEquals(httpCode.getStatusCode(), response.getStatus());
   * System.out.println(">> " + TestUtil.slurp((InputStream)
   * response.getEntity())); webClient.reset(); } */

  // protected void testPut(String path, Status httpCode, List<Pair<String,
  // String>> params)
  // throws IOException {
  // webClient.reset();
  // WebClient builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER,
  // token.getJwt());
  // for (Pair<String, String> p : params) {
  // builder = builder.query(p.getKey(), p.getValue());
  // }
  // response = builder.put(null);
  // assertEquals(httpCode.getStatusCode(), response.getStatus());
  // System.out.println(">> " + TestUtil.slurp((InputStream)
  // response.getEntity()));
  // webClient.reset();
  // }

  // protected void testDelete(String path, Status httpCode)
  // throws IOException {
  // WebClient builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER,
  // jwt);
  // response = builder.delete();
  // assertEquals(httpCode.getStatusCode(), response.getStatus());
  // System.out.println(">> " + TestUtil.slurp((InputStream)
  // response.getEntity()));
  // webClient.reset();
  // }

  protected void testDelete(String path, Status httpCode, List<Pair<String, String>> params)
      throws IOException {
    webClient.reset();
    WebClient builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());
    for (Pair<String, String> p : params) {
      builder = builder.query(p.getKey(), p.getValue());
    }
    response = builder.delete();
    assertEquals(httpCode.getStatusCode(), response.getStatus());
    Log.trace(">> " + TestUtil.slurp((InputStream) response.getEntity()), getClass().getName());
    webClient.reset();
  }

  protected Response testDeleteWithoutAssert(String path, Status httpCode,
      List<Pair<String, String>> params)
      throws IOException {
    webClient.reset();
    WebClient builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());
    for (Pair<String, String> p : params) {
      builder = builder.query(p.getKey(), p.getValue());
    }
    response = builder.delete();
    Log.trace(">> " + TestUtil.slurp((InputStream) response.getEntity()), getClass().getName());
    webClient.reset();
    return response;
  }

  /////////////////////////////////////////////////////////////////////////////////
  // TEST HTTP METHODS WITH SINGLE OR NO PARAMETER AND RETURN T
  /////////////////////////////////////////////////////////////////////////////////

  protected <T> T testGet(String path, Status httpCode, Class<T> clazz) throws Exception {
    return testGet(path, httpCode, clazz, token.getJwt());
  }

  protected <T> T testGet(String path, Status httpCode, Class<T> clazz, String jwt)
      throws Exception {
    webClient.reset();
    response = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, jwt).get();
    assertEquals(httpCode.getStatusCode(), response.getStatus());
    T t = TestUtil.slurp((InputStream) response.getEntity(), clazz);
    Log.trace(">> " + t, getClass().getName());
    webClient.reset();
    return t;
  }

  protected void testResetDB() {
    webClient.reset();
    response = webClient.path("api/develop/resetDB")
        .header(ShiroJWTFilter.AUTH_HEADER, token.getJwt()).get();
    assertEquals(200, response.getStatus());
  }

  protected void testResetDBwithUser() {
    webClient.reset();
  /*  try {
      webClient.reset();
    } catch (Exception e) {
      System.err.println("TODO MV: fix it please");
    }*/
    response = webClient.path("api/develop/resetDBwithUser")
        .header(ShiroJWTFilter.AUTH_HEADER, token.getJwt()).get();
    assertEquals(200, response.getStatus());
  }

  protected Response createDummy() {
    webClient.reset();

    response = webClient.path("api/develop/createDummy")
        .header(ShiroJWTFilter.AUTH_HEADER, token.getJwt()).post(null);
    assertEquals(200, response.getStatus());
    return response;
  }

  protected <T> T testGetWithoutAuth(String path, Status httpCode, Class<T> clazz)
      throws Exception {
    webClient.reset();
    response = webClient.path(path).get();
    assertEquals(httpCode.getStatusCode(), response.getStatus());
    T t = TestUtil.slurp((InputStream) response.getEntity(), clazz);
    Log.trace(">> " + t, getClass().getName());
    webClient.reset();
    return t;
  }

  protected <T> T testPost(String path, Status httpCode, Pair<String, String> params,
      Class<T> clazz) throws Exception {
    return testPost(path, httpCode, params, clazz, true);
  }

  protected <T> T testPost(String path, Status httpCode, Pair<String, String> params,
      Class<T> clazz,
      boolean authenticationNeeded) throws Exception {
    webClient.reset();
    WebClient builder;
    if (authenticationNeeded)
      builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());
    else
      builder = webClient.path(path);
    builder = builder.query(params.getKey(), params.getValue());
    response = builder.post(null);
    assertEquals(httpCode.getStatusCode(), response.getStatus());
    T t = TestUtil.slurp((InputStream) response.getEntity(), clazz);
    Log.trace(">> " + t, getClass().getName());
    webClient.reset();
    return t;
  }

  protected void testPost(String path, Status httpCode, Pair<String, String> params,
      boolean authenticationNeeded)
      throws Exception {
    WebClient builder;
    if (authenticationNeeded)
      builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());
    else
      builder = webClient.path(path);
    builder = builder.query(params.getKey(), params.getValue());
    response = builder.post(null);

    assertEquals(httpCode.getStatusCode(), response.getStatus());
    webClient.reset();
  }

  // protected <T> T testPut(String path, Status httpCode, Pair<String, String>
  // params, Class<T> clazz)
  // throws Exception {
  // WebClient builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER,
  // token.getJwt());
  // builder = builder.query(params.getKey(), params.getValue());
  // response = builder.put(null);
  // assertEquals(httpCode.getStatusCode(), response.getStatus());
  // T t = TestUtil.slurp((InputStream) response.getEntity(), clazz);
  // Log.trace(">> " + t, getClass().getName());
  // webClient.reset();
  // return t;
  // }

  protected <T> T testDelete(String path, Status httpCode, Pair<String, String> params,
      Class<T> clazz)
      throws Exception {
    WebClient builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());
    builder = builder.query(params.getKey(), params.getValue());
    response = builder.delete();
    assertEquals(httpCode.getStatusCode(), response.getStatus());
    T t = TestUtil.slurp((InputStream) response.getEntity(), clazz);
    Log.trace(">> " + t, getClass().getName());
    webClient.reset();
    return t;
  }

  protected <T> T testDelete(String path, Status httpCode, Class<T> clazz) throws Exception {
    WebClient builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());
    response = builder.delete();
    assertEquals(httpCode.getStatusCode(), response.getStatus());
    T t = TestUtil.slurp((InputStream) response.getEntity(), clazz);
    Log.trace(">> " + t, getClass().getName());
    webClient.reset();
    return t;
  }

  /////////////////////////////////////////////////////////////////////////////////
  // TEST HTTP METHODS WITH LIST OF PARAMETERS AND RETURN T
  /////////////////////////////////////////////////////////////////////////////////

  protected <T> T testPost(String path, Status httpCode, List<Pair<String, String>> params,
      Class<T> clazz)
      throws Exception {
    WebClient builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());
    for (Pair<String, String> p : params) {
      builder = builder.query(p.getKey(), p.getValue());
    }
    response = builder.post(null);
    assertEquals(httpCode.getStatusCode(), response.getStatus());
    T t = TestUtil.slurp((InputStream) response.getEntity(), clazz);
    Log.trace(">> " + t, getClass().getName());
    webClient.reset();
    return t;
  }

  // protected <T> T testPut(String path, Status httpCode, List<Pair<String,
  // String>> params,
  // Class<T> clazz)
  // throws Exception {
  // WebClient builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER,
  // token.getJwt());
  // for (Pair<String, String> p : params) {
  // builder = builder.query(p.getKey(), p.getValue());
  // }
  // response = builder.put(null);
  // assertEquals(httpCode.getStatusCode(), response.getStatus());
  // T t = TestUtil.slurp((InputStream) response.getEntity(), clazz);
  // Log.trace(">> " + t, getClass().getName());
  // webClient.reset();
  // return t;
  // }

  protected <T> T testDelete(String path, Status httpCode, List<Pair<String, String>> params,
      Class<T> clazz)
      throws Exception {
    WebClient builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());
    for (Pair<String, String> p : params) {
      builder = builder.query(p.getKey(), p.getValue());
    }
    response = builder.delete();
    assertEquals(httpCode.getStatusCode(), response.getStatus());
    T t = TestUtil.slurp((InputStream) response.getEntity(), clazz);
    Log.trace(">> " + t, getClass().getName());
    webClient.reset();
    return t;
  }

  ////////////////////////////////////////////////////////////////////

  protected <T> T testPost(String path, Status expectedStatus, Object body, Class<T> clazz)
      throws Exception {
    return testPost(path, expectedStatus, body, clazz, true);
  }

  protected <T> T testPost(String path, Status expectedStatus, Object body, Class<T> clazz,
      boolean auth)
      throws Exception {
    webClient.reset();
    WebClient builder;
    if (auth)
      builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());
    else
      builder = webClient.path(path);

    builder = builder.header("Content-Type", "application/json");
    response = builder.post(body);
    assertEquals(expectedStatus.getStatusCode(), response.getStatus());
    T t = TestUtil.slurp((InputStream) response.getEntity(), clazz);
    Log.trace(">> Result: " + t, getClass().getName());
    webClient.reset();
    return t;
  }

  protected Response testPostStatus(String path, Object body)
      throws Exception {
    webClient.reset();
    WebClient builder;
    builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());

    builder = builder.header("Content-Type", "application/json");
    response = builder.post(body);
    webClient.reset();
    return response;
  }

  protected <T> T testPostOctet(String path, Status expectedStatus, Object body, Class<T> clazz,
      boolean auth)
      throws Exception {
    webClient.reset();
    WebClient builder;
    if (auth)
      builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());
    else
      builder = webClient.path(path);
    builder = builder.header("Content-Type", "application/octet-stream");
    response = builder.post(body);
    assertEquals(expectedStatus.getStatusCode(), response.getStatus());

    T t = TestUtil.slurp((InputStream) response.getEntity(), clazz);
    Log.trace(">> Result: " + t, getClass().getName());
    webClient.reset();
    return t;
  }

  protected <T> T testPostCommaSeparatedValues(String path, Status expectedStatus, Object body, Class<T> clazz,
                                               boolean auth, String header)
      throws Exception {
    webClient.reset();
    WebClient builder;
    if (auth) {
      builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());
    }
    else {
      builder = webClient.path(path);
    }
    builder = builder.header("Content-Type", "text/comma-separated-values");
    builder = builder.header("charset", "iso-8859-1");
    builder = builder.header("Date",header);
    response = builder.post(body);
    assertEquals(expectedStatus.getStatusCode(), response.getStatus());

    T t = TestUtil.slurp((InputStream) response.getEntity(), clazz);
    Log.trace(">> Result: " + t, getClass().getName());
    webClient.reset();
    return t;
  }

  protected <T> T testPut(String path, Status expectedStatus, Object body, Class<T> clazz)
      throws Exception {
    return testPut(path, expectedStatus, body, clazz, true);
  }

  protected <T> T testPut(String path, Status expectedStatus, Object body, Class<T> clazz,
      boolean auth)
      throws Exception {
    webClient.reset();
    WebClient builder;
    if (auth)
      builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());
    else
      builder = webClient.path(path);

    builder = builder.header("Content-Type", "application/json");
    if (body != null) {
      response = builder.put(body);
    }
    else {
      response = builder.put("");
    }
    assertEquals(expectedStatus.getStatusCode(), response.getStatus());
    T t = TestUtil.slurp((InputStream) response.getEntity(), clazz);
    Log.trace(">> Result: " + t, getClass().getName());
    webClient.reset();
    return t;
  }

  public Response testPutStatus(String path, Object body)
      throws Exception {
    webClient.reset();
    WebClient builder;
    builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());

    builder = builder.header("Content-Type", "application/json");
    if (body != null) {
      response = builder.put(body);
    }
    else {
      response = builder.put("");
    }
    webClient.reset();
    return response;
  }

  protected void testPost(String path, Status expectedStatus, Object body, boolean auth)
      throws Exception {
    webClient.reset();
    WebClient builder;
    if (auth)
      builder = webClient.path(path).header(ShiroJWTFilter.AUTH_HEADER, token.getJwt());
    else
      builder = webClient.path(path);

    builder.header("Content-Type", "application/json");
    response = builder.post(body);
    assertEquals(expectedStatus.getStatusCode(), response.getStatus());
    webClient.reset();
  }

  ///////////////////////////////////////////////////////////////////////////
  // User creation
  //////////////////////////////////////////////////////////////////////////

  protected void loginBootstrapUser() throws Exception {
    // create and login bootstrap user
    Log.trace(">> logging in bootstrap user...", getClass().getName());
    login("admin", "passwort", Response.Status.OK);
  }

  protected void loginBootstrapUserWithDBReset() throws Exception {
    // create and login bootstrap user
    Log.trace(">> logging in bootstrap user...", getClass().getName());
    login("admin", "passwort", Response.Status.OK);
    // reset DB with user
    testResetDBwithUser();
  }

  protected String getJwt() {
    return token.getJwt();
  }

}
