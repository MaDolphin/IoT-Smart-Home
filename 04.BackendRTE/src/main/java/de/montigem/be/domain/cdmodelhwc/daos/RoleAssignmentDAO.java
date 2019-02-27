package de.montigem.be.domain.cdmodelhwc.daos;


import de.montigem.be.authz.Roles;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignment;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

/**
 * Via this point, permissions, roles and assignments can be created, altered
 * and removed.
 *
 * @author Philipp Kehrbusch
 */
@Stateless
@Lock(LockType.READ)
public class RoleAssignmentDAO extends RoleAssignmentDAOTOP {

  public void deleteRoleAssignmentsForUser(long userId) {
    Collection<RoleAssignment> assignments = getRoleAssignments(userId);
    for (RoleAssignment assignment : assignments) {
      getEntityManager().remove(assignment);
    }
    getEntityManager().flush();
  }

  public void deleteRoleAssignments(long userId, String objClass, long objId) {
    Collection<RoleAssignment> assignments = getRoleAssignments(userId, objClass, objId);
    for (RoleAssignment assignment : assignments) {
      getEntityManager().remove(assignment);
    }
    getEntityManager().flush();
  }

  public void deleteRoleAssignmentsByAccountId(long objId) {
    Collection<RoleAssignment> assignments = getRoleAssignmentsByAccountId(objId);
    for(RoleAssignment assignment : assignments){
      getEntityManager().remove(assignment);
    }
  }

  public void removeRoleFromUser(long userId, Roles role) {
    getRoleAssignments(userId).stream().filter(a -> a.getRole().equals(role.getIdentifier())).forEach(a -> getEntityManager().remove(a));
  }

  /**
   * Retrieves role assignments for a user by user id
   *
   * @param userId Id of the user to retrieve role assignments for
   * @return
   */
  public Collection<RoleAssignment> getRoleAssignments(long userId) {
    TypedQuery<RoleAssignment> query = getEntityManager().createQuery("SELECT a FROM RoleAssignment a " +
            "WHERE a.user.id=:id", RoleAssignment.class);
    query.setParameter("id", userId);
    return query.getResultList();
  }

  public void removeAllExceptFromAdmin(){
    TypedQuery<DomainUser> userQuery = getEntityManager().createQuery("SELECT a FROM DomainUser a WHERE a.username = 'admin' ", DomainUser.class);
    DomainUser admin = userQuery.getSingleResult();
    Query query = getEntityManager().createNativeQuery(""
        + "DELETE FROM RoleAssignment WHERE user_id!=:id ");
    query.setParameter("id", admin.getId());
    query.executeUpdate();
  }

  public boolean assigmentNotExists(long userId, long kontoid, String role){
    TypedQuery<RoleAssignment> query = getEntityManager().createQuery("SELECT ra FROM RoleAssignment ra WHERE " +
            "ra.user.id=:id AND ra.objId=:objid AND ra.role=:role", RoleAssignment.class);
    query.setParameter("id", userId);
    query.setParameter("objid", kontoid);
    query.setParameter("role",role);
    return query.getResultList().size()==0;
  }
  public Collection<RoleAssignment> getRoleAssignments(long userId, String objClass) {
    TypedQuery<RoleAssignment> query = getEntityManager().createQuery("SELECT ra FROM RoleAssignment ra WHERE " +
            "ra.user.id=:id AND ra.objClass=:objClass", RoleAssignment.class);
    query.setParameter("id", userId);
    query.setParameter("objClass", objClass);
    return query.getResultList();
  }

  public Collection<RoleAssignment> getRoleAssignments(long userId, String objClass, long objId) {
    TypedQuery<RoleAssignment> query = getEntityManager().createQuery("SELECT ra FROM RoleAssignment ra WHERE " +
            "ra.user.id=:id AND ra.objClass=:objClass AND ra.objId=:objId", RoleAssignment.class);
    query.setParameter("id", userId);
    query.setParameter("objClass", objClass);
    query.setParameter("objId", objId);
    return query.getResultList();
  }

  public Collection<RoleAssignment> getRoleAssignments(long userId, String objClass, long objId, String attribute) {
    TypedQuery<RoleAssignment> query = getEntityManager().createQuery("SELECT ra FROM RoleAssignment ra WHERE " +
                    "ra.user.id=:id AND ra.objClass=:objClass AND ra.objId=:objId AND ra.attribute=:attribute",
            RoleAssignment.class);
    query.setParameter("id", userId);
    query.setParameter("objClass", objClass);
    query.setParameter("objId", objId);
    query.setParameter("attribute", attribute);
    return query.getResultList();
  }

  public Collection<RoleAssignment> getRoleAssignmentsByAccountId(long objId) {
    TypedQuery<RoleAssignment> query = getEntityManager().createQuery("SELECT ra FROM RoleAssignment ra WHERE "
        + "ra.objId=:objId", RoleAssignment.class);
    query.setParameter("objId", objId);
    return query.getResultList();
  }

  public List<RoleAssignment> getRoleAssignmentsByUsername(String username, String resource) {
    router.setDataSource(resource);
    TypedQuery<RoleAssignment> query = getEntityManager().createQuery("SELECT a FROM RoleAssignment a " +
        "WHERE a.user.username=:username", RoleAssignment.class);
    query.setParameter("username", username);
    return query.getResultList();
  }
}
