/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.auth;

import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class UserActivation {

  @Id
  private String id;
  @OneToOne
  private DomainUser user;
  private String activationKeyHash;
  private String salt;

  protected UserActivation() {
  }

  public UserActivation(String id, DomainUser user, String activationKeyHash, String salt) {
    this.id = id;
    this.user = user;
    this.activationKeyHash = activationKeyHash;
    this.salt = salt;
  }

  public String getId() {
    return id;
  }

  public DomainUser getUser() {
    return user;
  }

  public void setUser(DomainUser user) {
    this.user = user;
  }

  public String getActivationKeyHash() {
    return activationKeyHash;
  }

  public String getSalt() {
    return salt;
  }
}
