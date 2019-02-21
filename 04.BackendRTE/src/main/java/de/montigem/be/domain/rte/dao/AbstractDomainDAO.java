/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.domain.rte.dao;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.config.Config;
import de.montigem.be.database.DatabaseRouter;
import de.montigem.be.domain.rte.interfaces.IObject;
import de.montigem.be.util.DAOLib;
import de.montigem.be.util.SubListHelper;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import javax.annotation.Resource;
import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.*;

public abstract class AbstractDomainDAO<D extends IObject> {

  protected EntityManager em;

  private AuditReader ar;

  @Resource(name = "DBRouter", type = DatabaseRouter.class)
  protected DatabaseRouter router;

  public abstract Class<D> getDomainClass();

  @PersistenceContext(unitName = Config.DOMAIN_DB)
  public void setEntityManager(EntityManager entityManager) {
    this.em = entityManager;
    em.setFlushMode(FlushModeType.AUTO);
    this.ar = AuditReaderFactory.get(this.em);
  }

  public EntityManager getEntityManager() {
    return em;
  }

  public CriteriaBuilder getCriteriaBuilder() {
    return em.getCriteriaBuilder();
  }

  public CriteriaQuery<? extends IObject> getCriteriaQuery() {
    return getCriteriaBuilder().createQuery(getDomainClass());
  }

  public Root<? extends IObject> getCriteriaRoot() {
    return getCriteriaQuery().from(getDomainClass());
  }

  @Transactional
  public <T> TypedQuery<T> createQuery(String query, Class<T> clazz) {
    return em.createQuery(query, clazz);
  }

  @Transactional
  public D create(D d, String resource) {
    router.setDataSource(resource);
    em.flush();
    D obj = em.merge(d);
    em.flush();
    return obj;
  }

  @Transactional(Transactional.TxType.REQUIRES_NEW)
  public D createWithNewSession(D d, String resource) {
    router.setDataSource(resource);
    em.flush();
    D obj = em.merge(d);
    em.flush();
    return obj;
  }

  @Transactional
  public D createAndLoad(D d, DAOLib daoLib, String resource) throws EntityNotFoundException {
    D obj = create(d, resource);

    return loadEager(obj, daoLib, resource);
  }

  /**
   * Deletes all items with a given id
   *
   * @param id
   * @return
   */
  @Transactional
  public boolean delete(long id, String resource) throws NoSuchElementException {
    List<D> objs = getAllById(id, resource);

    if (objs.isEmpty()) {
      throw new NoSuchElementException(
          "Löschen nicht möglich. Element kann in der Datenbank nicht gefunden werden.");
    }

    router.setDataSource(resource);

    for (D obj : objs) {
      em.remove(obj);
      em.flush();
    }
    return true;
  }

  /**
   * Deletes all items with a given id
   *
   * @param id
   * @return
   */
  @Transactional
  public boolean delete(long id, boolean force, String resource) throws NoSuchElementException {
    if (force) {
      List<D> objs = getAllById(id, resource);

      if (objs.isEmpty()) {
        throw new NoSuchElementException(
            "Löschen nicht möglich. Element kann in der Datenbank nicht gefunden werden.");
      }

      router.setDataSource(resource);

      for (D obj : objs) {
        em.remove(obj);
        em.flush();
      }
      return true;
    }
    else {
      return delete(id, resource);
    }
  }

  @Transactional
  public Optional<D> update(D newObject, String resource) {
    Optional<D> obj = find(newObject.getId(), resource);
    if (obj.isPresent()) {
      router.setDataSource(resource);
      obj.get().merge(newObject);
      em.merge(obj.get());
      em.flush();
    }
    return obj;
  }

  @Transactional
  public Optional<D> updateAndLoad(D newObject, DAOLib daoLib, String resource) {
    Optional<D> obj = findAndLoad(newObject.getId(), daoLib, resource);
    if (obj.isPresent()) {
      obj.get().merge(newObject);
      em.merge(obj.get());
      em.flush();
    }
    return obj;
  }

  @Transactional
  public Optional<D> updateWithoutAssociations(D newObject, String resource) {
    Optional<D> obj = find(newObject.getId(), resource);
    if (obj.isPresent()) {
      router.setDataSource(resource);
      obj.get().mergeWithoutAssociations(newObject);
      em.merge(obj.get());
      em.flush();
    }
    return obj;
  }

  /**
   * Retrieves the element with the specified id and the highest revision
   * number. TODO: Is it safe to include class name in query string?
   *
   * @param id
   * @return
   */
  @Transactional
  public Optional<D> find(long id, String resource) {
    router.setDataSource(resource);
    return Optional.ofNullable(em.find(getDomainClass(), id));
  }

  /**
   * Retrieves an element by id and revision.
   *
   * @param id
   * @param revision
   * @return
   */
  @Transactional
  public Optional<D> find(long id, long revision, String resource) {
    router.setDataSource(resource);
    D res = ar.find(getDomainClass(), id, revision);
    return Optional.ofNullable(res);
  }

  @Transactional
  public Optional<D> findAndLoad(long id, DAOLib daoLib, SecurityHelper securityHelper) {
    return findAndLoad(id, daoLib, securityHelper.getSessionCompliantResource());
  }

