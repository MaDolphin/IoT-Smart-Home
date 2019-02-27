/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.auth.jwt;

import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.domain.cdmodelhwc.classes.domainuseractivationstatus.DomainUserActivationStatus;
import de.montigem.be.domain.cdmodelhwc.daos.DomainUserDAO;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.realm.AuthenticatingRealm;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Optional;
import java.util.Properties;

public class LoginRealm extends AuthenticatingRealm {

  private DomainUserDAO dao;

  public LoginRealm() throws NamingException {
    Properties props = new Properties();
    props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
        "org.apache.openejb.client.LocalInitialContextFactory");
    InitialContext context = new InitialContext(props);
  }

  /**
   * @return True, if the token type is supported by this realm, false otherwise
   */
  @Override
  public boolean supports(AuthenticationToken token) {
    return token != null && token instanceof UPToken;
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
      throws AuthenticationException {
    UPToken upToken = (UPToken) token;
    if (upToken.getResource() != null) {
      Optional<DomainUser> user = dao.find(upToken.getUsername(), upToken.getResource());

      if (user.isPresent() && user.get().getActivated().getName().equals(DomainUserActivationStatus.AKTIVIERT.getName()) && user.get().isEnabled()) {
        return new SimpleAccount(
            new PrincipalWrapper(user.get(), upToken.getResource()),
            user.get().getEncodedPassword().orElse(""), getName());
      }
    }
    throw new AuthenticationException();
  }
}
