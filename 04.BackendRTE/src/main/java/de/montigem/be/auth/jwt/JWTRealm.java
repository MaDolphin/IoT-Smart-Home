package de.montigem.be.auth.jwt;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.AuthenticatingRealm;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class JWTRealm extends AuthenticatingRealm {

 // private MacocoUserDAO dao;

  // TODO GV, SVa
  public JWTRealm() throws NamingException {
    Properties props = new Properties();
    props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
        "org.apache.openejb.client.LocalInitialContextFactory");
    InitialContext context = new InitialContext(props);
 //   dao = (MacocoUserDAO) context.lookup("java:global/macoco-be/MacocoUserDAO");
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
  // TODO GV, SVa
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
      throws AuthenticationException {
    JWToken jwt = (JWToken) token;
    if (jwt.getResource() != null) {
     /* Optional<MacocoUser> user = dao.find(jwt.getUsername(), jwt.getResource());
      if (user.isPresent() && !BlackListManager.getBlacklist()
          .isTokenBlacklisted(jwt.getToken(), jwt.getResource()) &&
          user.get().getActivated().getName().equals(MacocoUserActivationStatus.AKTIVIERT.getName()) && user.get().isEnabled()) {
        return new SimpleAccount(new PrincipalWrapper(user.get(), jwt.getResource()),
            jwt.getToken(),
            getName());
      }*/
    }
    throw new AuthenticationException();

  }
}
