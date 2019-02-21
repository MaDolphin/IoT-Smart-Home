/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.aggregates.rte;

import de.montigem.be.config.Config;
import org.hibernate.envers.AuditReader;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * TODO
 *
 * @author (last commit) $$Author$$
 * @version $$Revision$$, $$Date$$
 * @since TODO
 */
public abstract class AggregateDao<D extends IAggregate> {

  protected EntityManager em;

  protected AuditReader ar;

  public abstract Class<D> getDomainClass();

  @PersistenceContext(unitName = Config.DOMAIN_DB)
  public void setEntityManager(EntityManager entityManager) {
    this.em = entityManager;
    em.setFlushMode(FlushModeType.AUTO);
  }

  public EntityManager getEntityManager() {
    return this.em;
  }

  public AuditReader getAuditReader() {
    return this.ar;
  }

  /**
   * Retrieves the element with the specified id and the highest revision
   * number. TODO: Is it safe to include class name in query string?
   *
   * @param id
   * @return
   */
  public Optional<D> find(long id) {
    return Optional.ofNullable(em.find(getDomainClass(), id));
  }

  /**
   * Retrieves an element by id and revision.
   *
   * @param id
   * @param revision
   * @return
   */
  public Optional<D> find(long id, long revision) {
    D res = ar.find(getDomainClass(), id, revision);
    return Optional.ofNullable(res);
  }

  /**
   * Retrieves all elements with a given id TODO: Is it safe to embed class name
   * in query string?
   *
   * @return
   */
  public List<D> getAllById(long id) {
    TypedQuery<D> query = em.createQuery(
        "SELECT d FROM " + getDomainClass().getName() + " d WHERE d.id=:id", getDomainClass());
    query.setParameter("id", id);
    return query.getResultList();
  }

}

