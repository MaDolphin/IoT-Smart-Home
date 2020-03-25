/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.auth.jwt.blacklist;

import javax.ejb.Lock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.ejb.LockType.READ;
import static javax.ejb.LockType.WRITE;

/**
 * TODO: Write me!
 *
 */
public class MemoryTokenBlacklist implements ITokenBlacklist {

  protected List<BlacklistedToken> list;

  /**
   * Constructor for MemoryTokenBlacklist
   */
  public MemoryTokenBlacklist() {
    list = new ArrayList<>();
  }

  /**
   * @see ITokenBlacklist#addToken(BlacklistedToken, String)( BlacklistedToken )
   */
  @Override
  public void addToken(BlacklistedToken token, String resource) {
    list.add(token);
  }

  /**
   * @see ITokenBlacklist#removeOutdatedTokens(String)
   */
  @Override
  @Lock(WRITE)
  public void removeOutdatedTokens(String resource) {
    for (BlacklistedToken token : list) {
      if (token.getExpiresAt().compareTo(LocalDateTime.now()) < 0) {
        list.remove(list);
      }
    }
  }

  /**
   * @see ITokenBlacklist#isTokenBlacklisted(String, String)
   */
  @Override
  @Lock(READ)
  public boolean isTokenBlacklisted(String token, String resource) {
    for (BlacklistedToken t : list) {
      if (t.getToken().equals(token)) {
        return true;
      }
    }
    return false;
  }

}
