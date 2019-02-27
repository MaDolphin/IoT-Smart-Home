/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */
package de.montigem.be.auth.jwt;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Date$<br>
 * $Revision$
 */
public interface IRefreshTokenManager {
  
  /**
   * Registers the refresh token for a user. Overwrites the existing token if
   * there is one.
   * 
   * @param username
   * @param refreshToken
   */
  void registerRefreshToken(String refreshToken, String username);
  
  /**
   * Removes the registered refresh token.
   * 
   * @param refreshToken
   */
  void removeRefreshToken(String refreshToken);

  void removeRefreshTokenByUsername(String username);
  
  /**
   * Retrieves a user for a registered refresh token
   *
   * @param refreshToken
   * @return The registered username, or null if the specified refresh token
   * does not exist
   */
  String retrieveUsernameForRefreshToken(String refreshToken);
}
