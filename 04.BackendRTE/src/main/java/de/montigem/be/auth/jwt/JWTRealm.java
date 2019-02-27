/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.auth.jwt;

import de.montigem.be.auth.jwt.blacklist.BlackListManager;
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

public class JWTRealm extends AuthenticatingRealm {

  private DomainUserDAO dao;

  public JWTRealm() throws NamingException {
    Properties props = new Properties();
    props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
        "org.apache.openejb.client.LocalInitialContextFactory");
    InitialContext context = new InitialContext(props);
    dao = (DomainUserDAO) context.lookup("java:global/montigem-be/DomainUserDAO");
  }

  /**
   * @return True, if the token type is supported by this realm, false otherwise
   */
  @Override
  public boolean supports(AuthenticationToken token) {
    return token != null && token instanceof JWToken;
  }

  /**
   * Retrieves an {@link AuthenticationInfo} object from the database, given a JWT.
   */
  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
      throws AuthenticationException {
    JWToken jwt = (JWToken) token;
    if (jwt.getResource() != null) {
     Optional<DomainUser> user = dao.find(jwt.getUsername(), jwt.getResource());
      if (user.isPresent() && !BlackListManager.getBlacklist()
          .isTokenBlacklisted(jwt.getToken(), jwt.getResource()) &&
          user.get().getActivated().getName().equals(DomainUserActivationStatus.AKTIVIERT.getName()) && user.get().isEnabled()) {
        return new SimpleAccount(new PrincipalWrapper(user.get(), jwt.getResource()),
            jwt.getToken(),
            getName());
      }
    }
    throw new AuthenticationException();

  }
}
