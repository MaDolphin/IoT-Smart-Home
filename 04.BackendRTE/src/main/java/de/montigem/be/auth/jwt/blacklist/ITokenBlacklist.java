/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.auth.jwt.blacklist;

public interface ITokenBlacklist {

  /**
   * Adds a token to the blacklist
   *
   * @param token
   */
  void addToken(BlacklistedToken token, String resource);

  /**
   * Cleans up the blacklist by removing all tokens that
   * are outdated anyway and thus need to be stored on
   * the blacklist no longer.
   */
  void removeOutdatedTokens(String resource);

  /**
   * Checks whether a token is blacklisted.
   *
   * @param token
   * @return True if the token is blacklisted, false otherwise.
   */
  boolean isTokenBlacklisted(String token, String resource);
}
