/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.auth;

import de.montigem.be.domain.cdmodelhwc.classes.domainuseractivationstatus.DomainUserActivationStatus;

import java.time.ZonedDateTime;

public interface IComplexUser extends IUser {
  
  String getEmail();
  boolean isEnabled();
  DomainUserActivationStatus isActivated();
  void setEnabled(boolean enabled);
  void setActivated(DomainUserActivationStatus activated);
  ZonedDateTime getRegistrationDate();
  String getTimID();
  void setTimID(String timID);
  String getInitials();
}
