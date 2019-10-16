/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.auth.jwt;

import java.time.ZonedDateTime;

/**
 * TODO: Write me!
 *
 *
 * @author  (last commit) $Author$
 * @version $Date$<br>
 *          $Revision$
 */
public class ExtendedJWT {

  private String jwt;
  private String refreshToken;
  private ZonedDateTime expirationDate;
  
  public ExtendedJWT(String jwt, String refreshToken, ZonedDateTime expirationDate) {
    this.jwt = jwt;
    this.refreshToken = refreshToken;
    this.expirationDate = expirationDate;
  }

  public String getJwt() {
    return this.jwt;
  }

  public void setJwt(String jwt) {
    this.jwt = jwt;
  }

  public String getRefreshToken() {
    return this.refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public ZonedDateTime getExpirationDate() {
    return this.expirationDate;
  }

  public void setExpirationDate(ZonedDateTime validUntil) {
    this.expirationDate = validUntil;
  }
}
