/*Generated file from MCGenerator*/
package de.montigem.be.domain.cdmodelhwc.daos;

import java.util.*;
import java.util.stream.Collectors;
import de.montigem.be.domain.cdmodelhwc.classes.adapter.*;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import de.se_rwth.commons.logging.Log;

/**
 * Adapter query methods.
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
@Stateless
@Lock(LockType.WRITE)
public class AdapterDAO extends AdapterDAOTOP {

	/**
	 * Finds an Adapter with the given identifier. Note: The identifier is not the
	 * Adapter ID, so it is not replaceable by findById().
	 * 
	 * @identifier identifier to search for
	 * @resource database
	 * @return Adapter with identifier if present, else empty.
	 */
	public Optional<Adapter> findByIdentifier(String identifier, String resource) {
		router.setDataSource(resource);
		TypedQuery<Adapter> query = getEntityManager()
				.createQuery("SELECT a FROM Adapter a WHERE a.identifier=:identifier", getDomainClass());
		query.setParameter("identifier", identifier);
		try {
			Adapter adapter = query.getSingleResult();
			return Optional.of(adapter);
		} catch (NoResultException e) {
			Log.debug("No adapter with identifier '" + identifier + "' found!", getClass().getName());
			return Optional.empty();
		}
	}

	/**
	 * Finds all Adapters with the given groupName. If the adapter groupName is not
	 * up to date and a false positive is found, the groupName of all adapters is
	 * updated. More efficient than updateGroup, but may not find Adapters that are
	 * out of date.
	 * 
	 * @identifier groupName to search for
	 * @resource database
	 * @return Adapter with given groupName if present, else empty.
	 */
	public List<Adapter> findByGroup(String identifier, String resource) {
		router.setDataSource(resource);
		TypedQuery<Adapter> query = getEntityManager()
				.createQuery("SELECT a FROM Adapter a WHERE a.groupName=:identifier", getDomainClass());
		query.setParameter("identifier", identifier);
		List<Adapter> adapters = new ArrayList<>();
		try {
			adapters = query.getResultList();
		} catch (NoResultException e) {
			Log.debug("No adapter with identifier '" + identifier + "' found!", getClass().getName());
		}
		for (Adapter adapter : adapters) {
			if (!adapter.getGroupName().equals(identifier)) {
				return updateGroup(identifier, resource);
			}
		}
		return adapters;
	}

	/**
	 * Updates the groupName of all Adapters. Returns all Adapters in the given
	 * group.
	 * 
	 * @group groupName to search for
	 * @resource database
	 * @return Adapters with given groupName if present, else empty.
	 */
	public List<Adapter> updateGroup(String group, String resource) {
		List<Adapter> adaptersInGroup = new LinkedList<>();
		List<Adapter> adapters = getAll(resource);
		for (Adapter a : adapters) {
			if (!a.getGroup().equals(a.getGroupName())) {
				a.updateGroup();
				update(a, resource);
			}
			if (a.getGroup().equals(group)) {
				adaptersInGroup.add(a);
			}
		}
		return adaptersInGroup;
	}

	/**
	 * Finds all classes with the given groupName in the given attribute.
	 * 
	 * @identifier groupName to search for
	 * @attribute simple attribute name that is checked for the groupName
	 * @resource database
	 * @resultClass class that is returned and that contains given attribute
	 * @return classes that contain the groupName in the given attribute
	 */
	public <T> List<T> findByGroup(String identifier, String attribute, String resource, Class<T> resultClass) {
		router.setDataSource(resource);
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> cq = builder.createQuery(resultClass);
		Root<T> r = cq.from(resultClass);
		cq.select(r);
		cq.where(builder.equal(r.get(attribute), identifier));

		TypedQuery<T> query = getEntityManager().createQuery(cq);
		List<T> objs = new ArrayList<>();
		try {
			objs = query.getResultList();
		} catch (NoResultException e) {
			Log.debug("No adapter with identifier '" + identifier + "' found!", getClass().getName());
		}
		return objs;
	}

	/**
	 * Finds all Adapters accessing an attribute of a specific entity.
	 * 
	 * @classId class id containing the attribute
	 * @attribute simple attribute name
	 * @resource database
	 * @return all Adapters that could write or read the attribute of the given
	 *         entity
	 */
	public List<Adapter> findByAttribute(long classId, String attribute, String resource) {
		router.setDataSource(resource);
		TypedQuery<Adapter> query = getEntityManager()
				.createQuery("SELECT a FROM Adapter a WHERE a.attributeName=:attribute", getDomainClass());
		query.setParameter("attribute", attribute);
		List<Adapter> adapters = new ArrayList<>();
		try {
			adapters = query.getResultList().stream().filter(adapter -> adapter.getAttributeClass().getId() == classId)
					.collect(Collectors.toList());
		} catch (NoResultException e) {
			Log.debug("No adapter for classId '" + classId + "' and attributeName'" + attribute + "' found!",
					getClass().getName());
		}
		return adapters;
	}

	/**
	 * Deletes Adapter with given id.
	 * 
	 * @id id of adapter that is deleted
	 * @resource database
	 */
	public void deleteById(long id, String resource) {
		router.setDataSource(resource);
		Query query = getEntityManager().createQuery("DELETE Adapter a WHERE a.id=:id");
		query.setParameter("id", id);
		query.executeUpdate();
	}
}
