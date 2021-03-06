/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.auth.jwt;

import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.codec.Base64;

public class CustomCredentialsMatcher implements CredentialsMatcher {

  @Override
  public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
   DomainUser user = ((PrincipalWrapper) info.getPrincipals().getPrimaryPrincipal()).getUser();
    UPToken upToken = (UPToken) token;
    byte[] salt = Base64.decode(user.getPasswordSaltBase64());
    boolean res = MontiGemSecurityUtils.encodePassword(String.valueOf(upToken.getPassword()), salt)
            .equals(user.getEncodedPassword().orElse(""));
    return res;
  }
}
