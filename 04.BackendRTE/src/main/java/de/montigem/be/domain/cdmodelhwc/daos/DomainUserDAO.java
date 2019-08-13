/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.domain.cdmodelhwc.daos;

import de.montigem.be.auth.UserActivationManager;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.domain.cdmodelhwc.classes.domainuseractivationstatus.DomainUserActivationStatus;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Stateless
public class DomainUserDAO extends DomainUserDAOTOP {

  @Inject
  private UserActivationManager activationManager;

  /**
   * Finds a user by username.
   *
   * @param username
   * @return The found user or null if no user with the given username exists.
   */
  public Optional<DomainUser> find(String username, String resource) {
    router.setDataSource(resource);
    TypedQuery<DomainUser> query = getEntityManager().createQuery(
        "SELECT u FROM DomainUser u WHERE u.username=:username", getDomainClass());
    query.setParameter("username", username);
    try {
      return Optional.of(query.getSingleResult());
    } catch (NoResultException e) {
      Log.debug("no user with username '" + username + "' found!",
          getClass().getName());
      return Optional.empty();
    }
  }



  public Optional<DomainUser> findByEmail(String email, String resource) {
    router.setDataSource(resource);
    TypedQuery<DomainUser> query = getEntityManager().createQuery(
        "SELECT u FROM DomainUser u WHERE u.email=:email", getDomainClass());
    query.setParameter("email", email);
    try {
      return Optional.of(query.getSingleResult());
    } catch (NoResultException e) {
      Log.debug("no user with email '" + email + "' found!",
          getClass().getName());
      return Optional.empty();
    }
  }

  public boolean delete(String username, String resource) {
    Optional<DomainUser> user = find(username, resource);

    router.setDataSource(resource);

    if (user.isPresent()) {
      getEntityManager().remove(user.get());
      getEntityManager().flush();
      return true;
    } else {
      return false;
    }
  }

  public void removeAllExceptAdmin(String resource) {
    for (DomainUser DomainUser : getAll(resource)) {
      update(DomainUser, resource);
    }
    router.setDataSource(resource);
    Query query = getEntityManager().createNativeQuery(""
        + "DELETE FROM DomainUser WHERE username != 'admin' ");
    query.executeUpdate();
  }

  @Transactional
  public DomainUser create(DomainUser user, String resource) {

    // check if username exists already
    if (find(user.getUsername(), resource).isPresent()) {
      return null;
    }

    DomainUser res = super.create(user, resource);

    return res;
  }

  public boolean noUserExistWithEmailAndUserName(String username, String email, String resource) {
    router.setDataSource(resource);
    TypedQuery<DomainUser> query = getEntityManager().createQuery(
        "SELECT a FROM  DomainUser a WHERE a.username = :username AND a.email = :email ",
        DomainUser.class);
    query.setParameter("username", username);
    query.setParameter("email", email);
    return query.getResultList().size() == 0;
  }

  public boolean noUserExistWithEmail(String email, String resource) {
    router.setDataSource(resource);
    TypedQuery<DomainUser> query = getEntityManager().createQuery(
            "SELECT a FROM  DomainUser a WHERE a.email = :email ",
            DomainUser.class);
    query.setParameter("email", email);
    return query.getResultList().size() == 0;
  }

  public boolean noUserExistWithInitials(String initials, String resource) {
    router.setDataSource(resource);
    TypedQuery<DomainUser> query = getEntityManager().createQuery(
            "SELECT a FROM  DomainUser a WHERE a.initials = :initials ",
            DomainUser.class);
    query.setParameter("initials", initials);
    return query.getResultList().size() == 0;
  }

  public boolean noUserExistWithUserName(String username, String resource) {
    router.setDataSource(resource);
    TypedQuery<DomainUser> query = getEntityManager().createQuery(
        "SELECT a FROM  DomainUser a WHERE a.username = :username  ",
        DomainUser.class);
    query.setParameter("username", username);
    return query.getResultList().size() == 0;
  }

  public List<DomainUser> getAllUserWithStatus(DomainUserActivationStatus status, String resource){
    router.setDataSource(resource);
    TypedQuery<DomainUser> query= getEntityManager().createQuery(
            "SELECT a FROM DomainUser a WHERE a.activated = :status",
            DomainUser.class);
    query.setParameter("status", status);
    return query.getResultList();
  }
}
