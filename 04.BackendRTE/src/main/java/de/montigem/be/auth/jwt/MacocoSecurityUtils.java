package de.montigem.be.auth.jwt;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;

import java.util.Optional;

public class MacocoSecurityUtils {

  public static String encodePassword(String plainPwd, byte[] salt) {
    if(plainPwd == null) {
      return null;
    }
    return new Sha256Hash(plainPwd, salt, 1024).toBase64();
  }

  public static String encodePasswordOptional(Optional<String> plainPwd, byte[] salt) {
    if(plainPwd == null || !plainPwd.isPresent()) {
      return null;
    }
    return new Sha256Hash(plainPwd.get(), salt, 1024).toBase64();
  }

  public static String generateSaltAsBase64() {
    RandomNumberGenerator rng = new SecureRandomNumberGenerator();
    String salt = rng.nextBytes().toBase64();
    return salt;
  }

}

