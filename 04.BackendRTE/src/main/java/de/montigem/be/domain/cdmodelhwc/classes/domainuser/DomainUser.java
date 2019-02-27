
/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved. http://www.se-rwth.de/
 */
package de.montigem.be.domain.cdmodelhwc.classes.domainuser;

import de.montigem.be.auth.jwt.MontiGemSecurityUtils;
import de.montigem.be.domain.cdmodelhwc.classes.domainuseractivationstatus.DomainUserActivationStatus;
import org.apache.shiro.codec.Base64;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Audited
@Entity
public class DomainUser extends DomainUserTOP {

  public DomainUser() {
  }

  public DomainUser(DomainUserActivationStatus activated, boolean enabled, String username, String email,
                    Optional<String> password, ZonedDateTime regDate, Optional<String> initials) {
    setActivated(activated != null ? activated : DomainUserActivationStatus.MAIL_NICHT_GESENDET);
    setEnabled(enabled);
    setUsername(username);
    setEmail(email);
    setPasswordSaltBase64(MontiGemSecurityUtils.generateSaltAsBase64());
    setEncodedPassword(Optional.of(MontiGemSecurityUtils
            .encodePasswordOptional(password, Base64.decode(this.getPasswordSaltBase64()))));
    setRegistrationDate(regDate);
    setInitials(initials);
  }

  @Override
  public DomainUser rawInitAttrs(List<String> labels, String username, Optional<String> encodedPassword, String passwordSaltBase64, ZonedDateTime registrationDate, Optional<String> initials, DomainUserActivationStatus activated, boolean enabled, String email, Optional<String> timID) {
    return super.rawInitAttrs(labels, username, encodedPassword, passwordSaltBase64, registrationDate, initials, activated != null ? activated : DomainUserActivationStatus.MAIL_NICHT_GESENDET, enabled, email, timID);
  }
}
