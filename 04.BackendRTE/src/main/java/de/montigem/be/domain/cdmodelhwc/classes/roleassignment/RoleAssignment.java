package de.montigem.be.domain.cdmodelhwc.classes.roleassignment;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Roles;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Optional;

@Audited
@Entity
public class RoleAssignment extends RoleAssignmentTOP {


  public RoleAssignment() {
  }

  public RoleAssignment(String role, DomainUser user, String objClass, Long objId, String attribute) {
    super();
    rawInitAttrs(new ArrayList<>(), role, objClass, Optional.ofNullable(objId), Optional.ofNullable(attribute), user);
  }

  public RoleAssignment(Roles role, DomainUser user, ObjectClasses objClass, Long objId, String attribute) {
    super();
    rawInitAttrs(new ArrayList<>(), role.getIdentifier(), objClass.getIdentifier(), Optional.ofNullable(objId), Optional.ofNullable(attribute), user);
  }

  public RoleAssignment(String role, DomainUser user, ObjectClasses objClass, Long objId, String attribute) {
    super();
    rawInitAttrs(new ArrayList<>(), role, objClass.getIdentifier(), Optional.ofNullable(objId), Optional.ofNullable(attribute), user);
  }

  public RoleAssignmentProxy toProxy() {
    return new RoleAssignmentProxy(this);
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof RoleAssignment) {
      return ((RoleAssignment) o).getId() == this.getId();
    }
    return false;
  }

}
