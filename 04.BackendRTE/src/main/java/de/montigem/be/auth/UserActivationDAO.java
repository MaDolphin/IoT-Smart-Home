package de.montigem.be.auth;

import de.montigem.be.auth.jwt.MontiGemSecurityUtils;
import de.montigem.be.config.Config;
import de.montigem.be.database.DatabaseRouter;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.domain.cdmodelhwc.daos.DomainUserDAO;
import org.apache.shiro.codec.Base64;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class UserActivationDAO {

  private EntityManager em;

  @Inject
  private DomainUserDAO userDao;

  @PersistenceContext(unitName = Config.DOMAIN_DB)
  public void setEntityManager(EntityManager entityManager) {
    this.em = entityManager;
    em.setFlushMode(FlushModeType.AUTO);
  }

  @Resource(name = "DBRouter", type = DatabaseRouter.class)
  protected DatabaseRouter router;

  public UserActivation addActivation(UserActivation activation, String resource) {
    router.setDataSource(resource);
    UserActivation res = em.merge(activation);
    em.flush();
    return res;
  }

  /**
   * Verifies an activation key
   *
   * @param id  The activation key's id
   * @param key The activation key's value
   * @return The activation key, if an activation key with id <i>id</id> and
   * value <i>keyHash</i> exists, null otherwise.
   */
  public UserActivation checkActivationKey(UUID id, String key, String resource) {
    router.setDataSource(resource);
    UserActivation activation = em.find(UserActivation.class, id.toString());
    if (activation != null) {
      String keyHash = MontiGemSecurityUtils.encodePassword(key, Base64.decode(activation.getSalt()));
      if (activation.getActivationKeyHash().equals(keyHash)) {
        return activation;
      }
    }
    return null;
  }

  /**
   * Delete Activation keys by user
   *
   * @param username
   */
  public void deleteActivationKeysByUser(String username, String resource) {
    router.setDataSource(resource);
    TypedQuery<UserActivation> query = em
        .createQuery("select u from UserActivation u where u.user.username=:username",
            UserActivation.class);
    query.setParameter("username", username);
    query.getResultList().forEach(u -> {
      em.remove(u);
    });
  }

  public UserActivation getUserActivationById(UUID id, String resource) {
    router.setDataSource(resource);
    return em.find(UserActivation.class, id.toString());
  }

  public void deleteActivationKey(UUID id, String resource) {
    router.setDataSource(resource);
    UserActivation activation = em.find(UserActivation.class, id.toString());
    if (activation != null) {
      activation.setUser(null);
      em.remove(activation);
    }
  }

  public void deleteKeyForUser(String username, String resource) {
    Optional<DomainUser> user = userDao.find(username, resource);
    if (!user.isPresent()) {
      return;
    }

    TypedQuery<UserActivation> query = em
        .createQuery("SELECT a FROM UserActivation a WHERE a.user.username=:username",
            UserActivation.class);
    query.setParameter("username", username);

    try {
      UserActivation activation = query.getSingleResult();
      activation.setUser(null);
      em.remove(activation);
    } catch (NoResultException e) {
      // do nothing here
    }
  }
}
