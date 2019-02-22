/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.shiro;

import de.montigem.be.auth.jwt.JWTRealm;
import de.montigem.be.auth.jwt.JWToken;
import de.montigem.be.auth.jwt.ShiroJWTFilter;
import de.montigem.be.authz.PolicyDecisionPoint;
import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.util.DAOLib;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.NamingException;
import java.util.Arrays;

@Stateless
public class SecurityManagerInit {
  @Inject
  private DAOLib daoLib;

  @Inject
  private RolePermissionManager rolePermissionManager;

  private final String username = "admin";

  private final String resource = "TestDB";

  public void init() throws NamingException {
    init(null);
  }

  public void init(String user) throws NamingException {
    DefaultSecurityManager securityManager;
    try {
      securityManager = (DefaultSecurityManager) SecurityUtils.getSecurityManager();
      logoutLoggedInSubject();
      createSubjectAndLogin(securityManager, user);
    } catch (UnavailableSecurityManagerException e) {
      securityManager = new DefaultSecurityManager();
      createSubjectAndLogin(securityManager, user);
    }
  }

  private void logoutLoggedInSubject() {
    Subject currentUser = SecurityUtils.getSubject();
    if (currentUser != null) {
      currentUser.logout();
      Session session = currentUser.getSession(false);
      if (session != null) {
        session.stop();
      }
    }
  }

  private void createSubjectAndLogin(DefaultSecurityManager securityManager, String user)
      throws NamingException {
    ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
    authenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
    securityManager.setAuthenticator(authenticator);

    AuthorizingRealm authorizer = new PolicyDecisionPoint();
    authorizer.setPermissionResolver(new WildcardPermissionResolver());
    securityManager.setAuthorizer(authorizer);

    JWTRealm jwtRealm = new JWTRealm();
    securityManager.setRealms(Arrays.asList((Realm) jwtRealm));
    SecurityUtils.setSecurityManager(securityManager);

    Subject subject = SecurityUtils.getSubject();

    JWToken token = new JWToken(ShiroJWTFilter.createTokenForUser(user != null ? user : username, false, resource, daoLib, rolePermissionManager),
        user != null ? user : username, resource);
    subject.login(token);
  }
}
