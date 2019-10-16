/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.auth.jwt.blacklist;

import de.montigem.be.config.Config;
import de.montigem.be.database.DatabaseRouter;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class DatabaseTokenBlacklist implements ITokenBlacklist {

  private EntityManager em;

  @PersistenceContext(unitName = Config.DOMAIN_DB)
  public void setEntityManager(EntityManager entityManager) {
    this.em = entityManager;
    em.setFlushMode(FlushModeType.AUTO);
  }

  @Resource(name = "DBRouter", type = DatabaseRouter.class)
  protected DatabaseRouter router;

  @Override
  public void addToken(BlacklistedToken token, String resource) {
    router.setDataSource(resource);
    em.persist(token);
    em.flush();
  }

  @Override
  public void removeOutdatedTokens(String resource) {
    router.setDataSource(resource);
    TypedQuery<BlacklistedToken> query = em.createQuery("SELECT t FROM BlacklistedToken t",
        BlacklistedToken.class);
    for (BlacklistedToken token : query.getResultList()) {
      if (token.getExpiresAt().compareTo(LocalDateTime.now()) < 0) {
        em.remove(token);
        em.flush();
      }
    }
  }

  @Override
  public boolean isTokenBlacklisted(String token, String resource) {
    // TypedQuery<BlacklistedToken> query = em.createQuery("SELECT t FROM BlacklistedToken t WHERE
    // t.token=:token", BlacklistedToken.class);
    // query.setParameter("token", token);
    // try {
    // query.getSingleResult();
    // return true;
    // }
    // catch (NoResultException e) {
    // return false;
    // }

    router.setDataSource(resource);
    CriteriaQuery<BlacklistedToken> cq = (CriteriaQuery) em.getCriteriaBuilder()
        .createQuery(BlacklistedToken.class);
    cq.select(cq.from(BlacklistedToken.class));
    List<BlacklistedToken> resultList = em.createQuery(cq).getResultList();
    for (BlacklistedToken t : resultList) {
      if (t.getToken().equals(token)) {
        return true;
      }
    }
    return false;
  }
}
