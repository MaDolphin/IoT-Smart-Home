/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.domain.rte.dao;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.rte.interfaces.IObject;
import de.montigem.be.util.DAOLib;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QueryRestriction<D extends IObject, T extends AbstractDomainDAO<D>> {
  private final List<Predicate> predicates;
  private int offset = 0;
  private int limit = -1;
  private T dao;
  private CriteriaQuery<D> cq;
  private Root<? extends D> root;
  private CriteriaBuilder cb;

  public QueryRestriction(T dao) {
    this.predicates = new ArrayList<>();
    this.dao = dao;

    this.cb = dao.getCriteriaBuilder();
    this.cq = dao.getCriteriaQuery();
    this.root = dao.getCriteriaRoot(this.cq);
    this.cq.select(this.root);
  }

  public QueryRestriction(List<Predicate> predicateList) {
    this.predicates = predicateList;
  }

  public QueryRestriction<D, T> addPredicate(Predicate p) {
    this.predicates.add(p);
    return this;
  }

  public QueryRestriction<D, T> addPredicateOfDTYPE(Class<? extends D> type) {
    this.predicates.add(dao.getCriteriaBuilder().equal(dao.getCriteriaRoot().type(), dao.getCriteriaBuilder().literal(type)));
    return this;
  }

  public QueryRestriction<D, T> addPredicateNotOfDTYPE(Class<? extends D> type) {
    this.predicates.add(dao.getCriteriaBuilder().notEqual(dao.getCriteriaRoot().type(), dao.getCriteriaBuilder().literal(type)));
    return this;
  }

  public QueryRestriction<D, T> addPredicateInIds(Set<Long> ids) {
    if (!ids.contains(-1L)) {
      this.predicates.add(dao.getCriteriaRoot().get("id").in(ids));
    }
    return this;
  }

  public QueryRestriction<D, T> addPredicateInIds(Set<Long> ids, String columnName) {
    if (!ids.contains(-1L)) {
      this.predicates.add(dao.getCriteriaRoot().get(columnName).in(ids));
    }
    return this;
  }

  /*
    public QueryRestriction<D, T> addPredicatesFromRestriction(AttributeRestriction<T> restriction, String columnName) {
      this.predicates.addAll(restriction.createPredicates(this.cb, this.getRoot().get(columnName)));
      return this;
    }
  */
  public QueryRestriction<D, T> addPredicatePermitted(DAOLib daoLib, SecurityHelper securityHelper,
      String objectClass, String... permissions) {
    return addPredicateInIds(securityHelper.getPermittedIds(daoLib, objectClass, permissions));
  }

  public QueryRestriction<D, T> addPredicatePermitted(String columnName, DAOLib daoLib, SecurityHelper securityHelper,
      String objectClass, String... permissions) {
    return addPredicateInIds(securityHelper.getPermittedIds(daoLib, objectClass, permissions), columnName);
  }

  public QueryRestriction<D, T> addPredicateIsActive(boolean isActive) {
    this.predicates.add(dao.getCriteriaBuilder().equal(dao.getCriteriaRoot().get("istAktiv"), isActive));
    return this;
  }

  public QueryRestriction<D, T> setOffset(int offset) {
    this.offset = offset;
    return this;
  }

  public QueryRestriction<D, T> setLimit(int limit) {
    this.limit = limit;
    return this;
  }

  public Predicate[] getPredicatesAsArray() {
    return this.predicates.toArray(new Predicate[this.predicates.size()]);
  }

  public int getOffset() {
    return this.offset;
  }

  public int getLimit() {
    return this.limit;
  }

  public CriteriaQuery<D> getCriteriaQuery() {
    return cq;
  }

  public Root<? extends D> getRoot() {
    return root;
  }

  public CriteriaBuilder getCriteriaBuilder() {
    return cb;
  }
}