  @Transactional
  public Optional<D> findAndLoad(long id, DAOLib daoLib, String resource) {
    Optional<D> p = find(id, resource);
    if (!p.isPresent()) {
      return p;
    }

    return Optional.of(loadEager(p.get(), daoLib, resource));
  }

  @Transactional
  public D loadEager(D d, DAOLib daoLib, String resource) {
    router.setDataSource(resource);
    return d;
  }

  @Transactional
  public D loadEager(D d, DAOLib daoLib, int hierarchyLevel, String resource) {
    router.setDataSource(resource);
    return d;
  }

  /**
   * Retrieves all elements with a given id TODO: Is it safe to embed class name
   * in query string?
   *
   * @return
   */
  @Transactional
  public List<D> getAllById(long id, String resource) {
    router.setDataSource(resource);
    TypedQuery<D> query = em.createQuery(
        "SELECT d FROM " + getDomainClass().getName() + " d WHERE d.id=:id", getDomainClass());
    query.setParameter("id", id);
    return query.getResultList();
  }

  @Transactional
  public List<D> getAllWithRestriction(QueryRestriction restriction, String resource) {
    router.setDataSource(resource);
    CriteriaQuery<D> cq = em.getCriteriaBuilder().createQuery(getDomainClass());
    cq.select(cq.from(getDomainClass()));
    cq.where(restriction.getPredicatesAsArray());

    TypedQuery<D> tq = em.createQuery(cq);
    tq.setFirstResult(restriction.getOffset());
    if (restriction.getLimit() != -1) {
      tq.setMaxResults(restriction.getLimit());
    }
    return tq.getResultList();
  }

  @Transactional
  public List<D> loadAllWithRestriction(QueryRestriction restriction, DAOLib daoLib, String resource) {
    List<D> list = getAllWithRestriction(restriction, resource);
    list.forEach(o -> loadEager(o, daoLib, resource));
    return list;
  }

  @Transactional
  public List<D> getAll(String resource) {
    return getAll(-1, -1, resource);
  }

  @Transactional
  public List<D> getAll(int first, int max, String resource) {
    router.setDataSource(resource);
    CriteriaQuery<D> cq = em.getCriteriaBuilder().createQuery(getDomainClass());
    cq.select(cq.from(getDomainClass()));
    List<D> resultList = em.createQuery(cq).getResultList();
    return SubListHelper.get(resultList, first, max);
  }

  @Transactional
  public List<D> getAllInIds(String resource, Set<Long> ids) {
    if (ids.isEmpty()) {
      return new ArrayList<>();
    }

    router.setDataSource(resource);
    CriteriaQuery<D> cq = em.getCriteriaBuilder().createQuery(getDomainClass());
    Root<D> criteriaRoot = cq.from(getDomainClass());
    cq.select(criteriaRoot);

    if (!ids.contains(-1L)) {
      List<Long> idList = new ArrayList<>(ids);
      Expression<Long> exp = criteriaRoot.get("id");
      Predicate pred = exp.in(idList);
      cq.where(pred);
    }

    return em.createQuery(cq).getResultList();
  }

  @Transactional
  public List<D> getAllPermitted(DAOLib daoLib, SecurityHelper securityHelper, ObjectClasses objectClass, String... permissions) {
    return getAllInIds(securityHelper.getSessionCompliantResource(), securityHelper.getPermittedIds(daoLib, objectClass.getIdentifier(), permissions));
  }

  @Transactional
  public List<D> loadAllPermitted(DAOLib daoLib, SecurityHelper securityHelper, ObjectClasses objectClass, String... permissions) {
    return loadAllInIds(daoLib, securityHelper.getSessionCompliantResource(), securityHelper.getPermittedIds(daoLib, objectClass.getIdentifier(), permissions));
  }

  @Transactional
  public List<D> loadAllInIds(DAOLib daoLib, String resource, Set<Long> ids) {
    List<D> os = getAllInIds(resource, ids);

    for (D o : os) {
      loadEager(o, daoLib, resource);
    }
    return os;
  }

  @Transactional
  public List<D> loadAll(DAOLib daoLib, String resource) {
    List<D> list = getAll(resource);
    list.forEach(o -> loadEager(o, daoLib, resource));
    return list;
  }

  @Transactional
  public int size(String resource) {
    return getAll(-1, -1, resource).size();
  }

  @Transactional
  public void removeAll(String resource) {
    router.setDataSource(resource);
    CriteriaQuery<D> cq = em.getCriteriaBuilder().createQuery(getDomainClass());
    cq.select(cq.from(getDomainClass()));
    List<D> resultList = em.createQuery(cq).getResultList();
    for (D k : resultList) {
      em.remove(k);
    }
  }

  @Transactional
  public boolean exists(long id, String resource) {
    router.setDataSource(resource);
    try {
      return em.getReference(Entity.class, id) != null;
    } catch (EntityNotFoundException e) {
      return false;
    }
  }

  @Transactional
  public <T> List<T> runQuery(TypedQuery<T> query, SecurityHelper securityHelper) {
    router.setDataSource(securityHelper.getSessionCompliantResource());
    return query.getResultList();
  }

}
