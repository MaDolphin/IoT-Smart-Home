/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.shiro;

import de.montigem.be.auth.jwt.JWTRealm;
import de.montigem.be.auth.jwt.JWToken;
import de.montigem.be.auth.jwt.ShiroJWTFilter;
import de.montigem.be.authz.PolicyDecisionPoint;
import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.NamingException;
import java.util.Collections;

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
    init(user, resource);
  }

  public void init(String user, String resource) throws NamingException {
    DefaultSecurityManager securityManager;
    try {
      securityManager = (DefaultSecurityManager) SecurityUtils.getSecurityManager();
      logoutLoggedInSubject();
      createSubjectAndLogin(securityManager, user, resource);
    }
    catch (UnavailableSecurityManagerException e) {
      securityManager = new DefaultSecurityManager();
      createSubjectAndLogin(securityManager, user, resource);
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
    createSubjectAndLogin(securityManager, user, resource);
  }

  private void createSubjectAndLogin(DefaultSecurityManager securityManager, String user, String resource)
      throws NamingException {
    SecurityHelper.createSubjectAndLogin(securityManager, user != null ? user : username, resource, daoLib, rolePermissionManager);
  }
}
