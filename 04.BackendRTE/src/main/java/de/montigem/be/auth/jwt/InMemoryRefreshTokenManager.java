/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.auth.jwt;

import de.se_rwth.commons.logging.Log;

import javax.ejb.Lock;
import javax.ejb.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.ejb.LockType.READ;
import static javax.ejb.LockType.WRITE;

/**
 * TODO: Write me!
 *
 */
@Singleton
public class InMemoryRefreshTokenManager implements IRefreshTokenManager {

  /**
   * Maps refresh tokens to username
   */
  private Map<String, String> refreshTokens = new HashMap<>();

  /**
   * @see IRefreshTokenManager#registerRefreshToken(String, String)
   */
  @Override
  @Lock(WRITE)
  public void registerRefreshToken(String refreshToken, String username) {
    Log.info("Register refresh token: " + refreshTokens, getClass().getName());
    refreshTokens.put(refreshToken, username);
  }

  /**
   * @see IRefreshTokenManager#removeRefreshToken(String)
   */
  @Override
  @Lock(WRITE)
  public void removeRefreshToken(String refreshToken) {
    Log.info("Remove token: " + refreshToken, getClass().getName());
    refreshTokens.remove(refreshToken);
  }

  /**
   * @see IRefreshTokenManager#retrieveUsernameForRefreshToken(String)
   */
  @Override
  @Lock(READ)
  public String retrieveUsernameForRefreshToken(String refreshToken) {
    Log.debug("Retrieve user-name for refresh token: " + refreshToken + " in: " + refreshTokens,
        getClass().getName());
    return refreshTokens.get(refreshToken);
  }

  @Override
  @Lock(WRITE)
  public void removeRefreshTokenByUsername(String username) {
    Log.info("Removing token for: " + username, getClass().getName());
    List<String> foundRefreshTokens = new ArrayList<>();
    for (String key : refreshTokens.keySet()) {
      if (refreshTokens.get(key).equals(username)) {
        foundRefreshTokens.add(key);
        Log.info("Found and remove token: " + key, getClass().getName());
      }
    }
    for (String token : foundRefreshTokens) {
      refreshTokens.remove(token);
    }
    Log.info("End of Removing token for" + username, getClass().getName());
  }
}
