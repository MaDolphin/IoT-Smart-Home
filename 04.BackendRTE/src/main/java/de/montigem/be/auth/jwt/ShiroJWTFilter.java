/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.auth.jwt;

import de.montigem.be.auth.jwt.blacklist.BlackListManager;
import de.montigem.be.auth.jwt.blacklist.BlacklistedToken;
import de.montigem.be.auth.jwt.blacklist.ITokenBlacklist;
import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.database.DatabaseDataSource;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.marshalling.JsonMarshal;
import de.montigem.be.util.DAOLib;
import de.se_rwth.commons.logging.Log;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ShiroJWTFilter extends AuthenticatingFilter {

  public static final String AUTH_HEADER = "X-AUTH";

  public static final long VALIDITY = TimeUnit.HOURS.toMillis(4);

  private static String secret;

  private static SecureRandom random = new SecureRandom();

  private String generalUrl;

  private String logoutUrl;

  private String changePwdUrl;

  private String forgotPwdUrl;

  private String activationUrl;

  private IRefreshTokenManager refreshTokenManager;

  private ITokenBlacklist tokenBlacklist = BlackListManager.getBlacklist();

  private DatabaseDataSource databaseDataSource;

  private DAOLib daoLib;

  private RolePermissionManager rolePermissionManager;

  public ShiroJWTFilter() throws NamingException {
    secret = new SecureRandomNumberGenerator().nextBytes().toBase64();
    Properties props = new Properties();
    props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
            "org.apache.openejb.client.LocalInitialContextFactory");
    InitialContext context = new InitialContext(props);
    refreshTokenManager = (IRefreshTokenManager) context
            .lookup("java:global/montigem-be/InMemoryRefreshTokenManager");
    databaseDataSource = (DatabaseDataSource) context
            .lookup("java:global/montigem-be/DatabaseDataSource");
    daoLib = (DAOLib) context
            .lookup("java:global/montigem-be/DAOLib");
    rolePermissionManager = (RolePermissionManager) context
            .lookup("java:global/montigem-be/RolePermissionManager");
  }

  /**
   * Decodes the subject from a JWT
   *
   * @param token The JWT to be decoded
   * @return
   */
  private static String getSubject(String token) {
    return Jwts.parser()
            .setSigningKey(secret)
            .setAllowedClockSkewSeconds(1200)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
  }

  /**
   * Retrieves the username encoded in a JWT token
   *
   * @param token
   * @return
   */
  public static String getUsernameFromToken(String token) {
    String subject = getSubject(token);
    String[] subjectSplitted = subject.split("\\$");
    if (subjectSplitted.length == 4) {
      return subjectSplitted[0];
    } else {
      return null;
    }
  }

  public static String getServerInstanceFromToken(String token) {
    String subject = getSubject(token);
    String[] subjectSplitted = subject.split("\\$");
    if (subjectSplitted.length == 4) {
      return subjectSplitted[2];
    } else {
      return null;
    }
  }

  public static String getPermissionFlagsFromToken(String token) {
    String subject = getSubject(token);
    String[] subjectSplitted = subject.split("\\$");
    if (subjectSplitted.length == 4) {
      return subjectSplitted[3];
    } else {
      return null;
    }
  }

  /**
   * Given a token, determines whether the token can be used for changing the password only.
   *
   * @param token
   * @return
   */
  public static boolean canChangePwdOnly(String token) {
    String subject = getSubject(token);
    String[] subjectSplitted = subject.split("\\$");
    if (subjectSplitted.length == 4) {
      return Boolean.valueOf(subjectSplitted[1]);
    } else {
      return true;
    }
  }

  /**
   * Encodes a username and scope in a JWT token. The scope determines whether the user is allowed to perform a
   * password change only or whether other actions are allowed too.
   *
   * @param username      Username to be encoded in token
   * @param onlyChangePwd Scope to be encoded in token
   * @return The generated token
   */
  public static String createTokenForUser(String username, boolean onlyChangePwd, String resource, DAOLib daoLib, RolePermissionManager rolePermissionManager) {
    Date now = new Date();
    Date expiration = new Date(now.getTime() + VALIDITY);

    return Jwts.builder()
            .setId(UUID.randomUUID().toString())
            .setSubject(username + "$" + onlyChangePwd + "$" + resource + "$" + getPermissionBytes(username, resource, daoLib, rolePermissionManager))
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
  }

  protected static String getPermissionBytes(String username, String resource, DAOLib daoLib, RolePermissionManager rolePermissionManager) {
    return PermissionFlags.asBinaryString(daoLib.getRoleAssignmentDAO().getRoleAssignmentsByUsername(username, resource), rolePermissionManager);
  }

  public static String generateRefreshToken() {
    return new BigInteger(130, random).toString(32);
  }

  public void setGeneralUrl(String generalUrl) {
    this.generalUrl = generalUrl;
  }

  public void setLogoutUrl(String url) {
    this.logoutUrl = url;
  }

  public void setChangePwdUrl(String url) {
    this.changePwdUrl = url;
  }

  public void setActivationUrl(String url) {
    this.activationUrl = url;
  }

  @Override
  public void setLoginUrl(String loginUrl) {
    String previous = getLoginUrl();
    if (previous != null) {
      this.appliedPaths.remove(previous);
    }
    super.setLoginUrl(loginUrl);
    this.appliedPaths.put(getLoginUrl(), null);
  }

  /**
   * If this is a login attempt, a {@link UsernamePasswordToken} containing the
   * login's username, password and resource is returned. If the user is already logged
   * in, a {@link JWToken} is generated, containing the web token and the
   * encoded username.
   */
  @Override
  protected AuthenticationToken createToken(ServletRequest request, ServletResponse response)
          throws Exception {
    if (isLoginRequest(request, response)) {
      Log.debug("login request", getClass().getName());
      String json = IOUtils.toString(request.getInputStream());
      if (json != null && !json.isEmpty()) {
        Optional<JWTLogin> login = JsonMarshal.getInstance()
                .unmarshalOptional(json, JWTLogin.class);
        if (login.isPresent()) {

          Log.debug("logging user " + login.get().getUsername() + " in", getClass().getName());

          Optional<String> databaseName = databaseDataSource
                  .getDatabaseName(login.get().getResource());

          if (!databaseName.isPresent()) {
            return new UPToken();
          }

          return new UPToken(login.get().getUsername(), login.get().getPassword(),
                  databaseName.get());
        }
      }
    } else if (isLoggedAttempt(request)) {

      HttpServletRequest httpRequest = WebUtils.toHttp(request);
      String token = httpRequest.getHeader(AUTH_HEADER);
      try {
        return new JWToken(token, getUsernameFromToken(token), getServerInstanceFromToken(token));
      } catch (Exception e) {
        return new UPToken();
      }
    }
    return new UPToken();
  }

  @Override
  public boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
                                ServletResponse response) throws IOException {

    if (isLoginRequest(request, response)) {
      Instant date = new Date(new Date().getTime() + VALIDITY).toInstant();
      ZonedDateTime expirationDate = ZonedDateTime.ofInstant(date, ZoneId.systemDefault());
      DomainUser user = ((PrincipalWrapper) subject.getPrincipal()).getUser();
      String jwToken = createTokenForUser(user.getUsername(),
              false, ((PrincipalWrapper) subject.getPrincipal()).getResource(), daoLib, rolePermissionManager);
      String refreshToken = generateRefreshToken();
      refreshTokenManager.removeRefreshTokenByUsername(user.getUsername());
      refreshTokenManager
              .registerRefreshToken(refreshToken, user.getUsername());
      ExtendedJWT extjwt = new ExtendedJWT(jwToken, refreshToken, expirationDate);
      PrintWriter writer = new PrintWriter(response.getOutputStream());
      WebUtils.toHttp(response).addHeader("content-type", "application/json");
      writer.write(JsonMarshal.getInstance().marshal(extjwt));
      writer.flush();
    }
    return true;
  }

  /**
   * Determines whether the request contains an authentication header
   *
   * @param request
   * @return
   */
  private boolean isLoggedAttempt(ServletRequest request) {
    HttpServletRequest httpRequest = WebUtils.toHttp(request);
    String token = httpRequest.getHeader(AUTH_HEADER);
    return token != null && !token.isEmpty();
  }

  private boolean isLogoutRequest(ServletRequest request) {
    return pathsMatch(logoutUrl, request);
  }

  /**
   * Checks whether request is a login request or whether it contains an
   * authentication header. If the former or latter is <></>he case, the method
   * tries to log the user in. If login fails or if no authentication header is
   * present, access is denied.
   *
   * @param request
   * @param response
   * @return True if access is granted, false otherwise.F
   * @throws Exception
   */
  @Override
  protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
          throws Exception {
    boolean access = false;

    HttpServletRequest httpReq = WebUtils.toHttp(request);

    // check whether authentication is needed
    if (httpReq.getServletPath().startsWith(generalUrl) && "GET".equals(httpReq.getMethod())) {
      access = true;
    } else if (httpReq.getServletPath().startsWith("/api/auth/tokens") && (httpReq.getMethod()
            .equals("GET") || "POST".equals(httpReq.getMethod()))) {
      access = true;
    } else if (httpReq.getServletPath().startsWith(this.changePwdUrl) && httpReq.getMethod()
            .equals("POST")) {
      access = true;
    } else if (httpReq.getServletPath().startsWith(activationUrl) && httpReq.getMethod()
            .equals("POST")) {
      access = true;
    } else if (httpReq.getServletPath().startsWith(this.forgotPwdUrl) && httpReq.getMethod()
            .equals("POST")) {
      access = true;
    } else if (httpReq.getServletPath().startsWith("/api/logger/log") && httpReq.getMethod()
            .equals("POST")) {
      access = true;
    } else if (httpReq.getServletPath().startsWith("/api/domain/datasource/dbname") && httpReq
            .getMethod().equals("GET")) {
      access = true;
    } else if (isLoginRequest(request, response)) {
      access = executeLogin(request, response);
      // if JWT must only be used for changing the pwd, validate url that the user tries to access
      if (access && isLoggedAttempt(request)) {
        System.out.println("isloggedattempt");
        String jwt = httpReq.getHeader(AUTH_HEADER);
        try {
          boolean onlyChangePwd = canChangePwdOnly(jwt);
          if (onlyChangePwd && !httpReq.getServletPath()
                  .startsWith("/api/domain/users/me/password")) {
            access = false;
          }
        } catch (Exception e) {
          Log.warn("MABx5005", e);
          access = false;
        }
      }
      if (access && isLogoutRequest(request)) {

        // add token to blacklist and clean up blacklist
        // invalidate refresh token
        String jwt = httpReq.getHeader(AUTH_HEADER);
        access = isAccess(request, access, jwt);
      }

      if (!access) {
        HttpServletResponse res = WebUtils.toHttp(response);
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return access;
      }

    } else if (isLoggedAttempt(request)) {
      access = executeLogin(request, response);
      if (access && isLogoutRequest(request)) {

        // add token to blacklist and clean up blacklist
        // invalidate refresh token
        String jwt = httpReq.getHeader(AUTH_HEADER);
        System.out.println(jwt);
        access = isAccess(request, access, jwt);
      }
      if (!access) {

        HttpServletResponse res = WebUtils.toHttp(response);
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return access;
      }
    }
    if (!access) {
      String jwt = httpReq.getHeader(AUTH_HEADER);
      HttpServletResponse res = WebUtils.toHttp(response);
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
    return access;
  }

  private boolean isAccess(ServletRequest request, boolean access, String jwt) {
    String username;
    Date expiresAt;
    try {
      username = getUsernameFromToken(jwt);
      expiresAt = Jwts.parser()
              .setSigningKey(secret)
              .parseClaimsJws(jwt)
              .getBody().getExpiration();

      HttpServletRequest httpRequest = WebUtils.toHttp(request);
      String token = httpRequest.getHeader(AUTH_HEADER);
      String resource = getServerInstanceFromToken(token);
      tokenBlacklist.addToken(new BlacklistedToken(jwt,
              LocalDateTime.ofInstant(expiresAt.toInstant(), ZoneId.systemDefault())), resource);
      tokenBlacklist.removeOutdatedTokens(resource);

      // invalidate refresh token
      refreshTokenManager.removeRefreshTokenByUsername(username);
    } catch (Exception e) {
      Log.warn("MABx5007", e);
      access = false;
    }
    return access;
  }

  public void setForgotPwdUrl(String forgotPwdUrl) {
    this.forgotPwdUrl = forgotPwdUrl;
  }

  private boolean checkIfDummy(HttpServletRequest httpReq) {
    boolean test = httpReq.getServletPath().contains("resetDB") ||
            httpReq.getServletPath().contains("createDummy");
    return test;
  }

}
