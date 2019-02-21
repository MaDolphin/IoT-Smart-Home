package de.montigem.be.auth.jwt.blacklist;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BlacklistedToken {
  
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private long id;
  
  private String token;
  
  private LocalDateTime addedAt;
  
  private LocalDateTime expiresAt;
  
  protected BlacklistedToken() {
  }
  
  public BlacklistedToken(String token, LocalDateTime expiresAt) {
    this.token = token;
    this.addedAt = LocalDateTime.now();
    this.expiresAt = expiresAt;
  }
  
  public String getToken() {
    return token;
  }
  
  public LocalDateTime getAddedAt() {
    return addedAt;
  }
  
  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }
}
