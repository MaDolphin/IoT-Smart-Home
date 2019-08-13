/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.auth.jwt;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.domain.cdmodelhwc.daos.DomainUserDAO;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.error.MontiGemErrorFactory;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;

import java.util.Optional;

public class MontiGemSecurityUtils {

  public static String encodePassword(String plainPwd, byte[] salt) {
    if (plainPwd == null) {
      return null;
    }
    return new Sha256Hash(plainPwd, salt, 1024).toBase64();
  }

  public static String encodePasswordOptional(Optional<String> plainPwd, byte[] salt) {
    if (plainPwd == null || !plainPwd.isPresent()) {
      return null;
    }
    return new Sha256Hash(plainPwd.get(), salt, 1024).toBase64();
  }

  public static String generateSaltAsBase64() {
    RandomNumberGenerator rng = new SecureRandomNumberGenerator();
    String salt = rng.nextBytes().toBase64();
    return salt;
  }

  public static void setPasswordForUserWithId(long userId, String password, DomainUserDAO userDAO, SecurityHelper securityHelper) throws MontiGemError {
    Optional<DomainUser> user = userDAO
            .find(userId, securityHelper.getSessionCompliantResource());

    if (!user.isPresent()) {
      throw MontiGemErrorFactory.loadIDError("DomainUser", userId);
    }

    setPasswordForUser(user.get(), password, userDAO, securityHelper);
  }

  public static void setPasswordForUserWithName(String username, String password, DomainUserDAO userDAO, SecurityHelper securityHelper) throws MontiGemError {
    Optional<DomainUser> user = userDAO
            .find(username, securityHelper.getSessionCompliantResource());

    if (!user.isPresent()) {
      throw MontiGemErrorFactory.loadIDError("DomainUser: " + username);
    }

    setPasswordForUser(user.get(), password, userDAO, securityHelper);
  }

  public static void setPasswordForUser(DomainUser user, String password, DomainUserDAO userDAO, SecurityHelper securityHelper) {
    String salt = MontiGemSecurityUtils.generateSaltAsBase64();
    user.setEncodedPassword(Optional.of(encodePassword(password, Base64.decode(salt))));
    user.setPasswordSaltBase64(salt);
    userDAO.updateWithoutAssociations(user, securityHelper.getSessionCompliantResource());
  }

}

